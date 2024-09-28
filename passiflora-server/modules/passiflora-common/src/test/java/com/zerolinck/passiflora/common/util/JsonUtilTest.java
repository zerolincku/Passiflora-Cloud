package com.zerolinck.passiflora.common.util;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author 林常坤 on 2024/09/28
 */
public class JsonUtilTest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class User {
        public String name;
        public int age;
    }

    @Test
    void testToJson() {
        User user = new User("Alice", 30);
        String json = JsonUtil.toJson(user);
        assertTrue(json.contains("\"name\":\"Alice\""));
        assertTrue(json.contains("\"age\":30"));
    }

    @Test
    void testToPrettyJson() {
        User user = new User("Bob", 25);
        String json = JsonUtil.toPrettyJson(user);
        assertTrue(json.contains("\"name\" : \"Bob\""));
        assertTrue(json.contains("\"age\" : 25"));
        assertTrue(json.contains("\n")); // 确保有换行，表示美化
    }

    @Test
    void testConvertValue() {
        String json = "{\"name\":\"Charlie\",\"age\":35}";
        User user = JsonUtil.convertValue(json, User.class);
        assertEquals("Charlie", user.name);
        assertEquals(35, user.age);
    }

    @Test
    void testConvertToList() {
        String json = "[{\"name\":\"David\",\"age\":40},{\"name\":\"Eve\",\"age\":45}]";
        List<User> users = JsonUtil.convertToList(json, User.class);
        assertEquals(2, users.size());
        assertEquals("David", users.get(0).name);
        assertEquals(45, users.get(1).age);
    }

    @Test
    void testConvertToMap() {
        User user = new User("Frank", 50);
        Map<String, Object> map = JsonUtil.convertToMap(user);
        assertEquals("Frank", map.get("name"));
        assertEquals(50, map.get("age"));
    }

    @Test
    void testConvertValueFromMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "Grace");
        map.put("age", 55);
        User user = JsonUtil.convertValue(map, User.class);
        assertEquals("Grace", user.name);
        assertEquals(55, user.age);
    }

    @Test
    void testConvertToMapList() {
        List<User> users = Arrays.asList(
                new User("Henry", 60),
                new User("Ivy", 65)
        );
        List<Map<String, Object>> mapList = JsonUtil.convertToMapList(users);
        assertEquals(2, mapList.size());
        assertEquals("Henry", mapList.get(0).get("name"));
        assertEquals(65, mapList.get(1).get("age"));
    }

    @Test
    void testConvertToListFromMapList() {
        List<Map<String, Object>> mapList = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("name", "Jack");
        map1.put("age", 70);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("name", "Kate");
        map2.put("age", 75);
        mapList.add(map1);
        mapList.add(map2);

        List<User> users = JsonUtil.convertToList(mapList, User.class);
        assertEquals(2, users.size());
        assertEquals("Jack", users.get(0).name);
        assertEquals(75, users.get(1).age);
    }

    @Test
    void testReadTree() {
        String json = "{\"name\":\"Lucy\",\"age\":80}";
        JsonNode jsonNode = JsonUtil.readTree(json);
        assertEquals("Lucy", jsonNode.get("name").asText());
        assertEquals(80, jsonNode.get("age").asInt());
    }
}
