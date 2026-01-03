package com.scsentry.common.util.table;

import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;

import java.util.Arrays;
import java.util.List;
import com.scsentry.model.testreport.data.System;

public class SystemTableGenerator extends CommonTableGenerator {

    private static final List<String> HEADERS = Arrays.asList("序号", "被测软件名称", "版本", "语言", "开发环境", "运行环境");

    public static Tbl createTable(List<System> systems) {
        Tbl table = createBasicTable();

        addHeaders(table, HEADERS);

        addRowData(table, systems);

        return table;
    }

    private static void addRowData(Tbl table, List<System> systems) {
        int index = 1;
        for (System system : systems) {
            Tr tr = factory.createTr();
            tr.getContent().add(createCell(String.valueOf(index)));
            tr.getContent().add(createCell(system.getName()));
            tr.getContent().add(createCell(system.getVersion()));
            tr.getContent().add(createCell(system.getLanguage()));
            tr.getContent().add(createCell(system.getDevEnv()));
            tr.getContent().add(createCell(system.getRunEnv()));
            table.getContent().add(tr);
            index++;
        }
    }
}
