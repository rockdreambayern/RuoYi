package com.scsentry.common.util.table;

import com.scsentry.model.testreport.data.Hardware;
import com.scsentry.model.testreport.data.Software;
import org.apache.commons.collections4.CollectionUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.wml.*;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public class EnvironmentTableGenerator extends CommonTableGenerator {

    private EnvironmentTableGenerator() {

    }

    private static final ObjectFactory factory = Context.getWmlObjectFactory();

    private static final List<String> HEADERS = Arrays.asList("序号", "硬件项名称", "配置", "数量", "软件名称", "版本", "备注");

    public static Tbl createTable(List<Hardware> hardWares) {
        Tbl table = createBasicTable();

        addHeaders(table, HEADERS);

        addRowData(table, hardWares);

        return table;
    }


    private static void addRowData(Tbl table, List<Hardware> hardWares) {
        int index = 1;
        for (Hardware hardware : hardWares) {
            List<Software> softWares = hardware.getSoftwares();
            if (CollectionUtils.isEmpty(softWares)) { // 推荐使用 Apache Commons 工具类，或自行实现空判断
                addHardwareSingleRow(table, hardware, index);
                index++;
                continue;
            }
            addHardwareWithSoftwareRows(table, hardware, softWares, index);
            index++;
        }
    }

    /**
     * 添加无软件的硬件单行数据（无合并单元格）
     */
    private static void addHardwareSingleRow(Tbl table, Hardware hardware, int index) {
        Tr tr = factory.createTr();
        tr.getContent().add(createCell(String.valueOf(index)));
        tr.getContent().add(createCell(hardware.getName()));
        tr.getContent().add(createCellLeftJustifying(hardware.getConfiguration()));
        tr.getContent().add(createCell(String.valueOf(hardware.getCount())));
        tr.getContent().add(createCell("——")); // 软件名称占位
        tr.getContent().add(createCell("——")); // 版本占位
        tr.getContent().add(createCell(getSafeComment(hardware.getComment())));
        table.getContent().add(tr);
    }

    /**
     * 添加带软件的硬件多行数据（含纵向合并单元格）
     */
    private static void addHardwareWithSoftwareRows(Tbl table, Hardware hardware, List<Software> softwares, int index) {
        boolean isFirstRow = true;
        String comment = getSafeComment(hardware.getComment());

        for (Software software : softwares) {
            Tr tr = factory.createTr();

            // 硬件相关字段（序号、名称、配置）- 纵向合并
            addMergedHardwareCells(tr, hardware, index, isFirstRow);

            // 软件相关字段（名称、版本）- 不合并
            tr.getContent().add(createCell(software.getName()));
            tr.getContent().add(createCell(software.getVersion()));

            // 备注字段 - 纵向合并
            addMergedCommentCell(tr, comment, isFirstRow);

            table.getContent().add(tr);
            isFirstRow = false;
        }
    }

    /**
     * 添加硬件相关的合并单元格（序号、名称、配置）
     */
    private static void addMergedHardwareCells(Tr tr, Hardware hardware, int index, boolean isFirstRow) {
        if (isFirstRow) {
            tr.getContent().add(createMergedCellStart(String.valueOf(index)));
            tr.getContent().add(createMergedCellStart(hardware.getName()));
            tr.getContent().add(buildCell(hardware.getConfiguration(), false, JcEnumeration.LEFT, null, "restart"));
            tr.getContent().add(createMergedCellStart(String.valueOf(hardware.getCount())));
        } else {
            tr.getContent().add(createMergedCellContinue());
            tr.getContent().add(createMergedCellContinue());
            tr.getContent().add(buildCell(hardware.getConfiguration(), false, JcEnumeration.LEFT, null, "continue"));
            tr.getContent().add(createMergedCellContinue());
        }
    }

    /**
     * 添加备注字段的合并单元格
     */
    private static void addMergedCommentCell(Tr tr, String comment, boolean isFirstRow) {
        if (isFirstRow) {
            tr.getContent().add(createMergedCellStart(comment));
        } else {
            tr.getContent().add(createMergedCellContinue());
        }
    }

    private static Tc createMergedCellStart(String text) {
        Tc tc = createCell(text);

        TcPr tcPr = tc.getTcPr();
        if (tcPr == null) tcPr = factory.createTcPr();

        TcPrInner.VMerge vMerge = new TcPrInner.VMerge();
        vMerge.setVal("restart");
        tcPr.setVMerge(vMerge);

        tc.setTcPr(tcPr);
        return tc;
    }

    private static Tc createMergedCellContinue() {
        Tc tc = createCell("");

        TcPr tcPr = tc.getTcPr();
        if (tcPr == null) tcPr = factory.createTcPr();

        TcPrInner.VMerge vMerge = new TcPrInner.VMerge();
        // val 不设置，表示 continue
        tcPr.setVMerge(vMerge);

        tc.setTcPr(tcPr);
        return tc;
    }

    public static Tc buildCell(String text,
                               boolean bold,
                               JcEnumeration align,
                               String bgColor,
                               String vMerge) {

        // ===== 创建单元格 =====
        Tc cell = factory.createTc();
        TcPr tcPr = factory.createTcPr();

        // 单元格垂直居中
        CTVerticalJc vJc = new CTVerticalJc();
        vJc.setVal(STVerticalJc.CENTER);
        tcPr.setVAlign(vJc);

        // 设置背景色（可选）
        if (bgColor != null) {
            CTShd shd = factory.createCTShd();
            shd.setFill(bgColor);
            tcPr.setShd(shd);
        }

        // 纵向合并控制：restart/continue
        if (vMerge != null) {
            TcPrInner.VMerge merge = new TcPrInner.VMerge();
            merge.setVal(vMerge);
            tcPr.setVMerge(merge);
        }

        cell.setTcPr(tcPr);

        // ===== 段落 设置对齐方式 =====
        P p = factory.createP();
        PPr pPr = factory.createPPr();
        Jc jc = factory.createJc();
        jc.setVal(align == null ? JcEnumeration.LEFT : align);
        pPr.setJc(jc);
        p.setPPr(pPr);

        // ===== 文字 run 设置 =====
        R r = factory.createR();
        RPr rPr = factory.createRPr();

        if (bold) {
            rPr.setB(new BooleanDefaultTrue());
        }

        rPr.setRFonts(FONTS_TIMES_NEW_ROMAN);

        // 字号（10.5pt = 21）
        HpsMeasure size = new HpsMeasure();
        size.setVal(BigInteger.valueOf(21));
        rPr.setSz(size);
        rPr.setSzCs(size);

        r.setRPr(rPr);

        Text t = factory.createText();
        t.setValue(text);
        r.getContent().add(t);

        p.getContent().add(r);
        cell.getContent().add(p);

        return cell;
    }
}
