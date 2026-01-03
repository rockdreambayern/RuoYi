package com.scsentry.service.testreport;

import com.scsentry.common.util.table.TableFinder;
import com.scsentry.model.testreport.data.Environment;
import com.scsentry.model.testreport.data.Hardware;
import com.scsentry.model.testreport.data.Software;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;

import java.util.ArrayList;
import java.util.List;

public class EnvironmentParser {

    private static final TextExtractor textExtractor = new TextExtractor();

    public Environment parse(MainDocumentPart mainDocumentPart) {
        Tbl table = TableFinder.findByBookMark(mainDocumentPart, "environment_table");

        List<Hardware> hardwares = new ArrayList<>();

        boolean isHeader = true;

        Hardware currentHardWare = null;
        String lastConfiguration = null;
        String lastHardWareName = null;
        String lastHardWareCount = null;
        String lastHarderWareUsage = null;

        for (Object rowObj : table.getContent()) {

            // 跳过表头
            if (isHeader) {
                isHeader = false;
                continue;
            }

            Tr tr = (Tr) XmlUtils.unwrap(rowObj);
            List<Object> cells = tr.getContent();

            // ===== 取每一列的文本 =====
            String hardWareName = getCellText(cells, 0);
            String configuration = getCellText(cells, 1);
            String hardWareCount = getCellText(cells, 2);
            String softWareName = getCellText(cells, 3);
            String softWareVersion = getCellText(cells, 4);
            String harderWareUsage = getCellText(cells, 5);
            String comment = getCellText(cells, 6);

            // ===== 处理“需求项 / 需求内容”合并 =====
            boolean newHardWare = false;

            if (StringUtils.isNoneBlank(hardWareName)) {
                lastHardWareName = hardWareName;
                newHardWare = true;
            }

            if (StringUtils.isNoneBlank(configuration)) {
                lastConfiguration = configuration;
            }

            if (StringUtils.isNoneBlank(hardWareCount)) {
                lastHardWareCount = hardWareCount;
            }

            if (StringUtils.isNoneBlank(harderWareUsage)) {
                lastHarderWareUsage = harderWareUsage;
            }

            // ===== 新建 TestContent =====
            if (newHardWare || currentHardWare == null) {
                currentHardWare = new Hardware();
                currentHardWare.setName(lastHardWareName);
                currentHardWare.setConfiguration(lastConfiguration);
                currentHardWare.setCount(Integer.parseInt(lastHardWareCount));
                currentHardWare.setUsage(lastHarderWareUsage);
                currentHardWare.setComment(comment);
                currentHardWare.setSoftwares(new ArrayList<>());

                hardwares.add(currentHardWare);
            }

            // ===== 构建 TestItem =====
            Software software = new Software();
            software.setName(softWareName);
            software.setVersion(softWareVersion);

            currentHardWare.getSoftwares().add(software);
        }

        return new Environment(hardwares, null, null);
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
