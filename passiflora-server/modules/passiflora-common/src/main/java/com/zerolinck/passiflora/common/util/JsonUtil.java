package com.zerolinck.passiflora.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.zerolinck.passiflora.common.config.jackson.JacksonConfig;
import lombok.SneakyThrows;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 林常坤 on 2024/09/28
 */
public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 将对象转换为 JSON 字符串
     */
    @SneakyThrows
    public static String toJson(Object obj) {
        return objectMapper.writeValueAsString(obj);
    }

    /**
     * 将对象转换为 JSON 字符串
     */
    @SneakyThrows
    public static String toPrettyJson(Object obj) {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }

    /**
     * 将 JSON 字符串转换为指定类型的对象
     */
    @SneakyThrows
    public static <T> T convertValue(String json, Class<T> clazz) {
        return objectMapper.readValue(json, clazz);
    }

    /**
     * 将 JSON 字符串转换为 List
     */
    @SneakyThrows
    public static <T> List<T> convertToList(String json, Class<T> clazz) {
        CollectionType type = objectMapper.getTypeFactory()
                .constructCollectionType(ArrayList.class, clazz);
        return parse(json, type);
    }

    /**
     * 将对象转换为 Map
     */
    @SneakyThrows
    public static Map<String, Object> convertToMap(Object obj) {
        return objectMapper.convertValue(obj, new TypeReference<>() {
        });
    }

    /**
     * 将 Map 转换为指定类型的对象
     */
    public static <T> T convertValue(Map<String, Object> map, Class<T> clazz) {
        return objectMapper.convertValue(map, clazz);
    }

    /**
     * 将 List 转换为 Map 的 List
     */
    public static <T> List<Map<String, Object>> convertToMapList(List<T> list) {
        return objectMapper.convertValue(list, new TypeReference<>() {
        });
    }

    /**
     * 将 Map 的 List 转换为指定类型对象的 List
     */
    public static <T> List<T> convertToList(List<Map<String, Object>> mapList, Class<T> clazz) {
        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
        return objectMapper.convertValue(mapList, listType);
    }

    @SneakyThrows
    public static JsonNode readTree(String json) {
        return objectMapper.readTree(json);
    }

    @Nonnull
    @SneakyThrows
    private static <T> T parse(@Nonnull String jsonString, JavaType javaType) {
        return objectMapper.readValue(jsonString, javaType);
    }

    static {
        SimpleModule module = new SimpleModule();
        // 注册反序列化器
        Map<Class<?>, JsonDeserializer<?>> deserializers = JacksonConfig.getDefaultDeserializer();
        for (Map.Entry<Class<?>, JsonDeserializer<?>> entry : deserializers.entrySet()) {
            addDeserializer(module, entry.getKey(), entry.getValue());
        }
        // 注册序列化器
        Map<Class<?>, JsonSerializer<?>> serializers = JacksonConfig.getDefaultSerializer();
        for (Map.Entry<Class<?>, JsonSerializer<?>> entry : serializers.entrySet()) {
            addSerializer(module, entry.getKey(), entry.getValue());
        }
        objectMapper.registerModule(module);
    }

    @SuppressWarnings("unchecked")
    private static <T> void addDeserializer(SimpleModule module, Class<?> type, JsonDeserializer<?> deserializer) {
        module.addDeserializer((Class<T>) type, (JsonDeserializer<? extends T>) deserializer);
    }

    @SuppressWarnings("unchecked")
    private static <T> void addSerializer(SimpleModule module, Class<?> type, JsonSerializer<?> serializer) {
        module.addSerializer((Class<? extends T>) type, (JsonSerializer<T>) serializer);
    }

}
