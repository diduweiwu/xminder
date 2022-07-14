package com.diduweiwu.xminder;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.RandomUtil;
import com.diduweiwu.xminder.builder.SheetBuilder;
import com.diduweiwu.xminder.builder.TopicBuilder;
import com.diduweiwu.xminder.builder.XmindBuilder;
import com.diduweiwu.xminder.constant.marker.MarkerFlag;
import com.diduweiwu.xminder.parser.XmindParser;
import com.diduweiwu.xminder.vo.Sheet;
import com.diduweiwu.xminder.vo.Topic;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.List;

@Slf4j
public class XmindParserTest {

    /**
     * 解析xmind8 格式文件
     */
    @Test
    public void testParseFile() {
        URL filePath = ResourceUtil.getResource("file/xmind-8.xmind");
        Sheet firstSheet = XmindParser.parseFileForFirstSheet(filePath.getPath());

        List<Topic> children = firstSheet.getTopicChildren();
        for (Topic child : children) {
            log.info("是否成功" + child.hashMarker(MarkerFlag.FLAG_GREEN));
        }

        System.out.println(firstSheet);
    }

    /**
     * 解析xmindzen 格式文件
     */
    @Test
    public void testParseZenFile() {
        URL filePath = ResourceUtil.getResource("file/xmind-8-to-zen.xmind");
        Sheet firstSheet = XmindParser.parseFileForFirstSheet(filePath.getPath());

        List<Topic> children = firstSheet.getTopicChildren();
        for (Topic child : children) {
            log.info("是否成功" + child.hashMarker(MarkerFlag.FLAG_GREEN));
        }

        System.out.println(firstSheet);
    }

    @Test
    public void testCreateFile() {
        XmindBuilder builder = XmindBuilder.builder();
        SheetBuilder sheet = builder.createSheet("第一个");
        SheetBuilder sheet2 = builder.createSheet("第二个");
        TopicBuilder node = sheet.createChildNode("我去").addMarker("flag-red")
                .addExtension("location", "热得很的程度");
        node.createChildNode("你去", child -> {
            child.setTitle("哈哈哈,我就不去");
        });

        TopicBuilder node2 = sheet2.createChildNode("我去2");

        String savedFilePath = "/var/tmp/" + RandomUtil.randomString(10) + ".xmind";
        builder.build(savedFilePath);
        log.info("保存成功: {}", savedFilePath);
    }
}
