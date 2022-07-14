package com.diduweiwu.xminder.builder;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.xmind.core.ISheet;
import org.xmind.core.ITopic;
import org.xmind.core.IWorkbook;

@Setter
@Getter
@RequiredArgsConstructor
public class SheetBuilder {
    private String title;
    private IWorkbook workbook;
    private TopicBuilder topic;

    /**
     * 创建一个Sheet对象,对应的就是Xmind里面的一个画布
     * 为了数据统一,我们允许多个画布,但是每个画布里面仅允许一个根节点
     *
     * @param title
     * @param workbook
     * @return
     */
    public static SheetBuilder create(String title, IWorkbook workbook) {
        SheetBuilder builder = new SheetBuilder();
        builder.title = title;
        builder.workbook = workbook;
        return builder;
    }

    /**
     * 创建sheet的根Topic节点
     *
     * @param title
     * @return
     */
    public TopicBuilder createChildNode(String title) {
        Assert.isNull(this.topic, "抱歉,目前一个Sheet仅允许一个根节点");
        TopicBuilder builder = TopicBuilder.create(title, this.workbook);
        this.topic = builder;

        return builder;
    }

    /**
     * 获取构建成功后的Topic节点
     *
     * @return
     */
    public ITopic fetchBuildTopic() {
        return this.topic.build();
    }

    /**
     * 执行构建Sheet操作
     *
     * @return
     */
    public ISheet build() {
        ISheet sheet = this.workbook.createSheet();
        sheet.replaceRootTopic(this.fetchBuildTopic());
        sheet.setTitleText(this.title);
        // 设置主sheet的名称为当前sheet名称
        if (StrUtil.isBlank(this.workbook.getPrimarySheet().getTitleText())) {
            this.workbook.getPrimarySheet().setTitleText(this.title);
        }

        return sheet;
    }
}
