package com.diduweiwu.xminder.parser;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TopicExtensionParser {

    @SneakyThrows
    public static Map<String, String> parse(JsonNode extensions) {
        if (CollUtil.isEmpty(extensions)) {
            return MapUtil.empty();
        }
        if (!extensions.isArray()) {
            String content = extensions.get("extension").get("content").toPrettyString();
            return new JsonMapper().readValue(content, new TypeReference<Map<String, String>>() {
            });
        }

        // 是数组形式
        return JSONUtil.parseArray(extensions.toPrettyString())
                .stream()
                .map(c -> ((JSONObject) c).getJSONArray("content"))
                .flatMap(Collection::stream).map(c -> (JSONObject) c)
                .collect(Collectors.toMap(c -> c.getStr("name"), c -> c.getStr("content")));
    }
}
