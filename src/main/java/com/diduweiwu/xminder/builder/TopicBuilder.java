package com.diduweiwu.xminder.builder;


import cn.hutool.core.collection.CollUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.xmind.core.ITopic;
import org.xmind.core.ITopicExtension;
import org.xmind.core.IWorkbook;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
public class TopicBuilder {
    private String title;
    private List<String> markers;
    // 一些自定义的扩展属性
    private Map<String, String> extensions;
    private IWorkbook workbook;
    private List<TopicBuilder> topics;

    /**
     * 静态方法创建一个Topic对象,但是创建时候必须传入workbook
     * 如果通过sheetBuilder创建子Topic,则不需要,会自动传入
     *
     * @param title
     * @param workbook
     * @return
     */
    public static TopicBuilder create(String title, IWorkbook workbook) {
        TopicBuilder builder = new TopicBuilder();
        builder.title = title;
        builder.workbook = workbook;
        builder.topics = new LinkedList<>();
        builder.markers = new LinkedList<>();
        builder.extensions = new HashMap<>();
        return builder;
    }

    public TopicBuilder addMarker(String markerName) {
        this.markers.add(markerName);
        return this;
    }

    public TopicBuilder addExtension(String key, String value) {
        this.extensions.put(key, value);
        return this;
    }

    public TopicBuilder addExtensions(Map<String, String> extensions) {
        this.extensions.putAll(extensions);
        return this;
    }

    public TopicBuilder addMarkers(List<String> markerNames) {
        this.markers.addAll(markerNames);
        return this;
    }

    /**
     * 创建一个子节点
     *
     * @param title
     * @return
     */
    public TopicBuilder createChildNode(String title) {
        TopicBuilder builder = TopicBuilder.create(title, this.workbook);
        this.topics.add(builder);

        return builder;
    }

    /**
     * 创建一个子节点
     *
     * @param title
     * @param consumer
     * @return
     */
    public TopicBuilder createChildNode(String title, Consumer<TopicBuilder> consumer) {
        TopicBuilder builder = TopicBuilder.create(title, this.workbook);
        this.topics.add(builder);

        consumer.accept(builder);

        return builder;
    }

    /**
     * 获取构建完成后的子节点Topic
     *
     * @return
     */
    public List<ITopic> fetchBuildTopicList() {
        return this.topics.stream().map(TopicBuilder::build).collect(Collectors.toList());
    }

    /**
     * 执行构建逻辑
     *
     * @return
     */
    public ITopic build() {
        ITopic topic = this.buildSelf();
        this.buildChildren(topic, this.fetchBuildTopicList());
        return topic;
    }

    private ITopic buildSelf() {
        ITopic topic = this.workbook.createTopic();
        topic.setTitleText(this.title);
        // 从左至右的节点排布
        topic.setStructureClass("org.xminder.ui.logic.right");
        this.buildMarkers(topic, this.markers);
        this.buildExtensions(topic, this.extensions);

        return topic;
    }

    private ITopic buildChildren(ITopic topic, List<ITopic> childrenTopics) {
        for (ITopic childTopic : childrenTopics) {
            topic.add(childTopic);
        }
        return topic;
    }

    private void buildExtensions(ITopic topic, Map<String, String> extensions) {
        if (CollUtil.isEmpty(extensions)) {
            return;
        }
        ITopicExtension content = topic.createExtension("custom");
        extensions.forEach((key, value) -> content.getContent().setAttribute(key, value));
    }

    private void buildMarkers(ITopic topic, List<String> markers) {
        if (CollUtil.isEmpty(markers)) {
            return;
        }
        for (String marker : markers) {
            topic.addMarker(marker);
        }
    }

    /**
     * 判断节点是否拥有某类标记节点
     *
     * @param markerName
     * @return
     */
    public boolean hashMarker(String markerName) {
        return CollUtil.contains(this.markers, markerName);
    }
}
