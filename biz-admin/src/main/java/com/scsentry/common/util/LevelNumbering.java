package com.scsentry.common.util;

import com.ruoyi.common.exception.base.BaseException;
import org.docx4j.XmlUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;
import org.docx4j.wml.*;

import java.math.BigInteger;

public class LevelNumbering {
    private LevelNumbering() {

    }

    public static String addMultilevelNumbering(WordprocessingMLPackage wordMLPackage) {
        try {
            NumberingDefinitionsPart ndp = new NumberingDefinitionsPart();
            wordMLPackage.getMainDocumentPart().addTargetPart(ndp);

            String numberingXml =
                    "<w:numbering xmlns:w='http://schemas.openxmlformats.org/wordprocessingml/2006/main'>"
                            + "  <w:abstractNum w:abstractNumId='1'>"
                            + "    <w:multiLevelType w:val='multilevel'/>"
                            + "    <w:lvl w:ilvl='0'>"
                            + "      <w:numFmt w:val='decimal'/>"
                            + "      <w:lvlText w:val='%1'/>"
                            + "      <w:start w:val='1'/>"
                            + "    </w:lvl>"
                            + "    <w:lvl w:ilvl='1'>"
                            + "      <w:numFmt w:val='decimal'/>"
                            + "      <w:lvlText w:val='%1.%2'/>"
                            + "      <w:start w:val='1'/>"
                            + "    </w:lvl>"
                            + "    <w:lvl w:ilvl='2'>"
                            + "      <w:numFmt w:val='decimal'/>"
                            + "      <w:lvlText w:val='%1.%2.%3'/>"
                            + "      <w:start w:val='1'/>"
                            + "    </w:lvl>"
                            + "  </w:abstractNum>"
                            + "  <w:num w:numId='1'>"
                            + "    <w:abstractNumId w:val='1'/>"
                            + "  </w:num>"
                            + "</w:numbering>";

            ndp.setJaxbElement(
                    (Numbering) XmlUtils.unmarshallFromTemplate(numberingXml, null)
            );
        } catch (Exception e) {
            throw new BaseException("系统内部错误");
        }

        return "1";  // 返回 numId=1
    }

    public static P createHeadingWithNumber(int level, String text, String numId) {
        ObjectFactory factory = Context.getWmlObjectFactory();

        P p = factory.createP();
        PPr ppr = factory.createPPr();

        // 设置样式 HeadingX
        PPrBase.PStyle style = factory.createPPrBasePStyle();
        style.setVal("Heading" + (level + 1));
        ppr.setPStyle(style);

        // ★ 关键：设置大纲级别
        PPrBase.OutlineLvl outlineLvl = factory.createPPrBaseOutlineLvl();
        outlineLvl.setVal(BigInteger.valueOf(level)); // level=0 → Heading1
        ppr.setOutlineLvl(outlineLvl);

        // 多级编号设置
        PPrBase.NumPr numPr = factory.createPPrBaseNumPr();
        PPrBase.NumPr.NumId numIdElm = factory.createPPrBaseNumPrNumId();
        numIdElm.setVal(new BigInteger(numId));
        numPr.setNumId(numIdElm);

        PPrBase.NumPr.Ilvl ilvl = factory.createPPrBaseNumPrIlvl();
        ilvl.setVal(BigInteger.valueOf(level));
        numPr.setIlvl(ilvl);
        ppr.setNumPr(numPr);

        // ====== 设置小四字号，作用于编号 + 内容 ======
        // plvl - 段落级生效 → 控制编号
        ParaRPr pRpr = factory.createParaRPr();
        HpsMeasure sz = factory.createHpsMeasure();
        sz.setVal(BigInteger.valueOf(24)); // 小四=12pt → 24
        pRpr.setSz(sz);
        pRpr.setSzCs(sz);
        ppr.setRPr(pRpr);

        p.setPPr(ppr);

        // ====== 文本本体 ======
        R r = factory.createR();

        // 文字字号，设置一次即可与编号一致
        RPr rpr = factory.createRPr();
        rpr.setSz(sz);
        rpr.setSzCs(sz);
        r.setRPr(rpr);

        Text t = factory.createText();
        t.setValue(text);
        t.setSpace("preserve");
        r.getContent().add(t);
        p.getContent().add(r);

        return p;
    }
}
