package com.scsentry.common.util.table;

import com.scsentry.model.testreport.data.Requirement;
import org.docx4j.jaxb.Context;
import org.docx4j.wml.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequirementTableGenerator extends CommonTableGenerator {

    private RequirementTableGenerator() {
    }

    private static final ObjectFactory factory = Context.getWmlObjectFactory();

    private static final List<String> HEADERS = Arrays.asList("序号", "需求项", "需求内容", "是否测试", "备注");

    public static Tbl createTable(List<Requirement> requirements) {
        Tbl table = createBasicTable();

        Map<String, List<Integer>> map = new HashMap<>();
        map.put("***需求规格说明***", Arrays.asList(0, 1, 2, 3, 4));
        addHeaders(table, HEADERS, map);

        addRowData(table, requirements);

        return table;
    }

    private static void addRowData(Tbl table, List<Requirement> requirements) {
        int index = 1;
        for (Requirement requirement : requirements) {
            Tr row = factory.createTr();
            Tc cell = createCell(String.valueOf(index));
            row.getContent().add(cell);
            cell = createCell(requirement.getItem());
            row.getContent().add(cell);
            cell = createCell(requirement.getContent(), JcEnumeration.LEFT);
            row.getContent().add(cell);
            cell = createCell("是");
            row.getContent().add(cell);
            cell = createCell("——");
            row.getContent().add(cell);
            table.getContent().add(row);
            index++;
        }
    }

    public static P createCaptionP(String captionNumber, String captionDesc) {
        P captionP = factory.createP();

        PPr captionPPr = factory.createPPr();
        Jc captionJc = new Jc();
        captionJc.setVal(JcEnumeration.CENTER);
        captionPPr.setJc(captionJc);
        captionP.setPPr(captionPPr);

        R captionR = factory.createR();
        RPr captionRPr = factory.createRPr();
        captionR.setRPr(captionRPr);

        Text captionText = factory.createText();
        captionText.setValue(captionNumber + "  " + captionDesc);
        captionR.getContent().add(captionText);
        captionP.getContent().add(captionR);

        return captionP;
    }

    public static P createTableDesc(String captionNumber, String captionDesc) {
        P captionP = factory.createP();

        PPr captionPPr = factory.createPPr();
        Jc captionJc = new Jc();
        captionJc.setVal(JcEnumeration.LEFT);
        captionPPr.setJc(captionJc);
        captionP.setPPr(captionPPr);

        R captionR = factory.createR();
        RPr captionRPr = factory.createRPr();
        captionR.setRPr(captionRPr);

        Text captionText = factory.createText();
        captionText.setValue(captionDesc + "参见" + captionNumber);
        captionR.getContent().add(captionText);
        captionP.getContent().add(captionR);

        return captionP;
    }
}
