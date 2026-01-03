package com.scsentry.common.util.table;

import com.scsentry.common.enums.Result;
import com.scsentry.model.testreport.data.TestContentCollection;
import com.scsentry.model.testreport.data.TestItem;
import com.scsentry.model.testreport.data.TestContent;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BugTableByTypeGenerator extends CommonTableGenerator {

    private static final List<String> HEADERS = Arrays.asList("测试类型", "缺陷数量");

    public static Tbl createTable(List<TestContentCollection> testContentCollectionList) {
        Tbl table = createBasicTable();

        addHeaders(table, HEADERS);

        addRowData(table, testContentCollectionList);

        return table;
    }

    private static void addRowData(Tbl table, List<TestContentCollection> testContentCollectionList) {
        long totalFailedTestCaseCount = 0;
        for (TestContentCollection testContentCollection : testContentCollectionList) {
            Tr tr = factory.createTr();
            List<TestItem> testItems = testContentCollection.getTestContents().stream().map(TestContent::getTestItems).flatMap(List::stream).collect(Collectors.toList());
            long failedTestCount = testItems.stream().filter(item -> item.getResult() == Result.FAIL).count();
            totalFailedTestCaseCount += failedTestCount;
            tr.getContent().add(createCell(testContentCollection.getTestContentName()));
            tr.getContent().add(createCell(String.valueOf(failedTestCount)));
            table.getContent().add(tr);
        }

        //合计
        Tr tr = factory.createTr();
        tr.getContent().add(createCell("合计"));
        tr.getContent().add(createCell(String.valueOf(totalFailedTestCaseCount)));
        table.getContent().add(tr);
    }
}
