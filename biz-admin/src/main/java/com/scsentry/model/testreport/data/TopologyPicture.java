package com.scsentry.model.testreport.data;

import com.scsentry.common.enums.BookMark;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import com.scsentry.common.util.ContentAdder;
import org.docx4j.wml.*;

public class TopologyPicture {

    public void generatePicture(WordprocessingMLPackage wordMLPackage, MainDocumentPart mainDocumentPart, BinaryPartAbstractImage image) throws Exception {
        ObjectFactory factory = Context.getWmlObjectFactory();


        BinaryPartAbstractImage imagePart =
                BinaryPartAbstractImage.createImagePart(
                        wordMLPackage,
                        image.getBytes()
                );

        // 1️⃣ 由 image 创建 Inline（关键）
        Inline inline = imagePart.createImageInline(
                "image",
                "image",
                1,
                2,
                false
        );

        // 2️⃣ 创建 Drawing
        Drawing drawing = factory.createDrawing();
        drawing.getAnchorOrInline().add(inline);

        // 3️⃣ Run
        R r = factory.createR();
        r.getContent().add(drawing);

        // 4️⃣ Paragraph
        P p = factory.createP();
        PPr pPr = factory.createPPr();
        Jc jc = factory.createJc();
        jc.setVal(JcEnumeration.CENTER);
        pPr.setJc(jc);
        p.setPPr(pPr);

        p.getContent().add(r);


        ContentAdder.addByBookMark(mainDocumentPart, p, BookMark.TOPO);
    }
}
