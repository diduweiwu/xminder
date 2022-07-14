package com.diduweiwu.xminder.rule;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.diduweiwu.xminder.vo.Node;

public class SecondLevelLengthRule implements RuleMatcher {
    @Override
    public boolean match(Node node) {
        Integer level = node.getLevel();
        if (level == 2) {
            Assert.isTrue(StrUtil.length(node.getTitle()) >= 5, "第二层级的节点名称长度必须>=5");
        }

        // 调用接口默认方法,或递归对所有子节点都执行校验
        return RuleMatcher.super.match(node);
    }
}
