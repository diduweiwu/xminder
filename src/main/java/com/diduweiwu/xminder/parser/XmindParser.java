package com.diduweiwu.xminder.parser;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.diduweiwu.xminder.vo.Sheet;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.SneakyThrows;

import java.util.Collections;
import java.util.List;

/**
 * @author test
 */
public class XmindParser {

    /**
     * 读取文件目录,解析xmind文件成sheet列表
     *
     * @param filePath
     * @return
     */
    @SneakyThrows
    public List<Sheet> parse(String filePath) {
        String contentJson = this.parseForJsonContent(filePath);

        // 内容为空,返回空的
        if (StrUtil.isBlank(contentJson)) {
            return Collections.emptyList();
        }

        return new JsonMapper().readValue(contentJson, new TypeReference<List<Sheet>>() {
        });
    }

    /**
     * 解析并分析content内容
     *
     * @param filePath
     * @return
     */
    public String parseForJsonContent(String filePath) {
        // 先执行解压和获取content.xml/content.json数据的操作
        // 获取压缩文件里面的content.xml和content.json(该文件仅zen版本有)
        UnZipper.Result unzipResult = new UnZipper().extract(filePath);

        // 获取统一处理后的content.json文件,content.xml文件会被转换为content.json格式
        return unzipResult.fetchContentJson();
    }

    /**
     * 静态方法调用执行解析操作
     *
     * @param filePath
     * @return
     */
    public static String parseFileForJsonContent(String filePath) {
        return new XmindParser().parseForJsonContent(filePath);
    }

    /**
     * 静态方法调用执行解析操作
     *
     * @param filePath
     * @return
     */
    public static List<Sheet> parseFile(String filePath) {
        return new XmindParser().parse(filePath);
    }

    /**
     * 静态方法调用执行解析操作并获取第一个
     *
     * @param filePath
     * @return
     */
    public static Sheet parseFileForFirstSheet(String filePath) {
        return CollUtil.getFirst(new XmindParser().parse(filePath));
    }

}
