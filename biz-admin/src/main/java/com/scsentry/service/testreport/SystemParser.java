package com.scsentry.service.testreport;

import com.scsentry.common.util.table.TableFinder;
import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;
import com.scsentry.model.testreport.data.System;

import java.util.ArrayList;
import java.util.List;

public class SystemParser {

    private static final TextExtractor textExtractor = new TextExtractor();

    public List<System> parse(MainDocumentPart mainDocumentPart) {
        Tbl table = TableFinder.findByBookMark(mainDocumentPart, "system_table");

        boolean isHeader = true;
        List<System> systems = new ArrayList<>();
        for (Object rowObj : table.getContent()) {
            // 跳过表头
            if (isHeader) {
                isHeader = false;
                continue;
            }
            Tr tr = (Tr) XmlUtils.unwrap(rowObj);
            List<Object> cells = tr.getContent();

            String name = textExtractor.extractText(cells.get(0));
            String version = textExtractor.extractText(cells.get(1));
            String language = textExtractor.extractText(cells.get(2));
            String devEnv = textExtractor.extractText(cells.get(3));
            String runEnv = textExtractor.extractText(cells.get(4));

            System system = new System();
            system.setName(name);
            system.setVersion(version);
            system.setLanguage(language);
            system.setDevEnv(devEnv);
            system.setRunEnv(runEnv);
            systems.add(system);
        }
        return systems;
    }
}
