package com.reopenai.infrastructure4j.core.serialization.jackson;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.reopenai.infrastructure4j.beans.builtin.NumberValue;
import com.reopenai.infrastructure4j.beans.builtin.TypeReference;
import com.reopenai.infrastructure4j.core.serialization.jackson.deserializer.LocalDateDeserializer;
import com.reopenai.infrastructure4j.core.serialization.jackson.deserializer.LocalDateTimeDeserializer;
import com.reopenai.infrastructure4j.core.serialization.jackson.deserializer.NumberValueDeserializer;
import com.reopenai.infrastructure4j.core.serialization.jackson.serializer.LocalDateSerializer;
import com.reopenai.infrastructure4j.core.serialization.jackson.serializer.LocalDateTimeSerializer;
import com.reopenai.infrastructure4j.core.serialization.jackson.serializer.NumberSerializer;
import com.reopenai.infrastructure4j.core.serialization.jackson.serializer.NumberValueSerializer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * JSON工具类
 *
 * @author Allen Huang
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonUtil {

    private static ObjectMapper objectMapper;

    static {
        //默认的ObjectMapper实例
        objectMapper = new ObjectMapper();
        objectMapper.setLocale(Locale.US);
        objectMapper.setTimeZone(TimeZone.getTimeZone("UTC"));
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);

        objectMapper.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(Number.class, new NumberSerializer());
        javaTimeModule.addSerializer(NumberValue.class, new NumberValueSerializer());
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer());
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());
        javaTimeModule.addDeserializer(NumberValue.class, new NumberValueDeserializer());
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        objectMapper.registerModule(javaTimeModule);
    }

    /**
     * 将一个字符串解析成List
     *
     * @param json 待解析的字符串
     * @return 解析后的List
     */
    public static List<Object> parseArray(String json) {
        if (StrUtil.isNotBlank(json)) {
            return parseObject(json, new TypeReference<>() {
            });
        }
        return new ArrayList<>(0);
    }

    /**
     * 将一个字符串解析成Map
     *
     * @param json 待解析的字符串
     * @return 解析后的map
     */
    public static Map<String, Object> parseMap(String json) {
        if (StrUtil.isNotEmpty(json)) {
            return parseObject(json, new TypeReference<>() {
            });
        }
        return new HashMap<>(0);
    }


    /**
     * 将JSON字符串转换成Object
     *
     * @param json 原始的json
     * @return 如果是对象则返回map，如果是数组则返回list
     */
    public static Object parseObject(String json) {
        return isTypeJSONArray(json) ? parseArray(json) : parseMap(json);
    }

    /**
     * 判断一个字符串是否是一个JSON
     *
     * @param str 待判断的字符串
     * @return 如果是字符串则返回true
     */
    public static boolean isTypeJSON(String str) {
        return isTypeJSONObject(str) || isTypeJSONArray(str);
    }

    /**
     * 判断一个字符串是否是一个JSON
     *
     * @param str 待判断的字符串
     * @return 如果是字符串则返回true
     */
    public static boolean isTypeJSONArray(String str) {
        return !StrUtil.isBlank(str) && StrUtil.isWrap(StrUtil.trim(str), '[', ']');
    }

    /**
     * 判断一个字符串是否是一个JSON
     *
     * @param str 待判断的字符串
     * @return 如果是字符串则返回true
     */
    public static boolean isTypeJSONObject(String str) {
        return !StrUtil.isBlank(str) && StrUtil.isWrap(StrUtil.trim(str), '{', '}');
    }

    /**
     * 将Map类型的数据转换成目标类型。
     *
     * @param map       需要转换的原始数据
     * @param reference 目标类型的泛型引用
     * @return 转换成功后的目标类型实例
     */
    public static <T> T parseObject(Map<String, Object> map, TypeReference<T> reference) {
        return objectMapper.convertValue(map, new com.fasterxml.jackson.core.type.TypeReference<T>() {
            @Override
            public Type getType() {
                return reference.getType();
            }
        });
    }

    /**
     * 将Map类型的数据转换成目标类型。此方式会丢失泛型。
     *
     * @param map    需要转换的原始数据
     * @param target 要转换的目标类
     * @return 转换成功后的目标类型实例
     */
    public static <T> T parseObject(Map<String, Object> map, Class<T> target) {
        return objectMapper.convertValue(map, target);
    }

    /**
     * 解析JSON字符串，并将JSON字符串转换成目标类型
     *
     * @param json 需要解析的JSON字符串
     * @param type 要转换的目标类型，支持泛型
     * @return 转换成功后的目标类型实例
     * @throws IllegalArgumentException 如果JSON无法被解析，则将抛出此异常
     */
    @SuppressWarnings("unchecked")
    public static <T> T parseObject(String json, Type type) throws IllegalArgumentException {
        try {
            return (T) objectMapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<>() {
                @Override
                public Type getType() {
                    return type;
                }
            });
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 解析JSON字符串，并将JSON字符串转换成目标类型
     *
     * @param json      需要解析的JSON字符串
     * @param reference 目标类型的泛型引用
     * @return 转换成功后的目标类型实例
     * @throws IllegalArgumentException 如果JSON无法被解析，则将抛出此异常
     */
    public static <T> T parseObject(String json, TypeReference<T> reference) throws IllegalArgumentException {
        return parseObject(json, reference.getType());
    }

    /**
     * 解析JSON字符串，并将JSON字符串转换成目标类型
     *
     * @param json   需要解析的JSON字符串
     * @param target 要转换的目标类
     * @return 转换成功后的目标类型实例
     * @throws IllegalArgumentException 如果JSON无法被解析，则将抛出此异常
     */
    public static <T> T parseObject(String json, Class<T> target) throws IllegalArgumentException {
        try {
            return objectMapper.readValue(json, target);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 解析参数，并将参数转换成目标类型
     *
     * @param params 需要解析的参数，该参数类型只能是Map或String
     * @param type   要转换的目标类
     * @return 转换成功后的目标类型实例
     * @throws IllegalArgumentException 如果JSON无法被解析，则将抛出此异常
     */
    public static <T> T parseFromObject(Object params, TypeReference<T> type) {
        return params instanceof Map ? parseObject((Map<String, Object>) params, type) : parseObject(params.toString(), type);
    }

    /**
     * 将一个对象序列化成JSON字符串
     *
     * @param object 需要序列化的对象
     * @return 序列化后的JSON字符串
     * @throws IllegalArgumentException 如果序列化失败，将会抛出此异常
     */
    public static String toJSONString(Object object) throws IllegalArgumentException {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException error) {
            throw new IllegalArgumentException(error);
        }
    }

    /**
     * 将TreeNode实例解析成目标类型
     *
     * @param treeNode TreeNode 实例
     * @param target   目标类型
     * @return 解析后的实例
     */
    public static <T> T treeToValue(TreeNode treeNode, Class<T> target) {
        try {
            return objectMapper.treeToValue(treeNode, target);
        } catch (JsonProcessingException error) {
            throw new IllegalArgumentException(error);
        }
    }

    /**
     * 将TreeNode实例解析成目标类型
     *
     * @param treeNode TreeNode 实例
     * @param target   目标类型
     * @return 解析后的实例
     */
    public static <T> T treeToValue(TreeNode treeNode, TypeReference<T> target) {
        try {
            JavaType javaType = objectMapper
                    .getTypeFactory()
                    .constructType(target.getType());
            return objectMapper.treeToValue(treeNode, javaType);
        } catch (JsonProcessingException error) {
            throw new IllegalArgumentException(error);
        }
    }

}
