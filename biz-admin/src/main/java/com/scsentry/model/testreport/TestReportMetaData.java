package com.scsentry.model.testreport;

import com.ruoyi.common.exception.base.BaseException;
import com.scsentry.common.enums.TestParameterType;
import com.scsentry.common.util.MetaDataCache;
import com.scsentry.model.testreport.data.*;
import com.scsentry.model.testreport.data.System;
import com.scsentry.service.testreport.*;
import lombok.Data;
import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.SdtElement;
import org.docx4j.wml.SdtPr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class TestReportMetaData {

    private Map<String, Object> metadata;

    private BinaryPartAbstractImage image;

    private Environment environment;

    List<TestContentCollection> testContentCollectionList = new ArrayList<>();

    private static final Logger log = LoggerFactory.getLogger(TestReportMetaData.class);

    private static final TextExtractor textExtractor = new TextExtractor();

    public static TestReportMetaData parse(File file) {
        WordprocessingMLPackage pkg;
        try {
            pkg = WordprocessingMLPackage.load(file);
        } catch (Exception e) {
            throw new BaseException("解析报告模板失败");
        }
        MainDocumentPart mainDocumentPart = pkg.getMainDocumentPart();
        Map<String, Object> metadata = parse(mainDocumentPart);
        process(metadata);

        EnvironmentParser environmentParser = new EnvironmentParser();
        Environment environment = environmentParser.parse(mainDocumentPart);

        SystemParser systemParser = new SystemParser();
        List<System> systems = systemParser.parse(mainDocumentPart);
        environment.setSystems(systems);

        ToolParser toolParser = new ToolParser();
        List<Tool> tools = toolParser.parse(mainDocumentPart);
        environment.setTools(tools);

        TestContentsParser testContentsParser = new TestContentsParser();
        List<TestContentCollection> testContentCollectionList = new ArrayList<>();

        for (TestParameterType testParameterType : ((TestParametersParser.TestParameters)metadata.get("testParameters")).getParameters()) {
            List<TestContent> testContents = testContentsParser.parseTable(mainDocumentPart, testParameterType);
            TestContentCollection funcTestContentCollection = new TestContentCollection(testParameterType, testContents);
            testContentCollectionList.add(funcTestContentCollection);
        }

        BinaryPartAbstractImage image = parsePicture(mainDocumentPart);


        MetaDataCache.put(metadata);

        TestReportMetaData testReportMetaData = new TestReportMetaData();
        testReportMetaData.setMetadata(metadata);
        testReportMetaData.setEnvironment(environment);
        testReportMetaData.setImage(image);
        testReportMetaData.setTestContentCollectionList(testContentCollectionList);
        return testReportMetaData;
    }

    public void check() {

    }

    private static BinaryPartAbstractImage parsePicture(MainDocumentPart mdp) {
        for (Relationship rel : mdp.getRelationshipsPart()
                .getRelationships()
                .getRelationship()) {

            Part part = mdp.getRelationshipsPart().getPart(rel);

            if (part instanceof BinaryPartAbstractImage) {
                return (BinaryPartAbstractImage)part;
            }
        }

        return null;
    }

    private static void process(Map<String, Object> metadata) {
        LocalDate beginDate = null;
        DateTimeFormatter inputFormatter =
                DateTimeFormatter.ofPattern("yyyy年M月d日");
        DateTimeFormatter outputFormatter =
                DateTimeFormatter.ofPattern("yyyy.MM.dd");
        if (null != metadata.get("receivedDate")) {
            String receiveDate = (String)metadata.get("receivedDate");


            beginDate = LocalDate.parse(receiveDate, inputFormatter);
            String result = beginDate.format(outputFormatter);
            metadata.put("receivedDate", result);
            metadata.put("beginDateZh", receiveDate);
        }

        if (null != metadata.get("endDate")) {

            LocalDate endDate = LocalDate.parse((String)metadata.get("endDate"), inputFormatter);
            String endDateDot = endDate.format(outputFormatter);
            String testDateRange;
            String testDateRangeZh;
            if (endDate.equals(beginDate)) {
                testDateRange = (String)metadata.get("receivedDate");
                testDateRangeZh = endDate.format(inputFormatter);
            } else {
                testDateRange = metadata.get("receivedDate") + "-" + endDateDot;
                testDateRangeZh = "从" + beginDate.format(inputFormatter) + "至" + endDate.format(inputFormatter);
            }

            String endDateZh = endDate.format(inputFormatter);

            metadata.put("testDateRange", testDateRange);
            metadata.put("testDateRangeZh", testDateRangeZh);
            metadata.put("endDateDot", endDateDot);
            metadata.put("endDateZh", endDateZh);
        }

        TestParametersParser testParametersParser = new TestParametersParser();
        testParametersParser.parse(metadata);
    }

    /**
     * 解析 Word 文档中的元数据
     */
    private static Map<String, Object> parse(MainDocumentPart mdp) {
        try {
            Map<String, Object> result = new HashMap<>();

            // 获取所有内容控件（SDT）
            List<Object> sdts =
                    mdp.getJAXBNodesViaXPath("//w:sdt", true);

            for (Object obj : sdts) {
                Object stdRun = XmlUtils.unwrap(obj);
                if (stdRun instanceof SdtElement) {
                    SdtElement sdtElement = (SdtElement)stdRun;
                    parseSingleSdt(sdtElement, result);
                }
            }

            return result;
        } catch (Exception e) {
            log.error("解析报告模板元数据失败", e);
            throw new BaseException("解析报告模板元数据失败");
        }
    }

    /**
     * 解析单个 SDT
     */
    private static void parseSingleSdt(
            SdtElement sdt,
            Map<String, Object> result) {

        SdtPr pr = sdt.getSdtPr();

        if (pr == null || pr.getTag() == null) {
            return;
        }

        String key = pr.getTag().getVal();
        if (key == null || key.trim().isEmpty()) {
            return;
        }

        if ("projectDescription".equals(key)) {
            List<String> texts = textExtractor.extractTexts(sdt);
            result.put(key, texts);
        } else {
            String text = textExtractor.extractText(sdt);
            result.put(key, text);
        }
    }
}
