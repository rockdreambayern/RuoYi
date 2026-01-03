package com.scsentry.common.util.table;

import com.scsentry.common.enums.Result;
import com.scsentry.common.util.MetaDataCache;
import com.scsentry.model.testreport.data.TestContent;
import com.scsentry.model.testreport.data.TestContentCollection;
import com.scsentry.model.testreport.data.TestItem;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TestCaseTableGenerator extends CommonTableGenerator {

    private static final List<String> HEADERS = Arrays.asList("配置项名称", "测试类型", "测试用例", "已执行", "未执行", "已通过", "未通过");

    public static Tbl createTable(List<TestContentCollection> testContentCollectionList) {
        Tbl table = createBasicTable();

        Map<String, List<Integer>> map = new HashMap<>();
        map.put("测试情况", Arrays.asList(2, 3, 4, 5, 6));
        addHeaders(table, HEADERS, map);

        addRowData(table, (String) MetaDataCache.get("projectName"), testContentCollectionList);

        return table;
    }

    private static void addRowData(Tbl table, String systemName, List<TestContentCollection> testContentCollectionList) {
        long totalTestCaseCount = 0;
        long totalPassedTestCaseCount = 0;
        boolean firstContentRow = true;
        for (TestContentCollection testContentCollection : testContentCollectionList) {
            Tr tr = factory.createTr();
            List<TestItem> testItems = testContentCollection.getTestContents().stream().map(TestContent::getTestItems).flatMap(List::stream).collect(Collectors.toList());
            long testCaseCount = testItems.size();
            long passedTestCount = testItems.stream().filter(item -> item.getResult() == Result.PASS).count();
            totalTestCaseCount += testCaseCount;
            totalPassedTestCaseCount += passedTestCount;
            // 首列空占位
            if (firstContentRow) {
                tr.getContent().add(buildMergedCell(systemName));
            } else {
                tr.getContent().add(buildMergeFollowerCell());
            }

            tr.getContent().add(createCell(testContentCollection.getTestContentName()));
            tr.getContent().add(createCell(String.valueOf(testCaseCount)));
            // 默认所有用例都会执行
            tr.getContent().add(createCell(String.valueOf(testCaseCount)));
            tr.getContent().add(createCell("0"));
            tr.getContent().add(createCell(String.valueOf(passedTestCount))); // 版本占位
            tr.getContent().add(createCell(String.valueOf(testCaseCount - passedTestCount)));
            table.getContent().add(tr);

            firstContentRow = false;
        }

        //合计
        Tr tr = factory.createTr();
        // 首列空占位
        tr.getContent().add(buildMergeFollowerCell());
        tr.getContent().add(createCell("合计"));
        tr.getContent().add(createCell(String.valueOf(totalTestCaseCount)));
        tr.getContent().add(createCell(String.valueOf(totalTestCaseCount)));
        tr.getContent().add(createCell("0"));
        tr.getContent().add(createCell(String.valueOf(totalPassedTestCaseCount)));
        tr.getContent().add(createCell(String.valueOf(totalTestCaseCount - totalPassedTestCaseCount)));
        table.getContent().add(tr);
    }
}
