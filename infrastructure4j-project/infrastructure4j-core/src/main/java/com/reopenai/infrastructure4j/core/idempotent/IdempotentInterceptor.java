package com.reopenai.infrastructure4j.core.idempotent;

import cn.hutool.core.util.StrUtil;
import com.reopenai.infrastructure4j.core.aop.MethodDescription;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

/**
 * 幂等切面处理器
 *
 * @author Allen Huang
 */
@Slf4j
public class IdempotentInterceptor implements MethodInterceptor, EnvironmentAware {

    private final SpelExpressionParser parser = new SpelExpressionParser();

    private final Map<String, IdempotentProvider> providerMap;

    private Environment environment;

    private String applicationName;

    public IdempotentInterceptor(Map<String, IdempotentProvider> providerMap) {
        this.providerMap = providerMap;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();
        IdempotentMethod ann = method.getAnnotation(IdempotentMethod.class);
        String expression = ann.value();
        if (StrUtil.isBlank(expression)) {
            log.warn("[IdempotentMethod]方法{}#{}无法保证幂等,因为@IdempotentMethod.value为空.", className, methodName);
            return invocation.proceed();
        }
        String idempotentKey = parseKey(invocation, expression);
        if (ann.includePrefix() && StrUtil.isNotBlank(applicationName)) {
            idempotentKey = String.format("COMPONENT:IDEMPOTENT_METHOD:%S:%s", applicationName, idempotentKey);
            if (log.isDebugEnabled()) {
                log.debug("[IdempotentMethod]生成的key名将包含服务前缀.更新后的key={}", idempotentKey);
            }
        } else {
            idempotentKey = "COMPONENT:IDEMPOTENT_METHOD:" + idempotentKey;
        }
        IdempotentProvider idempotentProvider = getIdempotentProvider(ann.provider());
        try {
            Object result;
            boolean hasInvoked = idempotentProvider.hasInvoked(idempotentKey);
            if (hasInvoked) {
                log.info("[IdempotentMethod]方法{}#{}在key={}时已被执行,为保证幂等本次调用将被忽略", className, methodName, idempotentKey);
                result = idempotentProvider.getInvokeResult(idempotentKey, method.getGenericReturnType());
                if (result instanceof Throwable) {
                    throw (Throwable) result;
                }
            } else {
                result = invocation.proceed();
            }
            idempotentProvider.onSuccess(idempotentKey, method.getGenericReturnType(), result, ann.expire());
            return result;
        } catch (Throwable e) {
            if (ann.includeException()) {
                Class<? extends Throwable> type = e.getClass();
                for (Class<? extends Throwable> target : ann.noNegativeFor()) {
                    if (target.isAssignableFrom(type)) {
                        log.info("[IdempotentMethod]方法调用是遇到异常，但是幂等声明中指定了要忽略此异常.当前异常类className={},指明要忽略的异常类className={}", type.getName(), target.getName());
                        throw e;
                    }
                }
                idempotentProvider.onError(idempotentKey, e, ann.expire());
            }
            throw e;
        }
    }

    private IdempotentProvider getIdempotentProvider(String beanName) {
        return Optional.ofNullable(this.providerMap.get(beanName))
                .orElseGet(() -> this.providerMap.get(IdempotentProvider.DEFAULT_PROVIDER_NAME));
    }

    private String parseKey(MethodInvocation invocation, String expression) {
        Method method = invocation.getMethod();
        MethodDescription methodDescription = MethodDescription.builder()
                .method(method)
                .methodName(method.getName())
                .target(invocation.getThis())
                .args(invocation.getArguments())
                .targetClass(method.getDeclaringClass())
                .build();
        StandardEvaluationContext context = new StandardEvaluationContext(methodDescription);
        context.setVariable("env", this.environment);
        String key = parser.parseExpression(expression).getValue(context, String.class);
        if (log.isDebugEnabled()) {
            log.debug("[IdempotentMethod]解析前的SpEl={}, 解析后的key={}", expression, key);
        }
        return key;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
        this.applicationName = environment.getProperty("spring.application.name");
    }

}
