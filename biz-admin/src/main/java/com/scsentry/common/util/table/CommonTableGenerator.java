package com.scsentry.common.util.table;

import com.scsentry.service.testreport.TextExtractor;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.wml.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class CommonTableGenerator {

    protected static final ObjectFactory factory = Context.getWmlObjectFactory();

    private static final String FONT_TIMES_NEW_ROMAN = "Times New Roman";

    private static final TextExtractor textExtractor = new TextExtractor();

    protected static final RFonts FONTS_TIMES_NEW_ROMAN;
    static
    {
        FONTS_TIMES_NEW_ROMAN = factory.createRFonts();
        FONTS_TIMES_NEW_ROMAN.setAscii(FONT_TIMES_NEW_ROMAN);
        FONTS_TIMES_NEW_ROMAN.setHAnsi(FONT_TIMES_NEW_ROMAN);
        FONTS_TIMES_NEW_ROMAN.setEastAsia(FONT_TIMES_NEW_ROMAN);
    }

    public static Tbl createBasicTable() {
        Tbl table = factory.createTbl();

        // ===== 1. 表格属性 =====
        TblPr tblPr = factory.createTblPr();
        TblWidth tblWidth = factory.createTblWidth();
        tblWidth.setType("pct");
        tblWidth.setW(BigInteger.valueOf(5000));
        tblPr.setTblW(tblWidth);

        CTTblLayoutType layout = factory.createCTTblLayoutType();
        layout.setType(STTblLayoutType.AUTOFIT);
        tblPr.setTblLayout(layout);

        // 设置边框
        TblBorders borders = factory.createTblBorders();
        CTBorder border = factory.createCTBorder();
        border.setVal(STBorder.SINGLE);
        border.setSz(BigInteger.valueOf(4));
        border.setColor("000000");
        borders.setTop(border);
        borders.setBottom(border);
        borders.setLeft(border);
        borders.setRight(border);
        borders.setInsideH(border);
        borders.setInsideV(border);
        tblPr.setTblBorders(borders);

        table.setTblPr(tblPr);

        return table;
    }

    /**
     * 在现有表头上增加分组表头行
     * @param table 原表格
     * @param headers 原表头内容（与列顺序一致）
     * @param content2IndexList key 为分组内容，value 为所属列 indexes (已保证连续)
     */
    public static void addHeaders(Tbl table, final List<String> headers,
                                  Map<String, List<Integer>> content2IndexList) {

        // ====== 1. 若无表头，直接返回 =====
        if (headers == null || headers.isEmpty()) { return; }

        // ===== 2. 先添加原来的一行表头 =====
        addHeaders(table, headers);

        Tr originalHeadersRow  = ((Tr)table.getContent().get(0));

        // ===== 3. 新增分组行 =====
        Tr groupRow = factory.createTr();

        // 分组行需创建与 headers 等宽 TableGrid（如果之前没设置则补）
        if (table.getTblGrid() == null) {
            TblGrid tblGrid = factory.createTblGrid();
            for (int i = 0; i < headers.size(); i++) {
                TblGridCol col = factory.createTblGridCol();
                col.setW(BigInteger.valueOf(800));
                tblGrid.getGridCol().add(col);
            }
            table.setTblGrid(tblGrid);
        }

        // ===== 4. 构造分组表头，根据 Map 进行合并 =====
        int currentIndex = 0;
        while (currentIndex < headers.size()) {
            boolean matched = false;

            for (Map.Entry<String, List<Integer>> entry : content2IndexList.entrySet()) {
                List<Integer> indexList = entry.getValue();
                if (indexList.get(0) == currentIndex) { // 找到需要从这列开始合并的分组
                    matched = true;
                    int start = indexList.get(0);
                    int end = indexList.get(indexList.size() - 1);
                    int span = end - start + 1;

                    // 单元格
                    Tc tc = factory.createTc();
                    TcPr tcPr = factory.createTcPr();

                    TcPrInner.VMerge vMerge = new TcPrInner.VMerge();
                    vMerge.setVal("restart");   // 合并起始
                    tcPr.setVMerge(vMerge);
                    tc.setTcPr(tcPr);

                    // 单元格合并设置 gridSpan
                    TcPrInner.GridSpan gridSpan = new TcPrInner.GridSpan();
                    gridSpan.setVal(BigInteger.valueOf(span));
                    tcPr.setGridSpan(gridSpan);

                    // 居中样式
                    P p = factory.createP();
                    PPr pPr = factory.createPPr();
                    Jc jc = factory.createJc();
                    jc.setVal(JcEnumeration.CENTER);
                    pPr.setJc(jc);
                    p.setPPr(pPr);

                    R r = factory.createR();
                    RPr rPr = factory.createRPr();
                    BooleanDefaultTrue bold = new BooleanDefaultTrue();
                    rPr.setB(bold);
                    r.setRPr(rPr);

                    Text text = factory.createText();
                    text.setValue(entry.getKey()); // 分组名称
                    r.getContent().add(text);
                    p.getContent().add(r);

                    tc.setTcPr(tcPr);
                    tc.getContent().add(p);

                    // 背景色
                    CTShd shd = factory.createCTShd();
                    shd.setFill("DCE5F1");
                    tcPr.setShd(shd);
                    tc.setTcPr(tcPr);

                    groupRow.getContent().add(tc);

                    currentIndex = end + 1; // 跳过合并过的列
                    break;
                }
            }

            // 若本列不在任何 group 中 → 创建独立单元格
            if (!matched) {
                Tc headerCell = ((Tc)originalHeadersRow.getContent().remove(currentIndex));
                Tc header = buildHeaderCell(textExtractor.extractText(headerCell));
                Tc tc = buildMergeFollowerCell(headerCell);
                groupRow.getContent().add(header);
                originalHeadersRow.getContent().add(currentIndex, tc);
                currentIndex++;
            }
        }

        // 插入到原表头前（新表头在上层）
        table.getContent().add(0, groupRow);
    }


    public static void addHeaders(Tbl table, final List<String> headers) {
        // ===== 2. 必须：设置 Table Grid，否则表格可能不显示 =====
        TblGrid tblGrid = factory.createTblGrid();
        for (int i = 0; i < headers.size(); i++) {
            TblGridCol gridCol = factory.createTblGridCol();
            gridCol.setW(BigInteger.valueOf(800)); // 每列宽度随便设
            tblGrid.getGridCol().add(gridCol);
        }
        table.setTblGrid(tblGrid);

        // ===== 3. 表头行 =====
        Tr headerRow = factory.createTr();

        for (String header : headers) {
            Tc cell = buildHeaderCell(header);
            headerRow.getContent().add(cell);
        }

        table.getContent().add(headerRow);
    }

    public static Tc createCell(String text, JcEnumeration jcEnumeration) {
        Tc cell = factory.createTc();

        // ===== 单元格属性：垂直居中 =====
        TcPr tcPr = factory.createTcPr();
        CTVerticalJc vJc = new CTVerticalJc();
        vJc.setVal(STVerticalJc.CENTER);
        tcPr.setVAlign(vJc);
        cell.setTcPr(tcPr);

        // ===== 段落 =====
        P p = factory.createP();

        // 段落属性：水平居中
        PPr pPr = factory.createPPr();
        Jc jc = factory.createJc();
        jc.setVal(jcEnumeration);
        pPr.setJc(jc);
        p.setPPr(pPr);

        PPrBase.Spacing spacing = factory.createPPrBaseSpacing();
        spacing.setLineRule(STLineSpacingRule.AUTO);  // 或 EXACT 如需固定行高
        spacing.setLine(BigInteger.valueOf(240));     // 240 = 1倍行距（word标准值）
        pPr.setSpacing(spacing);

        // ===== Run =====
        R r = factory.createR();

        // 字体 & 字号
        RPr rPr = factory.createRPr();
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

    public static Tc createCell(String text) {
        return createCell(text, JcEnumeration.CENTER);
    }

    public static Tc createCellLeftJustifying(String text) {
        Tc cell = factory.createTc();

        // ===== 单元格属性：垂直居中 =====
        TcPr tcPr = factory.createTcPr();
        CTVerticalJc vJc = new CTVerticalJc();
        vJc.setVal(STVerticalJc.CENTER);
        tcPr.setVAlign(vJc);
        cell.setTcPr(tcPr);

        // ===== 段落 =====
        P p = factory.createP();

        // 段落属性：水平居中
        PPr pPr = factory.createPPr();
        Jc jc = factory.createJc();
        jc.setVal(JcEnumeration.LEFT);
        pPr.setJc(jc);
        p.setPPr(pPr);

        // ===== Run =====
        R r = factory.createR();

        // 字体 & 字号
        RPr rPr = factory.createRPr();

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

    public static Tc buildHeaderCell(String header) {
        Tc cell = factory.createTc();
        TcPr tcPr = factory.createTcPr();

        TcPrInner.VMerge vMerge = new TcPrInner.VMerge();
        vMerge.setVal("restart");   // 合并起始
        tcPr.setVMerge(vMerge);
        cell.setTcPr(tcPr);

        // ===== 单元格属性：垂直居中 =====
        CTVerticalJc vJc = new CTVerticalJc();
        vJc.setVal(STVerticalJc.CENTER);
        tcPr.setVAlign(vJc);
        cell.setTcPr(tcPr);

        CTShd shd = factory.createCTShd();
        shd.setFill("DCE5F1");
        tcPr.setShd(shd);
        cell.setTcPr(tcPr);

        // 文本
        P p = factory.createP();
        PPr pPr = factory.createPPr();
        Jc jc = factory.createJc();
        jc.setVal(JcEnumeration.CENTER);
        pPr.setJc(jc);
        p.setPPr(pPr);

        R r = factory.createR();

        RPr rPr = factory.createRPr();
        BooleanDefaultTrue bold = new BooleanDefaultTrue();
        rPr.setB(bold);
        r.setRPr(rPr);

        Text t = factory.createText();
        t.setValue(header);
        r.getContent().add(t);

        p.getContent().add(r);
        cell.getContent().add(p);

        return cell;
    }

    /**
     * 安全处理备注信息（空值转为默认文本）
     */
    public static String getSafeComment(String comment) {
        return StringUtils.isBlank(comment) ? "——" : comment; // 推荐使用 Apache Commons Lang，或自行实现
    }


    // 构建作为合并起点的单元格（显示 systemName）
    public static Tc buildMergedCell(String val) {
        Tc cell = factory.createTc();
        TcPr tcPr = factory.createTcPr();

        TcPrInner.VMerge vMerge = new TcPrInner.VMerge();
        vMerge.setVal("restart");   // 合并起始
        tcPr.setVMerge(vMerge);
        cell.setTcPr(tcPr);

        // ===== 单元格属性：垂直居中 =====
        CTVerticalJc vJc = new CTVerticalJc();
        vJc.setVal(STVerticalJc.CENTER);
        tcPr.setVAlign(vJc);
        cell.setTcPr(tcPr);

        Text text = factory.createText();
        text.setValue(val);

        P p = factory.createP();
        PPr pPr = factory.createPPr();
        Jc jc = factory.createJc();
        jc.setVal(JcEnumeration.CENTER);
        pPr.setJc(jc);
        p.setPPr(pPr);
        R run = factory.createR();
        run.getContent().add(text);
        p.getContent().add(run);
        cell.getContent().add(p);

        return cell;
    }

    // 被合并的后续单元格（不显示值）
    public static Tc buildMergeFollowerCell() {
        Tc cell = factory.createTc();
        TcPr tcPr = new TcPr();

        TcPrInner.VMerge vMerge = new TcPrInner.VMerge();
        // 不设置 val → 表示继承上一行进行合并
        tcPr.setVMerge(vMerge);
        cell.setTcPr(tcPr);

        setAlignCenter(cell);
        return cell;
    }

    public static Tc buildMergeFollowerCell(Tc mergedStartCell) {
        Tc cell = factory.createTc();
        TcPr tcPr = new TcPr();

        // 继续合并
        TcPrInner.VMerge vMerge = new TcPrInner.VMerge();
        vMerge.setVal("continue");
        tcPr.setVMerge(vMerge);

        // ========== ✨ 背景继承关键逻辑 ==========
        TcPr startPr = mergedStartCell.getTcPr();
        if (startPr != null && startPr.getShd() != null) {
            CTShd shdCopy = new CTShd();
            shdCopy.setFill(startPr.getShd().getFill());      // 继承fill颜色
            shdCopy.setColor(startPr.getShd().getColor());
            shdCopy.setVal(startPr.getShd().getVal());
            tcPr.setShd(shdCopy);
        }

        cell.setTcPr(tcPr);

        // 居中处理保持一致
        setAlignCenter(cell);
        return cell;
    }

    private static void setAlignCenter(Tc cell) {
        TcPr tcPr = cell.getTcPr();

        // 与主单元格保持同样居中属性（垂直、水平均生效用于合并后的整体表现）
        CTVerticalJc vAlign = new CTVerticalJc();
        vAlign.setVal(STVerticalJc.CENTER);
        tcPr.setVAlign(vAlign);
        cell.setTcPr(tcPr);

        // 水平居中空段落 (必需，否则有时 Word 渲染不会继承)
        P p = factory.createP();
        PPr pPr = factory.createPPr();
        Jc jc = new Jc();
        jc.setVal(JcEnumeration.CENTER);
        pPr.setJc(jc);
        p.setPPr(pPr);
        cell.getContent().add(p);
    }
}
