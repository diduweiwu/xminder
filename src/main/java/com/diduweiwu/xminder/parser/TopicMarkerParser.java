package com.diduweiwu.xminder.parser;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TopicMarkerParser {

    /**
     * 解析标记节点数据,由于新旧xmind标记节点数据结构差异,所以都传入进来,进行判断分别解析
     *
     * @param legacyMarkerNode
     * @param zenMarkerNode
     * @return
     */
    public static List<String> parse(JsonNode legacyMarkerNode, JsonNode zenMarkerNode) {
        if (Objects.nonNull(legacyMarkerNode)) {
            return parseLegacyMarkerNode(legacyMarkerNode);
        }

        if (Objects.nonNull(zenMarkerNode)) {
            return parseZenMarkerNode(zenMarkerNode);
        }

        return Collections.emptyList();
    }

    /**
     * 解析xmind8版本marker标记数据
     *
     * @param legacyMarkerNode
     * @return
     */
    public static List<String> parseLegacyMarkerNode(JsonNode legacyMarkerNode) {
        JsonNode legacyMarkerRef = legacyMarkerNode.get("marker-ref");
        if (Objects.isNull(legacyMarkerRef)) {
            return Collections.emptyList();
        }

        String legacyMarkerRefJson = legacyMarkerRef.toPrettyString();
        // 不是数组,转换为数组格式
        if (!legacyMarkerRef.isArray()) {
            legacyMarkerRefJson = StrUtil.format("[{}]", legacyMarkerRefJson);
        }

        return JSONUtil.parseArray(legacyMarkerRefJson).stream().map(n -> ((JSONObject) n).getStr("marker-id")).collect(Collectors.toList());
    }

    /**
     * 解析xmind zen版本marker标记数据,这个版本的标记节点数据已经统一成了数组
     *
     * @param zenMarkerNode
     * @return
     */
    public static List<String> parseZenMarkerNode(JsonNode zenMarkerNode) {
        if (Objects.isNull(zenMarkerNode)) {
            return Collections.emptyList();
        }

        return JSONUtil.parseArray(zenMarkerNode.toPrettyString()).stream().map(n -> ((JSONObject) n).getStr("markerId")).collect(Collectors.toList());
    }

}
