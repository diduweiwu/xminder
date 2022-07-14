package com.diduweiwu.xminder.parser;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.diduweiwu.xminder.vo.Topic;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TopicChildrenParser {

    @SneakyThrows
    public static List<? extends Topic> parse(JsonNode childrenNode, Integer parentNodeLevel, String nodeType) {
        // children 为空,那么直接忽略结果了
        if (Objects.isNull(childrenNode)) {
            return Collections.emptyList();
        }

        // 分别尝试legacy模式解析/zen模式解析children节点数据
        String childrenContent = StrUtil.emptyToDefault(
                tryExtractLegacyChildrenNodeText(childrenNode, nodeType),
                tryExtractZenChildrenNodeText(childrenNode, nodeType)
        );

        if (JSONUtil.isTypeJSONArray(childrenContent)) {
            return new JsonMapper().readValue(childrenContent, new TypeReference<List<? extends Topic>>() {
            }).stream().peek(n -> n.setLevel(parentNodeLevel)).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    /**
     * 尝试将子节点里面的对应数据抽离成topic数组
     *
     * @param node
     * @return
     */
    public static List<Object> tryExtractChildrenNodeArray(Object node) {
        if (Objects.isNull(node)) {
            return Collections.emptyList();
        }

        // 万万没想到,topic在xmindzen版本格式时候,callout节点下是单个节点模式,但是xmind8版本格式,又是数组格式,所以这里需要判断处理下,统一转成数组
        String topicStr = ((JSONObject) node).getStr("topic");
        if (!JSONUtil.isTypeJSONArray(topicStr)) {
            return JSONUtil.parseArray(StrUtil.format("[{}]", topicStr));
        }
        return JSONUtil.parseArray(topicStr);
    }

    /**
     * 尝试legacy文件格式模式解析子节点
     * nodeType: 目前已知的是  attached 正常的子节点 callout 另类的子节点
     *
     * @param childrenNode
     * @param nodeType
     * @return
     */
    public static String tryExtractLegacyChildrenNodeText(JsonNode childrenNode, String nodeType) {
        if (Objects.isNull(childrenNode) || StrUtil.isBlank(nodeType)) {
            return null;
        }

        // children的attached节点不存在,说明是xmind8版本的文件
        JsonNode childrenTopics = childrenNode.get("topics");
        if (Objects.isNull(childrenTopics)) {
            return null;
        }

        // topics为数组,则需要根据nodeType保留对应类型的节点数据
        // nodeType: attached 正常节点 callout 标注节点
        if (childrenTopics.isArray()) {
            List<Object> childrenFilterAttached = JSONUtil.parseArray(childrenTopics.toPrettyString())
                    .stream()
                    // 过滤指定类型节点
                    .filter(c -> nodeType.equals(((JSONObject) c).getStr("type")))
                    // 尝试将节点里面的topic数据抽离成为数组
                    .map(TopicChildrenParser::tryExtractChildrenNodeArray)
                    // flatMap展开为汇总数据流
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            return JSONUtil.toJsonStr(childrenFilterAttached);
        }

        // topics为对象,则说明下一级的topic才是children数组
        // 至此.topics节点肯定不为数组
        JsonNode childrenTopic = childrenTopics.get("topic");
        if (Objects.isNull(childrenTopic)) {
            return null;
        }

        if (childrenTopic.isArray()) {
            return childrenTopic.toPrettyString();
        }

        // 如果是topic是对象,则组装成数组,统一数据结构
        return StrUtil.format("[{}]", childrenTopic.toPrettyString());
    }

    /**
     * 尝试zen文件格式模式解析子节点
     * nodeType: 目前已知的是  attached 正常的子节点 callout 另类的子节点
     *
     * @param childrenNode
     * @param nodeType
     * @return
     */
    public static String tryExtractZenChildrenNodeText(JsonNode childrenNode, String nodeType) {
        if (Objects.isNull(childrenNode) || StrUtil.isBlank(nodeType)) {
            return null;
        }

        // 先假设是zen版本思维导图
        JsonNode childrenAttached = childrenNode.get(nodeType);
        if (Objects.nonNull(childrenAttached) && childrenAttached.isArray()) {
            return childrenAttached.toPrettyString();
        }

        return null;
    }
}
