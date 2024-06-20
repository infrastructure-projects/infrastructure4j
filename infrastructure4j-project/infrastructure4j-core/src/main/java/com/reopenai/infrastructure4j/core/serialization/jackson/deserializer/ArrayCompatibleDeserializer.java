package com.reopenai.infrastructure4j.core.serialization.jackson.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.reopenai.infrastructure4j.beans.builtin.TypeReference;
import com.reopenai.infrastructure4j.core.serialization.jackson.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 序列化时的数组兼容器，在解析JSON字段时能够自动兼容非法的空数组格式.
 *
 * @author Allen Huang
 */
@NoArgsConstructor
@AllArgsConstructor
public class ArrayCompatibleDeserializer extends JsonDeserializer<List<Integer>> implements ContextualDeserializer {

    private JavaType type;

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        JavaType type = property.getType();
        return new ArrayCompatibleDeserializer(type);
    }

    @Override
    public List<Integer> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        TreeNode treeNode = p.readValueAsTree();
        if (treeNode.isArray()) {
            return JsonUtil.treeToValue(treeNode, new TypeReference<>() {
                @Override
                public Type getType() {
                    return type;
                }
            });
        }
        return null;
    }
}
