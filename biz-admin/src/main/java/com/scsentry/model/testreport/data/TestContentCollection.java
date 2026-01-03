package com.scsentry.model.testreport.data;

import com.scsentry.common.enums.BookMark;
import com.scsentry.common.enums.TestParameterType;
import com.scsentry.common.util.ContentAdder;
import com.scsentry.common.util.LevelNumbering;
import com.scsentry.common.util.TableSerialNo;
import com.scsentry.common.util.table.RequirementTableGenerator;
import com.scsentry.common.util.table.TestContentTableGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;

import java.util.List;

@AllArgsConstructor
@Data
public class TestContentCollection {

    private TestParameterType testParameterType;

    private List<TestContent> testContents;

    public String getTestContentName() {
        return testParameterType.getDescription();
    }

    public void generateTable(WordprocessingMLPackage wordprocessingMLPackage) {
        MainDocumentPart mainDocumentPart = wordprocessingMLPackage.getMainDocumentPart();
        int tableSerialNo = TableSerialNo.get();
        String testContentName  = getTestContentName();
        String numId = LevelNumbering.addMultilevelNumbering(wordprocessingMLPackage);
        ContentAdder.addByBookMark(mainDocumentPart, LevelNumbering.createHeadingWithNumber(1, testContentName, numId), BookMark.TEST_CONTENTS);
        ContentAdder.addByBookMark(mainDocumentPart, RequirementTableGenerator.createTableDesc("表" + tableSerialNo, testContentName + "测试内容"), BookMark.TEST_CONTENTS);
        ContentAdder.addByBookMark(mainDocumentPart, RequirementTableGenerator.createCaptionP("表" + tableSerialNo, testContentName + "测试内容"), BookMark.TEST_CONTENTS);
        ContentAdder.addByBookMark(mainDocumentPart, TestContentTableGenerator.createTable(testContents), BookMark.TEST_CONTENTS);
    }
}
