package com.diduweiwu.xminder.builder;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.xmind.core.Core;
import org.xmind.core.ISheet;
import org.xmind.core.IWorkbook;
import org.xmind.core.IWorkbookBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * xmind导出逻辑,由于没有支持xmind zen 格式的包,所以所有导出生成的xmind都用legacy文件模式
 * 因为xmind zen是支持打开legacy版本的xmind文件
 */
@RequiredArgsConstructor
public class XmindBuilder {
    private IWorkbookBuilder workbookBuilder;
    private IWorkbook workbook;
    private List<SheetBuilder> sheetList;

    public static XmindBuilder builder() {
        XmindBuilder xmindBuilder = new XmindBuilder();
        xmindBuilder.workbookBuilder = Core.getWorkbookBuilder();
        xmindBuilder.workbook = xmindBuilder.workbookBuilder.createWorkbook();
        xmindBuilder.sheetList = new LinkedList<>();

        return xmindBuilder;
    }

    public List<ISheet> fetchSheet() {
        return this.sheetList.stream().map(SheetBuilder::build).collect(Collectors.toList());
    }

    /**
     * 构建xmind文件
     *
     * @return
     */
    @SneakyThrows
    public void build(String saveFilePath) {
        List<ISheet> sheetList = this.fetchSheet();
        sheetList.stream().skip(1).forEachOrdered(s -> this.workbook.addSheet(s));
        this.workbook.getPrimarySheet().replaceRootTopic(sheetList.get(0).getRootTopic());
        this.workbook.save(saveFilePath);
    }

    /**
     * 新建一个sheet builder
     *
     * @param title
     * @return
     */
    public SheetBuilder createSheet(String title) {
        SheetBuilder newSheet = SheetBuilder.create(title, this.workbook);
        this.sheetList.add(newSheet);
        return newSheet;
    }
}
