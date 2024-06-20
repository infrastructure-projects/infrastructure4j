package com.reopenai.infrastructure4j.core.serialization.protobuff;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.ClassScanner;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.TypeUtil;
import com.google.protobuf.*;
import com.reopenai.infrastructure4j.beans.builtin.ErrorCode;
import com.reopenai.infrastructure4j.beans.builtin.TypeReference;
import com.reopenai.infrastructure4j.beans.enums.XEnum;
import com.reopenai.infrastructure4j.core.builtin.constants.JavaTimeConstants;
import com.reopenai.infrastructure4j.core.builtin.exception.FrameworkException;
import com.reopenai.infrastructure4j.core.runtime.RuntimeUtil;
import com.reopenai.infrastructure4j.core.serialization.protobuff.delegate.*;
import com.reopenai.infrastructure4j.core.serialization.protobuff.delegate.number.*;
import com.reopenai.infrastructure4j.core.serialization.protobuff.delegate.time.LocalDateTimeLongProtobufDelegate;
import com.reopenai.infrastructure4j.core.serialization.protobuff.delegate.time.LocalDateTimeStringProtobufDelegate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static com.google.protobuf.Descriptors.FieldDescriptor;


/**
 * 将Protobuf对象和Java普通对象之间相互转换的工具类.
 * 此工具类对转换的支持并不完整，如果有特殊场景的需求请提issue
 *
 * @author Allen Huang
 */
