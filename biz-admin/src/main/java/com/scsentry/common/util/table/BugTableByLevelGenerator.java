package com.scsentry.common.util.table;

import com.scsentry.common.enums.Result;
import com.scsentry.common.enums.SeverityLevel;
import com.scsentry.common.util.MetaDataCache;
import com.scsentry.model.testreport.data.TestContentCollection;
import com.scsentry.model.testreport.data.TestItem;
import com.scsentry.model.testreport.data.TestContent;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BugTableByLevelGenerator extends CommonTableGenerator {

    private static final List<String> HEADERS = Arrays.asList("配置项名称", "测试类型", "致命缺陷", "严重缺陷", "一般缺陷", "轻微缺陷", "合计");

    public static Tbl createTable(List<TestContentCollection> testContentCollectionList) {
        Tbl table = createBasicTable();

        addHeaders(table, HEADERS);

        addRowData(table, (String) MetaDataCache.get("projectName"), testContentCollectionList);

        return table;
    }

    private static void addRowData(Tbl table, String systemName, List<TestContentCollection> testContentCollectionList) {
        long totalCount = 0;
        long totalCriticalCount = 0;
        long totalMajorCount = 0;
        long totalMinorCount = 0;
        long totalTrivialCount = 0;

        boolean firstContentRow  = true;
        for (TestContentCollection testContentCollection : testContentCollectionList) {
            Tr tr = factory.createTr();
            List<TestItem> bugs = testContentCollection.getTestContents().stream().map(TestContent::getTestItems).flatMap(List::stream).filter(item -> item.getResult() == Result.FAIL).collect(Collectors.toList());
            long failedTestCount = bugs.size();
            totalCount += failedTestCount;
            Map<SeverityLevel, List<TestItem>> level2Items = bugs.stream().collect(Collectors.groupingBy(TestItem::getSeverityLevel));
            long criticalCount = level2Items.getOrDefault(SeverityLevel.CRITICAL, Collections.emptyList()).size();
            totalCriticalCount += criticalCount;
            long majorCount = level2Items.getOrDefault(SeverityLevel.MAJOR, Collections.emptyList()).size();
            totalMajorCount += majorCount;
            long minorCount = level2Items.getOrDefault(SeverityLevel.MINOR, Collections.emptyList()).size();
            totalMinorCount += minorCount;
            long trivialCount = level2Items.getOrDefault(SeverityLevel.TRIVIAL, Collections.emptyList()).size();
            totalTrivialCount += trivialCount;
            // 首列空占位
            if (firstContentRow) {
                tr.getContent().add(buildMergedCell(systemName));
            } else {
                tr.getContent().add(buildMergeFollowerCell());
            }
            tr.getContent().add(createCell(testContentCollection.getTestContentName()));
            tr.getContent().add(createCell(String.valueOf(criticalCount)));
            tr.getContent().add(createCell(String.valueOf(majorCount)));
            tr.getContent().add(createCell(String.valueOf(minorCount)));
            tr.getContent().add(createCell(String.valueOf(trivialCount)));
            tr.getContent().add(createCell(String.valueOf(failedTestCount)));
            table.getContent().add(tr);
            firstContentRow = false;
        }

        //合计
        Tr tr = factory.createTr();
        tr.getContent().add(buildMergeFollowerCell());
        tr.getContent().add(createCell("合计"));
        tr.getContent().add(createCell(String.valueOf(totalCriticalCount)));
        tr.getContent().add(createCell(String.valueOf(totalMajorCount)));
        tr.getContent().add(createCell(String.valueOf(totalMinorCount)));
        tr.getContent().add(createCell(String.valueOf(totalTrivialCount)));
        tr.getContent().add(createCell(String.valueOf(totalCount)));
        table.getContent().add(tr);
    }
}
