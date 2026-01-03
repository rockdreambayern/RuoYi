package com.scsentry.common.util.table;

import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tr;
import com.scsentry.model.testreport.data.Tool;

import java.util.Arrays;
import java.util.List;

public class ToolTableGenerator extends CommonTableGenerator {

    private static final List<String> HEADERS = Arrays.asList("序号", "工具类型", "工具名称", "版本", "用途", "备注");

    public static Tbl createTable(List<Tool> tools) {
        Tbl table = createBasicTable();

        addHeaders(table, HEADERS);

        addRowData(table, tools);

        return table;
    }

    private static void addRowData(Tbl table, List<Tool> tools) {
        int index = 1;
        for (Tool tool : tools) {
            Tr tr = factory.createTr();
            tr.getContent().add(createCell(String.valueOf(index)));
            tr.getContent().add(createCell(tool.getType()));
            tr.getContent().add(createCell(tool.getName()));
            tr.getContent().add(createCell(tool.getVersion()));
            tr.getContent().add(createCell(tool.getUsage()));
            tr.getContent().add(createCell(tool.getComment()));
            table.getContent().add(tr);
            index++;
        }
    }
}
