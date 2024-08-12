package com.reopenai.infrastructure4j.rsocket.client;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.lang.NonNull;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author Allen Huang
 */
public class RsocketClientScanner implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    protected ResourceLoader resourceLoader;

    protected Environment environment;

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata metadata, @NonNull BeanDefinitionRegistry registry) {
        Set<BeanDefinition> candidateComponents = new HashSet<>();
        String mainPackage = ClassUtils.getPackageName(metadata.getClassName());
        ClassPathScanningCandidateComponentProvider scanner = getScanner(mainPackage);
        scanner.setResourceLoader(this.resourceLoader);
        scanner.addIncludeFilter(new AnnotationTypeFilter(RsocketStub.class));
        Set<String> basePackages = getBasePackages(metadata);
        for (String basePackage : basePackages) {
            candidateComponents.addAll(scanner.findCandidateComponents(basePackage));
        }
        for (BeanDefinition candidateComponent : candidateComponents) {
            if (candidateComponent instanceof AnnotatedBeanDefinition component) {
                AnnotationMetadata annotationMetadata = component.getMetadata();
                if (!annotationMetadata.isInterface()) {
                    continue;
                }
                registerRsocketClient(registry, annotationMetadata);
            }
        }
    }

    private void registerRsocketClient(BeanDefinitionRegistry registry, AnnotationMetadata metadata) {
        String className = metadata.getClassName();
        BeanDefinitionBuilder definition = BeanDefinitionBuilder
                .genericBeanDefinition(RsocketClientFactoryBean.class);
        definition.addPropertyValue("target", className);
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
        beanDefinition.setPrimary(true);
        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className,
                new String[]{className.substring(className.lastIndexOf(".") + 1)});
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

    protected ClassPathScanningCandidateComponentProvider getScanner(String mainPackage) {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
            @Override
            protected boolean isCandidateComponent(@NonNull AnnotatedBeanDefinition beanDefinition) {
                boolean isCandidate = false;
                if (beanDefinition.getMetadata().isIndependent()) {
                    if (!beanDefinition.getMetadata().isAnnotation()) {
                        isCandidate = true;
                    }
                }
                String beanClassName = beanDefinition.getBeanClassName();
                if (beanClassName != null && beanClassName.contains(mainPackage)) {
                    return false;
                }
                return isCandidate;
            }
        };
    }

    protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata
                .getAnnotationAttributes(EnableRsocketClients.class.getCanonicalName());

        if (attributes == null) {
            return Collections.emptySet();
        }

        Set<String> basePackages = new HashSet<>();
        for (String pkg : (String[]) attributes.get("value")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (String pkg : (String[]) attributes.get("basePackages")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (Class<?> clazz : (Class[]) attributes.get("basePackageClasses")) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }

        if (basePackages.isEmpty() || (boolean) attributes.get("loadProjectPackages")) {
            String packageName = ClassUtils.getPackageName(importingClassMetadata.getClassName());
            String[] split = packageName.split("\\.");
            StringJoiner joiner = new StringJoiner(".");
            for (int i = 0; i < split.length; i++) {
                joiner.add(split[i]);
                if (i >= 2) {
                    break;
                }
            }
            packageName = joiner.toString();
            basePackages.add(packageName);
        }
        return basePackages;
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(@NonNull ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

}
