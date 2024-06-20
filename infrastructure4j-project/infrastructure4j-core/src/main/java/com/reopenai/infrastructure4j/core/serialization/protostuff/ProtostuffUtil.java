package com.reopenai.infrastructure4j.core.serialization.protostuff;

import com.reopenai.infrastructure4j.core.serialization.protostuff.delegate.BigDecimalDelegate;
import com.reopenai.infrastructure4j.core.serialization.protostuff.delegate.TimestampDelegate;
import io.protostuff.GraphIOUtil;
import io.protostuff.LinkedBuffer;
import io.protostuff.Schema;
import io.protostuff.Tag;
import io.protostuff.runtime.DefaultIdStrategy;
import io.protostuff.runtime.RuntimeEnv;
import io.protostuff.runtime.RuntimeSchema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Protostuff工具类
 *
 * @author Allen Huang
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProtostuffUtil {

    static {
        System.setProperty("protostuff.runtime.enums_by_name", "true");
        if (RuntimeEnv.ID_STRATEGY instanceof DefaultIdStrategy) {
            ((DefaultIdStrategy) RuntimeEnv.ID_STRATEGY).registerDelegate(new TimestampDelegate());
            ((DefaultIdStrategy) RuntimeEnv.ID_STRATEGY).registerDelegate(new BigDecimalDelegate());
        }
    }

    /**
     * 注册一个Schema
     *
     * @param target 目标类型
     */
    public static <T> void register(Class<T> target) {
        RuntimeSchema.getSchema(target);
    }

    /**
     * 按照原生的Protostuff序列化对象
     *
     * @param data 对象实例，不能为空
     * @return 序列化后的byte[]
     */
    public static <T> byte[] serialize(T data) {
        LinkedBuffer buffer = LinkedBuffer.allocate();
        byte[] bytes;
        try {
            if (data == null || WrapperUtils.needWrapper(data)) {
                Schema<Wrapper> schema = RuntimeSchema.getSchema(Wrapper.class);
                Wrapper wrapper = new Wrapper(data);
                bytes = GraphIOUtil.toByteArray(wrapper, schema, buffer);
            } else {
                Schema schema = RuntimeSchema.getSchema(data.getClass());
                bytes = GraphIOUtil.toByteArray(data, schema, buffer);
            }
        } finally {
            buffer.clear();
        }
        return bytes;
    }

    /**
     * 按照原生的Protostuff方式反序列化对象
     *
     * @param buff   待序列化的字节数组
     * @param target 需要序列化的目标类型
     * @return 序列化后的byte[]
     */
    public static <T> T deserialize(byte[] buff, Class<T> target) {
        Object result;
        if (WrapperUtils.needWrapper(target)) {
            Schema<Wrapper> schema = RuntimeSchema.getSchema(Wrapper.class);
            Wrapper wrapper = schema.newMessage();
            //GraphIOUtil
            GraphIOUtil.mergeFrom(buff, wrapper, schema);
            result = wrapper.getData();
        } else {
            Schema schema = RuntimeSchema.getSchema(target);
            result = schema.newMessage();
            GraphIOUtil.mergeFrom(buff, result, schema);
        }
        return (T) result;
    }


    /**
     * 包装器，某些不能直接序列化的对象使用包装器包装
     *
     * @author Allen Huang
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Wrapper<T> {

        @Tag(1)
        private T data;

    }

    private static class WrapperUtils {
        private static final Set<Class<?>> WRAPPER_SET = new HashSet<>();

        static {
            WRAPPER_SET.add(Map.class);
            WRAPPER_SET.add(HashMap.class);
            WRAPPER_SET.add(TreeMap.class);
            WRAPPER_SET.add(Hashtable.class);
            WRAPPER_SET.add(SortedMap.class);
            WRAPPER_SET.add(LinkedHashMap.class);
            WRAPPER_SET.add(ConcurrentHashMap.class);

            WRAPPER_SET.add(List.class);
            WRAPPER_SET.add(ArrayList.class);
            WRAPPER_SET.add(LinkedList.class);

            WRAPPER_SET.add(Vector.class);

            WRAPPER_SET.add(Set.class);
            WRAPPER_SET.add(HashSet.class);
            WRAPPER_SET.add(TreeSet.class);
            WRAPPER_SET.add(BitSet.class);

            WRAPPER_SET.add(StringBuffer.class);
            WRAPPER_SET.add(StringBuilder.class);

            WRAPPER_SET.add(BigDecimal.class);
            WRAPPER_SET.add(Date.class);
            WRAPPER_SET.add(Calendar.class);
            WRAPPER_SET.add(Time.class);
            WRAPPER_SET.add(Timestamp.class);
            WRAPPER_SET.add(java.sql.Date.class);

            WRAPPER_SET.add(Wrapper.class);

        }

        /**
         * Determine if the object needs wrap
         *
         * @param clazz object type
         * @return need wrap
         */
        public static boolean needWrapper(Class<?> clazz) {
            return WrapperUtils.WRAPPER_SET.contains(clazz) || clazz.isArray() || clazz.isEnum();
        }

        /**
         * Determine if the object needs wrap
         *
         * @param obj object
         * @return need wrap
         */
        public static boolean needWrapper(Object obj) {
            return needWrapper(obj.getClass());
        }

    }

}
