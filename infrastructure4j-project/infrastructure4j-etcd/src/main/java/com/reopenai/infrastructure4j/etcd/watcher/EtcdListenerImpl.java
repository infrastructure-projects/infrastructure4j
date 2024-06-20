package com.reopenai.infrastructure4j.etcd.watcher;

import com.reopenai.infrastructure4j.etcd.utils.MethodHandleUtil;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.watch.WatchEvent;
import io.etcd.jetcd.watch.WatchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.core.ParameterizedTypeReference;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 基于MethodHandle实现的Watch.Listener
 *
 * @author Allen Huang
 */
public class EtcdListenerImpl implements Watch.Listener {

    private final Invoker invoker;

    private final Logger logger;

    public EtcdListenerImpl(Object bean, Method method) throws BeansException {
        this.invoker = Invoker.create(bean, method);
        this.logger = LoggerFactory.getLogger(method.getDeclaringClass());
    }

    @Override
    public void onNext(WatchResponse response) {
        try {
            invoker.invoke(response);
        } catch (Throwable e) {
            logger.error("[ETCD][Listener]接收事件出错", e);
        }
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onCompleted() {

    }

    /**
     * 执行器
     */
    private interface Invoker {


        void invoke(WatchResponse response) throws Throwable;

        static Invoker create(Object bean, Method method) throws RuntimeException {
            try {
                Parameter[] parameters = method.getParameters();
                if (parameters.length != 1) {
                    throw new IllegalArgumentException("The number of parameters of @EtcdListener method must be equal to 1");
                }
                MethodHandle methodHandle = MethodHandleUtil.findVirtual(method).bindTo(bean);
                Parameter parameter = parameters[0];
                Type type = parameter.getParameterizedType();
                if (type.equals(WatchResponse.class)) {
                    return new ResponseInvoker(methodHandle);
                } else if (type.equals(WatchEvent.class)) {
                    return new EventInvoker(methodHandle);
                } else if (type.equals(EventsInvoker.TYPE)) {
                    return new EventsInvoker(methodHandle);
                }
                throw new IllegalArgumentException("@EtcdListener方法只支持 WatchResponse/List<WatchEvent>/WatchEvent 三种类型的参数");
            } catch (Throwable e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

    }


    private record EventInvoker(MethodHandle methodHandle) implements Invoker {

        @Override
        public void invoke(WatchResponse response) throws Throwable {
            List<WatchEvent> events = response.getEvents();
            for (WatchEvent event : events) {
                methodHandle.invoke(event);
            }
        }

    }


    private record EventsInvoker(MethodHandle methodHandle) implements Invoker {

        static final Type TYPE = new ParameterizedTypeReference<List<WatchEvent>>() {
        }.getType();

        @Override
        public void invoke(WatchResponse response) throws Throwable {
            List<WatchEvent> events = response.getEvents();
            methodHandle.invokeWithArguments(events);
        }

    }

    private record ResponseInvoker(MethodHandle methodHandle) implements Invoker {

        @Override
        public void invoke(WatchResponse response) throws Throwable {
            methodHandle.invoke(response);
        }
    }

}
