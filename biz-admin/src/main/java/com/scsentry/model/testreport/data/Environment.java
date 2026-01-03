package com.scsentry.model.testreport.data;

import com.scsentry.common.enums.BookMark;
import com.scsentry.common.util.ContentAdder;
import com.scsentry.common.util.TableSerialNo;
import com.scsentry.common.util.table.EnvironmentTableGenerator;
import com.scsentry.common.util.table.RequirementTableGenerator;
import com.scsentry.common.util.table.SystemTableGenerator;
import com.scsentry.common.util.table.ToolTableGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;

import java.util.List;

@AllArgsConstructor
@Data
public class Environment {

    private List<Hardware> hardwares;

    private List<System> systems;

    private List<Tool> tools;

    private static final String SOFT_HARD_ENVIRONMENT = "软/硬件环境配置";

    private static final String SOFTWARE_TO_TEST = "被测软件程序清单";

    private static final String TEST_TOOL = "使用工具";

    public void generateTable(WordprocessingMLPackage wordprocessingMLPackage)  {
        MainDocumentPart mainDocumentPart = wordprocessingMLPackage.getMainDocumentPart();

        int tableSerialNo = TableSerialNo.get();
        ContentAdder.addByBookMark(mainDocumentPart, RequirementTableGenerator.createTableDesc("表" + tableSerialNo, SOFT_HARD_ENVIRONMENT), BookMark.ENVIRONMENT);
        ContentAdder.addByBookMark(mainDocumentPart, RequirementTableGenerator.createCaptionP("表" + tableSerialNo, SOFT_HARD_ENVIRONMENT), BookMark.ENVIRONMENT);
        ContentAdder.addByBookMark(wordprocessingMLPackage.getMainDocumentPart(), EnvironmentTableGenerator.createTable(hardwares), BookMark.ENVIRONMENT);

        tableSerialNo = TableSerialNo.get();
        ContentAdder.addByBookMark(mainDocumentPart, RequirementTableGenerator.createTableDesc("表" + tableSerialNo, SOFT_HARD_ENVIRONMENT), BookMark.SOFTWARE_TO_TEST);
        ContentAdder.addByBookMark(mainDocumentPart, RequirementTableGenerator.createCaptionP("表" + tableSerialNo, SOFT_HARD_ENVIRONMENT), BookMark.SOFTWARE_TO_TEST);
        ContentAdder.addByBookMark(wordprocessingMLPackage.getMainDocumentPart(), SystemTableGenerator.createTable(systems), BookMark.SOFTWARE_TO_TEST);

        tableSerialNo = TableSerialNo.get();
        ContentAdder.addByBookMark(mainDocumentPart, RequirementTableGenerator.createTableDesc("表" + tableSerialNo, "测试人员配置情况"), BookMark.STUFF_CONFIG);
        ContentAdder.addByBookMark(mainDocumentPart, RequirementTableGenerator.createCaptionP("表" + tableSerialNo, "测试人员配置"), BookMark.STUFF_CONFIG);

        tableSerialNo = TableSerialNo.get();
        ContentAdder.addByBookMark(mainDocumentPart, RequirementTableGenerator.createTableDesc("表" + tableSerialNo, TEST_TOOL), BookMark.TOOL);
        ContentAdder.addByBookMark(mainDocumentPart, RequirementTableGenerator.createCaptionP("表" + tableSerialNo, TEST_TOOL), BookMark.TOOL);
        ContentAdder.addByBookMark(wordprocessingMLPackage.getMainDocumentPart(), ToolTableGenerator.createTable(tools), BookMark.TOOL);

    }
}
