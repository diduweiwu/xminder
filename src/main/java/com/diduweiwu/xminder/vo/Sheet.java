package com.diduweiwu.xminder.vo;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * sheet其实也可以抽象成一个root node,本质上属性与Topic节点没有太大差异,都统一为
 * id title children这三个属性即可
 *
 * @author test
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Sheet extends Topic {
    @JsonProperty("id")
    private String id;

    @JsonProperty("title")
    private String title;

    @JsonProperty(value = "topic", access = JsonProperty.Access.WRITE_ONLY)
    private Topic legacyTopic;

    @JsonProperty(value = "rootTopic", access = JsonProperty.Access.WRITE_ONLY)
    private Topic zenTopic;

    public List<Topic> getTopicChildren() {
        return ListUtil.of(ObjectUtil.defaultIfNull(this.legacyTopic, this.zenTopic));
    }
}
