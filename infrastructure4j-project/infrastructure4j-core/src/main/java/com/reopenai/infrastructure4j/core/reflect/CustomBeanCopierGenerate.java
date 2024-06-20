package com.reopenai.infrastructure4j.core.reflect;

import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Type;
import org.springframework.cglib.core.*;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 生成的BeanCopy器
 *
 * @author Allen Huang
 */
abstract class CustomBeanCopierGenerate {

    private static final BeanCopierKey KEY_FACTORY =
            (BeanCopierKey) KeyFactory.create(BeanCopierKey.class);
    private static final Type CONVERTER =
            TypeUtils.parseType("org.springframework.cglib.core.Converter");
    private static final Type BEAN_COPIER =
            TypeUtils.parseType(CustomBeanCopierGenerate.class.getName());
    private static final Signature COPY =
            new Signature("copy", Type.VOID_TYPE, new Type[]{Constants.TYPE_OBJECT, Constants.TYPE_OBJECT, CONVERTER});
    private static final Signature CONVERT =
            TypeUtils.parseSignature("Object convert(Object, Class, Object)");

    interface BeanCopierKey {
        Object newInstance(String source, String target, boolean useConverter);
    }

    static CustomBeanCopierGenerate create(Class source, Class target, boolean useConverter, Set<String> ignoreFields) {
        Generator gen = new Generator();
        gen.setSource(source);
        gen.setTarget(target);
        gen.setUseConverter(useConverter);
        gen.setIgnoreFields(ignoreFields);
        return gen.create();
    }

    abstract public void copy(Object from, Object to, Converter converter);

    static class Generator extends AbstractClassGenerator {
        private static final Source SOURCE = new Source(CustomBeanCopierGenerate.class.getName());
        private Class source;
        private Class target;
        private boolean useConverter;

        private Set<String> ignoreFields;

        public Generator() {
            super(SOURCE);
        }

        public void setSource(Class source) {
            if (!Modifier.isPublic(source.getModifiers())) {
                setNamePrefix(source.getName());
            }
            this.source = source;
        }

        public void setTarget(Class target) {
            if (!Modifier.isPublic(target.getModifiers())) {
                setNamePrefix(target.getName());
            }
            this.target = target;
            // SPRING PATCH BEGIN
            setContextClass(target);
            // SPRING PATCH END
        }

        public void setIgnoreFields(Set<String> ignoreFields) {
            this.ignoreFields = ignoreFields;
        }

        public void setUseConverter(boolean useConverter) {
            this.useConverter = useConverter;
        }

        protected ClassLoader getDefaultClassLoader() {
            return source.getClassLoader();
        }

        protected ProtectionDomain getProtectionDomain() {
            return ReflectUtils.getProtectionDomain(source);
        }

        public CustomBeanCopierGenerate create() {
            Object key = KEY_FACTORY.newInstance(source.getName(), target.getName(), useConverter);
            return (CustomBeanCopierGenerate) super.create(key);
        }

        public void generateClass(ClassVisitor v) {
            Type sourceType = Type.getType(source);
            Type targetType = Type.getType(target);
            ClassEmitter ce = new ClassEmitter(v);
            ce.begin_class(Constants.V1_8,
                    Constants.ACC_PUBLIC,
                    getClassName(),
                    BEAN_COPIER,
                    null,
                    Constants.SOURCE_FILE);

            EmitUtils.null_constructor(ce);
            CodeEmitter e = ce.begin_method(Constants.ACC_PUBLIC, COPY, null);
            PropertyDescriptor[] getters = ReflectUtils.getBeanGetters(source);
            PropertyDescriptor[] setters = ReflectUtils.getBeanSetters(target);

            Map names = new HashMap();
            for (PropertyDescriptor propertyDescriptor : getters) {
                names.put(propertyDescriptor.getName(), propertyDescriptor);
            }
            Local targetLocal = e.make_local();
            Local sourceLocal = e.make_local();
            if (useConverter) {
                e.load_arg(1);
                e.checkcast(targetType);
                e.store_local(targetLocal);
                e.load_arg(0);
                e.checkcast(sourceType);
                e.store_local(sourceLocal);
            } else {
                e.load_arg(1);
                e.checkcast(targetType);
                e.load_arg(0);
                e.checkcast(sourceType);
            }
            for (PropertyDescriptor setter : setters) {
                // 过滤掉忽略的字段
                if (ignoreFields.contains(setter.getName())) {
                    continue;
                }
                PropertyDescriptor getter = (PropertyDescriptor) names.get(setter.getName());
                if (getter != null) {
                    MethodInfo read = ReflectUtils.getMethodInfo(getter.getReadMethod());
                    MethodInfo write = ReflectUtils.getMethodInfo(setter.getWriteMethod());
                    if (parseSetterType(setter).equals(parseGetterType(getter))) {
                        if (useConverter) {
                            Type setterType = write.getSignature().getArgumentTypes()[0];
                            e.load_local(targetLocal);
                            e.load_arg(2);
                            e.load_local(sourceLocal);
                            e.invoke(read);
                            e.box(read.getSignature().getReturnType());
                            EmitUtils.load_class(e, setterType);
                            e.push(write.getSignature().getName());
                            e.invoke_interface(CONVERTER, CONVERT);
                            e.unbox_or_zero(setterType);
                            e.invoke(write);
                        } else if (compatible(getter, setter)) {
                            e.dup2();
                            e.invoke(read);
                            e.invoke(write);
                        }
                    }
                }
            }
            e.return_value();
            e.end_method();
            ce.end_class();
        }

        private java.lang.reflect.Type parseSetterType(PropertyDescriptor setter) {
            return setter.getWriteMethod().getParameters()[0].getParameterizedType();
        }

        private java.lang.reflect.Type parseGetterType(PropertyDescriptor getter) {
            return getter.getReadMethod().getGenericReturnType();
        }

        private static boolean compatible(PropertyDescriptor getter, PropertyDescriptor setter) {
            // TODO: allow automatic widening conversions?
            return setter.getPropertyType().isAssignableFrom(getter.getPropertyType());
        }

        protected Object firstInstance(Class type) {
            return ReflectUtils.newInstance(type);
        }

        protected Object nextInstance(Object instance) {
            return instance;
        }
    }

}
