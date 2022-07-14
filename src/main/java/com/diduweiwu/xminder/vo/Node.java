package com.diduweiwu.xminder.vo;

import java.util.List;

/**
 * xminder node节点接口
 *
 * @author test
 */
public interface Node {
    /**
     * 节点的id
     *
     * @return
     */
    String getId();

    /**
     * 节点的title名称(一般是思维导图上面的文本)
     *
     * @return
     */
    String getTitle();

    /**
     * 节点的子节点
     *
     * @return
     */
    List<? extends Topic> getTopicChildren();

    /**
     * 节点的marker标记
     *
     * @return
     */
    List<String> getTopicMarkers();

    /**
     * 获取节点的层级
     *
     * @return
     */
    Integer getLevel();
}
