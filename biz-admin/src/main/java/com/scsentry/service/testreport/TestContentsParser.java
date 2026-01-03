package com.scsentry.service.testreport;

import com.scsentry.common.enums.Result;
import com.scsentry.common.enums.SeverityLevel;
import com.scsentry.common.enums.TestParameterType;
import com.scsentry.common.util.table.TableFinder;
import com.scsentry.model.testreport.data.TestContent;
import com.scsentry.model.testreport.data.TestItem;
import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;

import java.util.ArrayList;
import java.util.List;

public class TestContentsParser {

    private static final TextExtractor textExtractor = new TextExtractor();

    private TestParameterType type;

    public List<TestContent> parseTable(MainDocumentPart mdp, TestParameterType type) {

        String parameter = type.getCode();

        Tbl targetTbl = TableFinder.findByBookMark(mdp, String.format("%s_table", parameter));

        List<TestContent> testContents = new ArrayList<>();

        boolean isHeader = true;

        TestContent currentContent = null;
        String lastRequirement = null;
        String lastRequirementContent = null;

        for (Object rowObj : targetTbl.getContent()) {

            // 跳过表头
            if (isHeader) {
                isHeader = false;
                continue;
            }

            Tr tr = (Tr) XmlUtils.unwrap(rowObj);
            List<Object> cells = tr.getContent();

            // ===== 取每一列的文本 =====
            String requirement = getCellText(cells, 0);
            String requirementContent = getCellText(cells, 1);
            String item = getCellText(cells, 2);
            String itemContent = getCellText(cells, 3);
            String severityDesc = getCellText(cells, 4);
            String resultDesc = getCellText(cells, 5);
            String comment = getCellText(cells, 6);

            // ===== 处理“需求项 / 需求内容”合并 =====
            boolean newRequirement = false;

            if (requirement != null && !requirement.trim().isEmpty()) {
                lastRequirement = requirement;
                newRequirement = true;
            }

            if (requirementContent != null && !requirementContent.trim().isEmpty()) {
                lastRequirementContent = requirementContent;
            }

            // ===== 新建 TestContent =====
            if (newRequirement || currentContent == null) {
                currentContent = new TestContent();
                currentContent.setRequirement(lastRequirement);
                currentContent.setContent(lastRequirementContent);
                currentContent.setTestItems(new ArrayList<>());
                testContents.add(currentContent);
            }

            // ===== 构建 TestItem =====
            TestItem testItem = new TestItem();
            testItem.setItem(item);
            testItem.setContent(itemContent);
            testItem.setSeverityLevel(SeverityLevel.findByDesc(severityDesc));
            testItem.setResult(Result.findByDesc(resultDesc));

            if (comment != null && !comment.trim().isEmpty()) {
                testItem.setComment(comment);
            }

            currentContent.getTestItems().add(testItem);
        }

        return testContents;
    }

    private String getCellText(List<Object> cells, int index) {
        if (index >= cells.size()) {
            return null;
        }
        Object obj = XmlUtils.unwrap(cells.get(index));
        if (!(obj instanceof Tc)) {
            return null;
        }
        Tc tc = (Tc)obj;
        return textExtractor.extractText(tc);
    }
}
