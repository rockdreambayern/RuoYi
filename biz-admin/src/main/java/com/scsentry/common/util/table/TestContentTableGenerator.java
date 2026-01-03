package com.scsentry.common.util.table;

import com.scsentry.model.testreport.data.TestContent;
import com.scsentry.model.testreport.data.TestItem;
import org.docx4j.wml.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestContentTableGenerator extends CommonTableGenerator {

    private static final List<String> HEADERS = Arrays.asList("序号", "需求项", "检测项", "检测内容", "检测结论", "备注");

    public static Tbl createTable(List<TestContent> testContents) {
        Tbl table = createBasicTable();

        Map<String, List<Integer>> map = new HashMap<>();
        map.put("**需求规格说明**", Arrays.asList(0, 1));
        map.put("**检测项**", Arrays.asList(2, 3, 4, 5));

        addHeaders(table, HEADERS, map);

        addRowData(table, testContents);

        return table;
    }

    private static void addRowData(Tbl table, List<TestContent> testContents) {
        int index = 1;
        for (TestContent testContent : testContents) {
            List<TestItem> testItems = testContent.getTestItems();
            index = addTestContentWithTestItem(table, testContent, testItems, index);
        }
    }

    private static int addTestContentWithTestItem(Tbl table, TestContent testContent, List<TestItem> testItems, int index) {
        boolean isFirstRow = true;

        for (TestItem testItem : testItems) {
            Tr tr = factory.createTr();

            tr.getContent().add(createMergedCellStart(String.valueOf(index)));
            // 硬件相关字段（序号、名称、配置）- 纵向合并
            addMergedTestContentCells(tr, testContent, isFirstRow);

            // 软件相关字段（名称、版本）- 不合并
            tr.getContent().add(createCell(testItem.getItem()));
            tr.getContent().add(createCell(testItem.getContent()));
            tr.getContent().add(createCell(testItem.getResult().getDesc()));
            tr.getContent().add(createCell(testItem.getComment()));

            table.getContent().add(tr);
            isFirstRow = false;
            index++;
        }

        return index;
    }

    /**
     * 添加硬件相关的合并单元格（序号、名称、配置）
     */
    private static void addMergedTestContentCells(Tr tr, TestContent testContent, boolean isFirstRow) {
        if (isFirstRow) {
            tr.getContent().add(createMergedCellStart(testContent.getRequirement()));
        } else {
            tr.getContent().add(createMergedCellContinue());
        }
    }

    private static Tc createMergedCellStart(String text) {
        Tc tc = createCell(text);

        TcPr tcPr = tc.getTcPr();
        if (tcPr == null) tcPr = factory.createTcPr();

        TcPrInner.VMerge vMerge = new TcPrInner.VMerge();
        vMerge.setVal("restart");
        tcPr.setVMerge(vMerge);

        tc.setTcPr(tcPr);
        return tc;
    }

    private static Tc createMergedCellContinue() {
        Tc tc = createCell("");

        TcPr tcPr = tc.getTcPr();
        if (tcPr == null) tcPr = factory.createTcPr();

        TcPrInner.VMerge vMerge = new TcPrInner.VMerge();
        // val 不设置，表示 continue
        tcPr.setVMerge(vMerge);

        tc.setTcPr(tcPr);
        return tc;
    }
}
