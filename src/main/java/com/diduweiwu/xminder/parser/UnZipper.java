package com.diduweiwu.xminder.parser;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

/**
 * 解压
 *
 * @author test
 */
public class UnZipper {
    @Data
    @Builder
    public static class Result {
        String contentJson;
        String contentXml;

        /**
         * 判断是否为xmind-zen格式,当存在contentJson文件的时候为xmind-zen
         *
         * @return
         */
        public boolean isZenVersion() {
            return StrUtil.isNotBlank(this.contentJson);
        }

        /**
         * xminder-zen格式的文件,数据是存储在content.json文件里面
         * 所以针对xmind8格式文件,将content.xml转换为json格式
         * 如果只想获取原始的contentJson,使用getContentJson就行了
         *
         * @return
         */
        @SneakyThrows
        public String fetchContentJson() {
            if (StrUtil.isNotBlank(this.contentJson)) {
                return contentJson;
            }

            if (StrUtil.isNotBlank(this.contentXml)) {
                // legacy格式的xmind,第一个数组节点是一些基础信息,第二个节点开始才是sheet画布,所以需要skip一下
                return JSONUtil.toJsonPrettyStr(new XmlMapper().readValue(this.contentXml, JSONArray.class).stream().skip(1).collect(Collectors.toList()));
            }

            return null;
        }
    }

    /**
     * 仅做解压操作
     *
     * @param filePath
     * @return
     */
    public Result extract(String filePath) {
        Result.ResultBuilder builder = Result.builder();
        try (ZipFile zipFile = ZipUtil.toZipFile(FileUtil.file(filePath), StandardCharsets.UTF_8)) {
            ZipUtil.read(zipFile, zipEntry -> {
                // 读取到content.xml,存储起来
                if ("content.xml".equals(zipEntry.getName())) {
                    try (InputStream zipStream = ZipUtil.getStream(zipFile, zipEntry)) {
                        builder.contentXml(new String(IoUtil.readBytes(zipStream)));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                // 读取到content.json,存储起来,说明当前读取的xmind是zen格式
                if ("content.json".equals(zipEntry.getName())) {
                    try (InputStream zipStream = ZipUtil.getStream(zipFile, zipEntry)) {
                        builder.contentJson(new String(IoUtil.readBytes(zipStream)));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("解析失败!" + e.getLocalizedMessage());
        }

        return builder.build();
    }
}
