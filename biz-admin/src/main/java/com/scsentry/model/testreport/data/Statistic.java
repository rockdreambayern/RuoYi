package com.scsentry.model.testreport.data;

import com.scsentry.common.enums.BookMark;
import com.scsentry.common.enums.Result;
import com.scsentry.common.util.ContentAdder;
import com.scsentry.common.util.TableSerialNo;
import com.scsentry.common.util.table.BugTableByLevelGenerator;
import com.scsentry.common.util.table.BugTableByTypeGenerator;
import com.scsentry.common.util.table.RequirementTableGenerator;
import com.scsentry.common.util.table.TestCaseTableGenerator;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.*;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
public class Statistic {

    private static final String TEST_CASE = "测试用例统计";

    private static final String BUG_BY_TYPE = "基于测试类型的缺陷分布统计表";

    private static final String BUG_BY_LEVEL = "基于缺陷等级的缺陷分布统计表";

    private List<TestContentCollection> testContentCollectionList;

    private static final String FONT_TIMES_NEW_ROMAN = "Times New Roman";

    private static final RFonts FONTS_TIMES_NEW_ROMAN;
    static
    {
        FONTS_TIMES_NEW_ROMAN = Context.getWmlObjectFactory().createRFonts();
        FONTS_TIMES_NEW_ROMAN.setAscii(FONT_TIMES_NEW_ROMAN);
        FONTS_TIMES_NEW_ROMAN.setHAnsi(FONT_TIMES_NEW_ROMAN);
        FONTS_TIMES_NEW_ROMAN.setEastAsia(FONT_TIMES_NEW_ROMAN);
    }

    public void generateTestCaseTable(WordprocessingMLPackage wordprocessingMLPackage) throws Exception {
        MainDocumentPart mainDocumentPart = wordprocessingMLPackage.getMainDocumentPart();
        int tableSerialNo = TableSerialNo.get();
        ContentAdder.addByBookMark(mainDocumentPart, RequirementTableGenerator.createTableDesc("表" + tableSerialNo, TEST_CASE), BookMark.TEST_CASE_STAT);
        ContentAdder.addByBookMark(mainDocumentPart, RequirementTableGenerator.createCaptionP("表" + tableSerialNo, TEST_CASE), BookMark.TEST_CASE_STAT);
        ContentAdder.addByBookMark(mainDocumentPart, TestCaseTableGenerator.createTable(testContentCollectionList), BookMark.TEST_CASE_STAT);

        tableSerialNo = TableSerialNo.get();

        ContentAdder.addByBookMark(mainDocumentPart, RequirementTableGenerator.createTableDesc("表" + tableSerialNo, BUG_BY_TYPE), BookMark.BUG_TABLE_BY_TYPE);
        ContentAdder.addByBookMark(mainDocumentPart, RequirementTableGenerator.createCaptionP("表" + tableSerialNo, BUG_BY_TYPE), BookMark.BUG_TABLE_BY_TYPE);
        ContentAdder.addByBookMark(mainDocumentPart, BugTableByTypeGenerator.createTable(testContentCollectionList), BookMark.BUG_TABLE_BY_TYPE);

        tableSerialNo = TableSerialNo.get();

        ContentAdder.addByBookMark(mainDocumentPart, RequirementTableGenerator.createTableDesc("表" + tableSerialNo, BUG_BY_LEVEL), BookMark.BUG_TABLE_BY_LEVEL);
        ContentAdder.addByBookMark(mainDocumentPart, RequirementTableGenerator.createCaptionP("表" + tableSerialNo, BUG_BY_LEVEL), BookMark.BUG_TABLE_BY_LEVEL);
        ContentAdder.addByBookMark(mainDocumentPart, BugTableByLevelGenerator.createTable(testContentCollectionList), BookMark.BUG_TABLE_BY_LEVEL);

        addSummary(wordprocessingMLPackage);
    }

    private void addSummary(WordprocessingMLPackage wordprocessingMLPackage) throws Exception {
        MainDocumentPart mainDocumentPart = wordprocessingMLPackage.getMainDocumentPart();
        int index = 1;
        for (TestContentCollection testContentCollection : testContentCollectionList) {
            List<TestItem> bugs = testContentCollection.getTestContents().stream().map(TestContent::getTestItems).flatMap(List::stream).filter(item -> item.getResult() == Result.FAIL).collect(Collectors.toList());
            List<TestItem> totalTestItems = testContentCollection.getTestContents().stream().map(TestContent::getTestItems).flatMap(List::stream).collect(Collectors.toList());
            String text = String.format(
                    "%d）%s：测试组设计测试项%d个，执行测试项%d个，通过%d个；",
                    index,
                    testContentCollection.getTestContentName(),
                    totalTestItems.size(),
                    totalTestItems.size(),
                    totalTestItems.size() - bugs.size()
            );
            index++;

            ObjectFactory factory = Context.getWmlObjectFactory();

            P p = factory.createP();
            R r = factory.createR();

            // 字体 & 字号
            RPr rPr = factory.createRPr();
            rPr.setRFonts(FONTS_TIMES_NEW_ROMAN);
            r.setRPr(rPr);

            Text t = factory.createText();

            t.setValue(text);
            t.setSpace("preserve");

            r.getContent().add(t);
            p.getContent().add(r);

            ContentAdder.addByBookMark(mainDocumentPart, p, BookMark.SUMMARY);
        }
    }
}
