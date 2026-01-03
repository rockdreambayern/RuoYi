package com.scsentry.common.util;

import com.ruoyi.common.exception.base.BaseException;
import com.scsentry.common.enums.BookMark;
import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.CTBookmark;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.P;

import java.util.Collections;
import java.util.List;

public class ContentAdder {

    private ContentAdder() {

    }

    public static void addByBookMark(MainDocumentPart mdp, Object content, BookMark bookMark) {
        List<Object> bookmarks = Collections.emptyList();
        try {
            bookmarks = mdp.getJAXBNodesViaXPath(String.format("//w:bookmarkStart[@w:name='%s']", bookMark.getCode()), false);
        } catch (Exception e) {
            throw new BaseException("模板中标签" + bookMark.getCode() + "不存在");
        }
        if (!bookmarks.isEmpty()) {
            Object obj = bookmarks.get(0);
            CTBookmark bookmark = (CTBookmark) XmlUtils.unwrap(obj);
            P paragraph = (P) bookmark.getParent();
            ContentAccessor parent = (ContentAccessor) paragraph.getParent();
            List<Object> contentList = parent.getContent();
            int index = contentList.indexOf(paragraph);
            contentList.add(index, content);
        } else {
            throw new BaseException("模板中标签" + bookMark.getCode() + "不存在");
        }
    }
}
