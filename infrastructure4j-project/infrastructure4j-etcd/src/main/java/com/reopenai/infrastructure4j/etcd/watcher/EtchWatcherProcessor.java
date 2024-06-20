package com.reopenai.infrastructure4j.etcd.watcher;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.WatchOption;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * EtchWatcherProcessor
 *
 * @author Allen Huang
 */
@RequiredArgsConstructor
public class EtchWatcherProcessor implements ApplicationContextAware, CommandLineRunner, Ordered, DisposableBean {

    private final Client client;

    private final List<Watch.Watcher> watchers = new LinkedList<>();

    private ApplicationContext applicationContext;

    @Override
    public void run(String... args) throws Exception {
        // 更希望等系统一切都就绪的时候再启动监听程序,所以才使用CommandLineRunner hook
        Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(EtcdComponent.class);
        Environment environment = applicationContext.getEnvironment();
        for (Object bean : beanMap.values()) {
            Class<?> clazz = AopUtils.getTargetClass(bean);
            if (clazz.isAnnotationPresent(EtcdComponent.class)) {
                invokeEtcdListenerInit(environment, clazz, bean);
                createEtcdListener(environment, clazz, bean);
            }
        }
    }

    private void invokeEtcdListenerInit(Environment environment, Class<?> clazz, Object bean) throws Exception {
        Map<Method, EtcdListener.InitHook> annotatedMethods = MethodIntrospector.selectMethods(clazz,
                (MethodIntrospector.MetadataLookup<EtcdListener.InitHook>) method -> AnnotatedElementUtils.findMergedAnnotation(method, EtcdListener.InitHook.class)
        );
        KV kvClient = client.getKVClient();
        for (Map.Entry<Method, EtcdListener.InitHook> entry : annotatedMethods.entrySet()) {
            Method method = entry.getKey();
            EtcdListener.InitHook initHook = entry.getValue();
            String[] keys = initHook.value();
            for (String key : keys) {
                String rawKey = environment.resolvePlaceholders(key);
                ByteSequence queryKey = ByteSequence.from(rawKey, StandardCharsets.UTF_8);
                if (initHook.async()) {
                    kvClient.get(queryKey).thenCompose(response -> {
                        try {
                            boolean canAccess = method.canAccess(bean);
                            method.setAccessible(true);
                            method.invoke(bean, response);
                            method.setAccessible(canAccess);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                        return null;
                    }).exceptionally(ex -> {
                        Logger logger = LoggerFactory.getLogger(clazz);
                        logger.error("执行EtcdListener.InitHook失败: {}", ex.getMessage(), ex);
                        return null;
                    });
                } else {
                    GetResponse response = kvClient.get(queryKey).join();
                    boolean canAccess = method.canAccess(bean);
                    method.setAccessible(true);
                    method.invoke(bean, response);
                    method.setAccessible(canAccess);
                }
            }
        }
    }

    private void createEtcdListener(Environment environment, Class<?> clazz, Object bean) {
        Map<Method, Set<EtcdListener>> annotatedMethods = MethodIntrospector.selectMethods(clazz, (MethodIntrospector.MetadataLookup<Set<EtcdListener>>) method -> {
            Set<EtcdListener> listenerMethods = findListenerAnnotations(method);
            return (!listenerMethods.isEmpty() ? listenerMethods : null);
        });
        if (!annotatedMethods.isEmpty()) {
            for (Map.Entry<Method, Set<EtcdListener>> entry : annotatedMethods.entrySet()) {
                Method method = entry.getKey();
                for (EtcdListener listener : entry.getValue()) {
                    Watch.Watcher watcher = processEtcdListener(environment, listener, method, bean);
                    watchers.add(watcher);
                }
            }
        }
    }

    private Watch.Watcher processEtcdListener(Environment environment, EtcdListener etcdListener, Method method, Object bean) throws BeansException {
        WatchOption.Builder builder = WatchOption.builder()
                .withRevision(etcdListener.revision())
                .withPrevKV(etcdListener.prevKV())
                .withProgressNotify(etcdListener.progressNotify())
                .withCreateNotify(etcdListener.createdNotify())
                .withNoPut(etcdListener.noPut())
                .withNoDelete(etcdListener.noDelete())
                .withRequireLeader(etcdListener.requireLeader())
                .withPrevKV(etcdListener.prefix());
        String endKey = etcdListener.endKey();
        if (!endKey.isEmpty()) {
            String rawKey = environment.resolvePlaceholders(endKey);
            builder.withRange(ByteSequence.from(rawKey, StandardCharsets.UTF_8));
        }
        WatchOption option = builder.build();
        String rawKey = environment.resolvePlaceholders(etcdListener.key());
        ByteSequence key = ByteSequence.from(rawKey, StandardCharsets.UTF_8);
        return client.getWatchClient().watch(key, option, new EtcdListenerImpl(bean, method));
    }


    private Set<EtcdListener> findListenerAnnotations(Method method) {
        Set<EtcdListener> listeners = new HashSet<>();
        EtcdListener ann = AnnotatedElementUtils.findMergedAnnotation(method, EtcdListener.class);
        if (ann != null) {
            listeners.add(ann);
        }
        EtcdListeners anns = AnnotationUtils.findAnnotation(method, EtcdListeners.class);
        if (anns != null) {
            listeners.addAll(Arrays.stream(anns.value()).toList());
        }
        return listeners;
    }

    @Override
    public void destroy() throws Exception {
        for (Watch.Watcher watcher : watchers) {
            try {
                watcher.close();
            } catch (Exception e) {
                // ignore
            }
        }
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

}
