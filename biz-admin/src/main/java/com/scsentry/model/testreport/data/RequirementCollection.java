package com.scsentry.model.testreport.data;

import com.scsentry.common.enums.BookMark;
import com.scsentry.common.util.ContentAdder;
import com.scsentry.common.util.LevelNumbering;
import com.scsentry.common.util.TableSerialNo;
import com.scsentry.common.util.table.RequirementTableGenerator;
import lombok.AllArgsConstructor;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;

import java.util.List;

@AllArgsConstructor
public class RequirementCollection {

    private String requirementName;

    private List<Requirement> requirements;

    public void generateTable(WordprocessingMLPackage wordprocessingMLPackage) {
        MainDocumentPart mainDocumentPart = wordprocessingMLPackage.getMainDocumentPart();
        int tableSerialNo = TableSerialNo.get();
        String numId = LevelNumbering.addMultilevelNumbering(wordprocessingMLPackage);
        ContentAdder.addByBookMark(mainDocumentPart, LevelNumbering.createHeadingWithNumber(2, requirementName, numId), BookMark.REQUIREMENTS);
        ContentAdder.addByBookMark(mainDocumentPart, RequirementTableGenerator.createTableDesc("表" + tableSerialNo, requirementName + "需求"), BookMark.REQUIREMENTS);
        ContentAdder.addByBookMark(mainDocumentPart, RequirementTableGenerator.createCaptionP("表" + tableSerialNo, requirementName + "需求表"), BookMark.REQUIREMENTS);
        ContentAdder.addByBookMark(mainDocumentPart, RequirementTableGenerator.createTable(requirements), BookMark.REQUIREMENTS);
    }
}
