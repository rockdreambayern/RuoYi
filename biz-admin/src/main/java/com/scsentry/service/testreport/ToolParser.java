package com.scsentry.service.testreport;

import com.scsentry.common.util.table.TableFinder;
import com.scsentry.model.testreport.data.Tool;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;

import java.util.ArrayList;
import java.util.List;

public class ToolParser {

    private static final TextExtractor textExtractor = new TextExtractor();

    public List<Tool> parse(MainDocumentPart mainDocumentPart) {
        Tbl table = TableFinder.findByBookMark(mainDocumentPart, "tool_table");

        boolean isHeader = true;
        List<Tool> tools = new ArrayList<>();
        for (Object rowObj : table.getContent()) {
            // 跳过表头
            if (isHeader) {
                isHeader = false;
                continue;
            }
            Tr tr = (Tr) XmlUtils.unwrap(rowObj);
            List<Object> cells = tr.getContent();

            String type = textExtractor.extractText(cells.get(0));
            String name = textExtractor.extractText(cells.get(1));
            String version = textExtractor.extractText(cells.get(2));
            String usage = textExtractor.extractText(cells.get(3));
            String comment = textExtractor.extractText(cells.get(4));

            Tool tool = new Tool();
            tool.setType(type);
            tool.setName(name);
            tool.setVersion(version);
            tool.setUsage(usage);
            if (StringUtils.isNoneBlank(comment)) {
                tool.setComment(comment);
            }
            tools.add(tool);
        }
        return tools;
    }
}