@Slf4j
@SuppressWarnings({"rawtypes", "unchecked"})
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProtobufUtil {

    /**
     * 对象默认值的缓存
     */
    private static final Map<Class<?>, GeneratedMessageV3> DEFAULT_VALUE = new HashMap<>(16);

    /**
     * 已注册的类型处理器
     */
    @SuppressWarnings("rawtypes")
    private static final Map<Type, Map<String, ProtobufDelegate>> handlers = new ConcurrentHashMap<>(16);

    /**
     * 字段描述和字段名之间的缓存
     */
    private static final Map<Descriptors.Descriptor, Map<String, FieldDescriptor>> fieldNameMaps = new ConcurrentHashMap<>(16);

    static {
        //包装类缓存
        DEFAULT_VALUE.put(Int32Value.class, Int32Value.getDefaultInstance());
        DEFAULT_VALUE.put(Int64Value.class, Int64Value.getDefaultInstance());
        DEFAULT_VALUE.put(DoubleValue.class, DoubleValue.getDefaultInstance());
        DEFAULT_VALUE.put(FloatValue.class, FloatValue.getDefaultInstance());
        DEFAULT_VALUE.put(BoolValue.class, BoolValue.getDefaultInstance());
        DEFAULT_VALUE.put(StringValue.class, StringValue.getDefaultInstance());
        DEFAULT_VALUE.put(BytesValue.class, BytesValue.getDefaultInstance());

        //注册默认的类型处理器
        registerDelegate(Long.class, Int64Value.getDescriptor(), new LongProtobufDelegate());
        registerDelegate(Float.class, FloatValue.getDescriptor(), new FloatProtobufDelegate());
        registerDelegate(byte[].class, BytesValue.getDescriptor(), new BytesProtobufDelegate());
        registerDelegate(Double.class, DoubleValue.getDescriptor(), new DoubleProtobufDelegate());
        registerDelegate(Boolean.class, BoolValue.getDescriptor(), new BooleanProtobufDelegate());
        registerDelegate(String.class, StringValue.getDescriptor(), new StringProtobufDelegate());
        registerDelegate(Integer.class, Int32Value.getDescriptor(), new IntegerProtobufDelegate());
        registerDelegate(BigDecimal.class, Int64Value.getDescriptor(), new BigDecimalLongProtobufDelegate());
        registerDelegate(BigDecimal.class, FloatValue.getDescriptor(), new BigDecimalFloatProtobufDelegate());
        registerDelegate(BigDecimal.class, Int32Value.getDescriptor(), new BigDecimalIntegerProtobufDelegate());
        registerDelegate(BigDecimal.class, DoubleValue.getDescriptor(), new BigDecimalDoubleProtobufDelegate());
        registerDelegate(BigDecimal.class, StringValue.getDescriptor(), new BigDecimalStringProtobufDelegate());
        registerDelegate(LocalDateTime.class, Int64Value.getDescriptor(), new LocalDateTimeLongProtobufDelegate());
        registerDelegate(LocalDateTime.class, StringValue.getDescriptor(), new LocalDateTimeStringProtobufDelegate());

        //扫描包，注册默认的枚举处理器
        String mainClass = RuntimeUtil.getMainPackage();
        registerEnumDelegate(String.join(".", mainClass, "enums"));
        registerEnumDelegate(String.join(".", mainClass, "api.enums"));
        registerEnumDelegate(String.join(".", mainClass, "bean.enums"));
        registerEnumDelegate(String.join(".", mainClass, "common.enums"));
        registerEnumDelegate(String.join(".", mainClass, "message.enums"));
        registerEnumDelegate(String.join(".", mainClass, "api.bean.enums"));
    }

    //---------------------------------------
    //          Java Object To Protobuf
    //---------------------------------------

    /**
     * 此方式对性能会有影响，在高性能场景下请勿使用此方法转换对象.
     * 将一个Java对象转换成Protobuf对象.
     *
     * @param value   Java对象实例
     * @param builder Protobuf的构建实例
     */
    public static void merge(Object value, Message.Builder builder) {
        ProtobufDelegate handler = getDelegate(value.getClass(), builder.getDescriptorForType());
        if (handler != null) {
            handler.merge(value, builder);
            return;
        }
        mergeMessage(value, builder);
    }

    private static void mergeMessage(Object value, Message.Builder builder) {
        Map<String, FieldDescriptor> fieldNameMap = getFieldNameMap(builder.getDescriptorForType());
        Field[] fields = ReflectUtil.getFields(value.getClass(), field -> !Modifier.isStatic(field.getModifiers()));
        for (Field field : fields) {
            FieldDescriptor fieldDescriptor = fieldNameMap.get(field.getName());
            if (fieldDescriptor != null) {
                Object fieldValue = ReflectUtil.getFieldValue(value, field);
                if (fieldValue != null) {
                    mergeField(fieldDescriptor, fieldValue, builder);
                }
            }
        }
    }

    private static void mergeField(FieldDescriptor field, Object value, Message.Builder builder) {
        if (field.isRepeated()) {
            if (builder.getRepeatedFieldCount(field) > 0) {
                throw new RuntimeException(
                        "Field " + field.getFullName() + " has already been set.");
            }
        } else {
            if (builder.hasField(field)) {
                throw new RuntimeException(
                        "Field " + field.getFullName() + " has already been set.");
            }
        }
        if (field.isMapField()) {
            mergeMapField(field, value, builder);
        } else if (field.isRepeated()) {
            mergeRepeatedField(field, value, builder);
        } else {
            Object parseValue = parseFieldValue(field, value, builder);
            if (parseValue != null) {
                builder.setField(field, parseValue);
            }
        }
    }

    private static void mergeMapField(FieldDescriptor field, Object value, Message.Builder builder) {
        if (value instanceof Map) {
            Descriptors.Descriptor type = field.getMessageType();
            FieldDescriptor keyField = type.findFieldByName("key");
            FieldDescriptor valueField = type.findFieldByName("value");
            Map mapValue = (Map) value;
            for (Object mk : mapValue.keySet()) {
                Message.Builder entryBuilder = builder.newBuilderForField(field);
                Object k = parseFieldValue(keyField, mk, entryBuilder);
                Object v = parseFieldValue(valueField, mapValue.get(mk), entryBuilder);
                if (v != null) {
                    entryBuilder.setField(keyField, k);
                    entryBuilder.setField(valueField, v);
                    builder.addRepeatedField(field, entryBuilder.build());
                }
            }
            return;
        }
        log.warn("[Protobuf]转换Map类型的数据异常.参数必须是map类型.字段名称:{}", field.getJsonName());
        throw new FrameworkException(ErrorCode.Builtin.PROTOBUF_CONVERSION_ERROR, field.getJsonName());
    }

    private static void mergeRepeatedField(FieldDescriptor field, Object value, Message.Builder builder) {
        if (value instanceof Collection) {
            Collection<?> collectionValues = (Collection<?>) value;
            if (CollUtil.isNotEmpty(collectionValues)) {
                for (Object cv : collectionValues) {
                    Object element = parseFieldValue(field, cv, builder);
                    if (element != null) {
                        builder.addRepeatedField(field, element);
                    }
                }
            }
            return;
        }
        log.warn("[Protobuf]尝试转换[repeated]类型的数据，但是参数为非集合类型.参数名称:{}", field.getJsonName());
        throw new FrameworkException(ErrorCode.Builtin.PROTOBUF_CONVERSION_ERROR, field.getJsonName());
    }

    private static Object parseBoolValue(FieldDescriptor field, Object value) {
        if (value instanceof String) {
            return Boolean.valueOf(((String) value));
        } else if (value instanceof Boolean) {
            return value;
        }
        log.warn("[Protobuf]转换Boolean类型出错.字段名称:{}", field.getJsonName());
        throw new FrameworkException(ErrorCode.Builtin.PROTOBUF_CONVERSION_ERROR, field.getJsonName());
    }

    private static Object parseFloatValue(FieldDescriptor field, Object value) {
        if (value instanceof String) {
            return Float.parseFloat(((String) value));
        } else if (value instanceof Float) {
            return value;
        } else if (value instanceof BigDecimal) {
            return ((BigDecimal) value).floatValue();
        }
        log.warn("[Protobuf]转换Float类型出错.字段名称:{}", field.getJsonName());
        throw new FrameworkException(ErrorCode.Builtin.PROTOBUF_CONVERSION_ERROR, field.getJsonName());
    }

    private static Object parseDoubleValue(FieldDescriptor field, Object value) {
        if (value instanceof String) {
            return Double.parseDouble(((String) value));
        } else if (value instanceof Double) {
            return value;
        } else if (value instanceof BigDecimal) {
            return ((BigDecimal) value).doubleValue();
        }
        log.warn("[Protobuf]转换Double类型出错.字段名称:{}", field.getJsonName());
        throw new FrameworkException(ErrorCode.Builtin.PROTOBUF_CONVERSION_ERROR, field.getJsonName());
    }

    private static Object parseIntegerValue(FieldDescriptor field, Object value) {
        if (value instanceof String) {
            return Integer.parseInt(((String) value));
        } else if (value instanceof BigDecimal) {
            return ((BigDecimal) value).intValue();
        } else if (value instanceof Long) {
            return ((Long) value).intValue();
        } else if (value instanceof Integer) {
            return value;
        }
        log.warn("[Protobuf]转换Integer类型出错.字段名称:{}", field.getJsonName());
        throw new FrameworkException(ErrorCode.Builtin.PROTOBUF_CONVERSION_ERROR, field.getJsonName());
    }

    private static Object parseLongValue(FieldDescriptor field, Object value) {
        if (value instanceof String) {
            BigDecimal decimalValue = new BigDecimal((String) value);
            BigInteger b = decimalValue.toBigIntegerExact();
            return b.longValue();
        } else if (value instanceof BigDecimal) {
            return ((BigDecimal) value).longValue();
        } else if (value instanceof Integer) {
            return Long.valueOf(((Integer) value));
        } else if (value instanceof Long) {
            return value;
        }
        log.warn("[Protobuf]转换Long类型出错.字段名称:{}", field.getJsonName());
        throw new FrameworkException(ErrorCode.Builtin.PROTOBUF_CONVERSION_ERROR, field.getJsonName());
    }

    private static Object parseStringValue(FieldDescriptor field, Object value) {
        if (value instanceof String) {
            return value;
        }
        return value.toString();
    }

    private static Object parseBytesValue(FieldDescriptor field, Object value) {
        if (value instanceof String) {
            return ByteString.copyFrom((String) value, StandardCharsets.UTF_8);
        } else if (value instanceof byte[]) {
            return ByteString.copyFrom(((byte[]) value));
        }
        return null;
    }

    private static Object parseFieldValue(FieldDescriptor field, Object value, Message.Builder builder) {
        switch (field.getType()) {
            case BOOL:
                return parseBoolValue(field, value);
            case FLOAT:
                return parseFloatValue(field, value);
            case DOUBLE:
                return parseDoubleValue(field, value);
            case INT32:
            case SINT32:
            case SFIXED32:
            case UINT32:
            case FIXED32:
                return parseIntegerValue(field, value);
            case INT64:
            case SINT64:
            case SFIXED64:
            case UINT64:
            case FIXED64:
                return parseLongValue(field, value);
            case STRING:
                return parseStringValue(field, value);
            case BYTES:
                return parseBytesValue(field, value);
            case MESSAGE:
            case GROUP:
                Message.Builder subBuilder = builder.newBuilderForField(field);
                merge(value, subBuilder);
                return subBuilder.build();
            default:
                return null;
        }
    }

    private static Map<String, FieldDescriptor> getFieldNameMap(Descriptors.Descriptor descriptor) {
        if (!fieldNameMaps.containsKey(descriptor)) {
            Map<String, FieldDescriptor> fieldNameMap = new HashMap<>(8);
            for (FieldDescriptor field : descriptor.getFields()) {
                fieldNameMap.put(field.getName(), field);
                fieldNameMap.put(field.getJsonName(), field);
            }
            fieldNameMaps.put(descriptor, fieldNameMap);
            return fieldNameMap;
        }
        return fieldNameMaps.get(descriptor);
    }

    //---------------------------------------
    //          Protobuf To Java Object
    //---------------------------------------

    /**
     * 将BigDecimal对象转换成StringValue类型的对象
     *
     * @param data Data类型的对象
     * @return StringValue实例
     */
    public static StringValue stringOf(BigDecimal data) {
        return data == null ? null : StringValue.of(data.toPlainString());
    }

    /**
     * 此方式对性能会有影响，在高性能场景下请勿使用此方法转换对象.
     * 将一个Protobuf对象转换成Java对象.
     * 在Protobuf V3中，所有的值都是非Null的，Java中的却包含了Null的语义.
     * 此工具也实现了对Null语义的支持,如果Protobuf中没有此值，那么在Java中就是Null.
     * 需要注意的是如果在Protobuf中是基本类型，但是在Java中是包装类型，
     * 如果此参数不存在，Java中也将为Null.
     *
     * @param message  Protobuf对象实例
     * @param javaType 要转换后的Java对象实例
     */
    public static <T> T parseObject(MessageOrBuilder message, TypeReference<T> javaType) {
        return (T) parseObject(message, javaType.getType());
    }

    /**
     * 此方式对性能会有影响，在高性能场景下请勿使用此方法转换对象.
     * 将一个Protobuf对象转换成Java对象.
     * 在Protobuf V3中，所有的值都是非Null的，Java中的却包含了Null的语义.
     * 此工具也实现了对Null语义的支持,如果Protobuf中没有此值，那么在Java中就是Null.
     * 需要注意的是如果在Protobuf中是基本类型，但是在Java中是包装类型，
     * 如果此参数不存在，Java中也将为Null.
     *
     * @param message  Protobuf对象实例
     * @param javaType 要转换后的Java对象实例
     */
    public static <T> T parseObject(MessageOrBuilder message, Class<T> javaType) {
        return (T) parseObject(message, (Type) javaType);
    }

    /**
     * 此方式对性能会有影响，在高性能场景下请勿使用此方法转换对象.
     * 将一个Protobuf对象转换成Java对象.
     * 在Protobuf V3中，所有的值都是非Null的，Java中的却包含了Null的语义.
     * 此工具也实现了对Null语义的支持,如果Protobuf中没有此值，那么在Java中就是Null.
     * 需要注意的是如果在Protobuf中是基本类型，但是在Java中是包装类型，
     * 如果此参数不存在，Java中也将为Null.
     *
     * @param message  Protobuf对象实例
     * @param javaType 要转换后的Java对象实例
     */
    public static Object parseObject(MessageOrBuilder message, Type javaType) {
        // 先判断是否有TypeHandler，如果有的话直接使用TypeHandler解析
        ProtobufDelegate handler = getDelegate(javaType, message.getDescriptorForType());
        if (handler != null) {
            return handler.parse(message);
        }

        // 说明是Object类型，需要反射创建对象的实例
        if (javaType instanceof Class) {
            //获取Java的真实类型
            Class javaClass = ((Class) javaType);
            //暂时不兼容枚举类型
            if (javaClass.isEnum()) {
                log.warn("[Protobuf]暂不支持枚举类型的数据解析.");
                return null;
            }

            //获取所有有值的字段
            Map<FieldDescriptor, Object> fieldsToParse = message.getAllFields();

            //构建对象的实例.这里的对象有可能是一个集合类型或者Map类型，这里没有做兼容了
            Object instance = ReflectUtil.newInstance(javaClass);
            //获取对象所有的字段
            Map<String, Field> fieldMap = ReflectUtil.getFieldMap(javaClass);

            for (Map.Entry<FieldDescriptor, Object> field : fieldsToParse.entrySet()) {
                FieldDescriptor fieldDescriptor = field.getKey();
                Field javaField = fieldMap.get(fieldDescriptor.getJsonName());
                //首先判断下是否需要处理这个字段
                if (javaField != null) {
                    //字段的原始值
                    Object value = field.getValue();
                    Object result = parseField(fieldDescriptor, value, javaField.getGenericType());
                    if (result != null) {
                        ReflectUtil.setFieldValue(instance, javaField, result);
                    }
                }
            }

            return instance;
        }
        return null;
    }

    private static Object parseField(FieldDescriptor field, Object fieldValue, Type javaType) {
        if (field.isMapField() && Map.class.isAssignableFrom(TypeUtil.getClass(javaType))) {
            Type keyType = TypeUtil.getTypeArgument(javaType, 0);
            Type valueType = TypeUtil.getTypeArgument(javaType, 1);
            return parseMapFieldValue(field, fieldValue, keyType, valueType);
        } else if (field.isRepeated()) {
            return parseRepeatedFieldValue(field, fieldValue, javaType);
        } else {
            return parseSingleFieldValue(field, fieldValue, javaType);
        }
    }

    private static Object parseRepeatedFieldValue(FieldDescriptor field, Object fieldValue, Type javaType) {
        Class javaClass = TypeUtil.getClass(javaType);
        List elements = (List) fieldValue;
        Collection collection;
        if (List.class.isAssignableFrom(javaClass)) {
            collection = new ArrayList(elements.size());
        } else if (Set.class.isAssignableFrom(javaClass)) {
            collection = new HashSet(elements.size());
        } else {
            throw new IllegalArgumentException("repeated类型的参数只能用List或者Set接收.fieldName:" + field.getJsonName());
        }

        Type elementType = TypeUtil.getTypeArgument(javaType, 0);

        for (Object element : elements) {
            Object value = parseSingleFieldValue(field, element, elementType);
            collection.add(value);
        }

        return collection;
    }

    private static Object parseMapFieldValue(FieldDescriptor field, Object fieldValue, Type keyType, Type valueType) {
        Descriptors.Descriptor type = field.getMessageType();
        FieldDescriptor keyField = type.findFieldByName("key");
        FieldDescriptor valueField = type.findFieldByName("value");
        if (keyField == null || valueField == null) {
            log.warn("[Protobuf]parse Map data error.fieldName:{}", field.getJsonName());
            throw new FrameworkException(ErrorCode.Builtin.PROTOBUF_CONVERSION_ERROR, field.getJsonName());
        }
        Collection<Object> elements = (List<Object>) fieldValue;
        Map map = new HashMap(elements.size());
        for (Object element : elements) {
            Message entry = (Message) element;
            Object entryKey = entry.getField(keyField);
            Object entryValue = entry.getField(valueField);
            Object key = parseSingleFieldValue(keyField, entryKey, keyType);
            if (key != null) {
                Object value = parseSingleFieldValue(valueField, entryValue, valueType);
                map.put(key, value);
            }
        }
        return map;
    }

    private static Object parseSingleFieldValue(FieldDescriptor field, Object fieldValue, Type javaType) {
        return switch (field.getType()) {
            case BYTES ->
                //TODO 未适配
                    fieldValue;
            case MESSAGE, GROUP -> parseObject((MessageOrBuilder) fieldValue, javaType);
            default -> fieldValue;
        };
    }

    /**
     * 判断一个Protobuf值是否不是null。
     *
     * @param message protobuf对象实例
     * @return 如果该值不为null或不为默认对象，则返回true，否则返回false
     */
    public static <T extends GeneratedMessageV3> boolean isNotNull(T message) {
        return !isNull(message);
    }

    /**
     * 判断一个Protobuf值是否是null。
     *
     * @param message protobuf对象实例
     * @return 如果该值为null或为默认对象，则返回true，否则返回false
     */
    public static <T extends GeneratedMessageV3> boolean isNull(T message) {
        if (message == null) {
            return true;
        }
        Class<? extends GeneratedMessageV3> target = message.getClass();
        GeneratedMessageV3 defaultValue = DEFAULT_VALUE.get(target);
        if (defaultValue == null) {
            defaultValue = getDefaultValue(target);
        }
        return defaultValue == message;
    }

    private static <T extends GeneratedMessageV3> T getDefaultValue(Class<? extends GeneratedMessageV3> target) {
        try {
            Method method = target.getMethod("getDefaultInstance");
            if (Modifier.isStatic(method.getModifiers())) {
                return (T) method.invoke(null);
            }
        } catch (Exception e) {
            throw new FrameworkException(e, ErrorCode.Builtin.PROTOBUF_PARSE_DEFAULT_VALUE_ERROR);
        }
        return null;
    }

    //-----------------------------------
    //          Integer
    //-----------------------------------

    /**
     * 将LocalDateTime转换成Int64Value实例
     *
     * @param dateTime 时间对象实例
     * @return Int64Value实例
     */
    public static Int64Value of(LocalDateTime dateTime) {
        if (dateTime != null) {
            return Int64Value.of(LocalDateTimeUtil.toEpochMilli(dateTime));
        }
        return Int64Value.getDefaultInstance();
    }

    /**
     * 将LocalDate转换成Int64Value实例
     *
     * @param date 时间对象实例
     * @return Int64Value实例
     */
    public static Int64Value of(LocalDate date) {
        if (date != null) {
            return Int64Value.of(LocalDateTimeUtil.toEpochMilli(date));
        }
        return Int64Value.getDefaultInstance();
    }

    /**
     * 将Int64Value解析成时间对象
     *
     * @param timestamp 表示时间戳的Int64Value实例
     * @return 解析后的时间对象实例
     */
    public static LocalDateTime parseLocalDateTime(Int64Value timestamp) {
        if (Int64Value.getDefaultInstance() == timestamp) {
            return LocalDateTimeUtil.of(timestamp.getValue(), JavaTimeConstants.SYSTEM_ZONE_OFFSET);
        }
        return null;
    }

    /**
     * 将Int64Value解析成时间对象
     *
     * @param timestamp 表示时间戳的Int64Value实例
     * @return 解析后的时间对象实例
     */
    public static LocalDate parseLocalDate(Int64Value timestamp) {
        if (Int64Value.getDefaultInstance() == timestamp) {
            return LocalDateTimeUtil.of(timestamp.getValue(), JavaTimeConstants.SYSTEM_ZONE_OFFSET).toLocalDate();
        }
        return null;
    }

    /**
     * 写入一个{@link Integer}值并将其转换成{@link Int32Value}实例。
     *
     * @param value {@link Integer}实例
     * @return 转换后的 {@link Int32Value} 实例
     */
    public static Int32Value writeInteger(Integer value) {
        Int32Value.Builder builder = Int32Value.newBuilder();
        if (value != null) {
            builder.setValue(value);
        }
        return builder.build();
    }

    /**
     * 写入一个{@link Integer}值并将其转换成{@link Int32Value}实例。
     * 消费者将消费转换后的值。
     *
     * @param value {@link Integer}实例
     */
    public static void writeInteger(Consumer<Int32Value> consumer, Integer value) {
        consumer.accept(writeInteger(value));
    }

    /**
     * 读取一个Protobuf类型的int包装类型，并转换成{@link Integer}实例。
     * 如果此包装类未序列化任何值，则将返回null.
     *
     * @param value {@link Int32Value} 实例
     * @return 如果 {@link Int32Value} 有值则返回转换后的 {@link Integer} 实例，否则返回null
     */
    public static Integer readInteger(Int32Value value) {
        return isNull(value) ? null : value.getValue();
    }

    /**
     * 读取一个Protobuf类型的int包装类型，并转换成{@link Integer}实例。
     * 如果此包装类未序列化任何值，则将返回null.
     * 消费者将消费转换后的值。
     *
     * @param value {@link Int32Value} 实例
     */
    public static void readInteger(Consumer<Integer> consumer, Int32Value value) {
        consumer.accept(readInteger(value));
    }


    //-----------------------------------
    //          Long
    //-----------------------------------

    /**
     * 写入一个{@link Long}值并将其转换成{@link Int64Value}实例。
     *
     * @param value {@link Long}实例
     * @return 转换后的{@link Int64Value}实例
     */
    public static Int64Value writeLong(Long value) {
        Int64Value.Builder builder = Int64Value.newBuilder();
        if (value != null) {
            builder.setValue(value);
        }
        return builder.build();
    }

    /**
     * 写入一个{@link Long}值并将其转换成{@link Int64Value}实例。
     * 消费者将消费转换后的值。
     *
     * @param value {@link Long} 的值
     */
    public static void writeLong(Consumer<Int64Value> consumer, Long value) {
        consumer.accept(writeLong(value));
    }

    /**
     * 读取一个Protobuf类型的long包装类型，并转换成{@link Long}实例。
     * 如果此包装类未序列化任何值，则将返回null.
     *
     * @param value {@link Int64Value} 实例
     * @return 如果 {@link Int64Value} 有值则返回转换后的 {@link Long} 实例，否则返回null
     */
    public static Long readLong(Int64Value value) {
        return isNull(value) ? null : value.getValue();
    }

    /**
     * 读取一个Protobuf类型的long包装类型，并转换成{@link Long}实例。
     * 如果此包装类未序列化任何值，则将返回null.
     * 消费者将消费转换后的值。
     *
     * @param value {@link Int64Value} 实例
     */
    public static void readLong(Consumer<Long> consumer, Int64Value value) {
        consumer.accept(readLong(value));
    }

    //-----------------------------------
    //          Double
    //-----------------------------------

    /**
     * 写入一个{@link Double}值并将其转换成{@link DoubleValue}实例。
     *
     * @param value {@link Double}实例
     * @return 转换后的 {@link DoubleValue} 实例
     */
    public static DoubleValue writeDouble(Double value) {
        DoubleValue.Builder builder = DoubleValue.newBuilder();
        if (value != null) {
            builder.setValue(value);
        }
        return builder.build();
    }

    /**
     * 写入一个{@link Double}值并将其转换成{@link DoubleValue}实例。
     * 消费者将消费转换后的值。
     *
     * @param value {@link Double} 的值
     */
    public static void writeDouble(Consumer<DoubleValue> consumer, Double value) {
        consumer.accept(writeDouble(value));
    }

    /**
     * 读取一个Protobuf类型的double包装类型，并转换成{@link Double}实例。
     * 如果此包装类未序列化任何值，则将返回null.
     *
     * @param value {@link DoubleValue} 实例
     * @return 如果 {@link DoubleValue} 有值则返回转换后的 {@link Double} 实例，否则返回null
     */
    public static Double readDouble(DoubleValue value) {
        return isNull(value) ? null : value.getValue();
    }

    /**
     * 读取一个Protobuf类型的double包装类型，并转换成{@link Double}实例。
     * 如果此包装类未序列化任何值，则将返回null.
     * 消费者将消费转换后的值。
     *
     * @param value {@link DoubleValue} 实例
     */
    public static void readDouble(Consumer<Double> consumer, DoubleValue value) {
        consumer.accept(readDouble(value));
    }


    //-----------------------------

    @SuppressWarnings("unchecked")
    public static <SOURCE, TARGET extends MessageOrBuilder> ProtobufDelegate<SOURCE, TARGET> getDelegate(Type source, Descriptors.Descriptor descriptor) {
        return Optional.ofNullable(handlers.get(source))
                .map(map -> map.get(descriptor.getFullName()))
                .orElse(null);
    }

    /**
     * 注册一个TypeHandler实例
     *
     * @param sourceType Java数据类型
     * @param descriptor Protobuf类型描述
     * @param handler    TypeHandler实例
     */
    public static void registerDelegate(Type sourceType, Descriptors.Descriptor descriptor, ProtobufDelegate<?, ?> handler) {
        handlers.computeIfAbsent(sourceType, k -> new ConcurrentHashMap<>())
                .put(descriptor.getFullName(), handler);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void registerEnumDelegate(String packages) {
        Set<Class<?>> classes = ClassScanner.scanPackage(packages, clazz -> clazz.isEnum() && XEnum.class.isAssignableFrom(clazz));
        if (CollUtil.isNotEmpty(classes)) {
            for (Class type : classes) {
                Class<?> returnType = ReflectUtil.getMethod(type, "getValue").getReturnType();
                if (int.class == returnType || Integer.class == returnType) {
                    registerDelegate(type, Int32Value.getDescriptor(), new XEnumProtobufDelegate<>(type));
                }
                registerDelegate(type, StringValue.getDescriptor(), new EnumProtobufDelegate<>(type));
            }
        }
    }

}
