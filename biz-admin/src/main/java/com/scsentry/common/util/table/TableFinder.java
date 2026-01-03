package com.scsentry.common.util.table;

import com.ruoyi.common.exception.base.BaseException;
import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.CTBookmark;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.P;
import org.docx4j.wml.Tbl;

import java.util.Collections;
import java.util.List;

public class TableFinder {

    private TableFinder() {

    }

    public static Tbl findByBookMark(MainDocumentPart mainDocumentPart, String bookMark) {
        List<Object> bookmarks = Collections.emptyList();
        try {
            // 1. 找到书签
            bookmarks = mainDocumentPart.getJAXBNodesViaXPath(
                    String.format("//w:bookmarkStart[@w:name='%s']", bookMark), false);
        } catch (Exception e) {
            throw new BaseException("解析书签失败");
        }

        if (bookmarks.isEmpty()) {
            throw new BaseException("未找到书签" + bookMark);
        }

        CTBookmark bookmark = (CTBookmark) XmlUtils.unwrap(bookmarks.get(0));

        // 2. 获取书签所在段落
        P bookmarkP = (P) bookmark.getParent();

        // 3. 获取段落所在的父容器（通常是 Body）
        ContentAccessor parent = (ContentAccessor) bookmarkP.getParent();
        List<Object> contents = parent.getContent();

        // 4. 找到段落后面的第一个 Tbl
        int index = contents.indexOf(bookmarkP);

        for (int i = index + 1; i < contents.size(); i++) {
            Object o = XmlUtils.unwrap(contents.get(i));
            if (o instanceof Tbl) {
                return (Tbl)o;
            }
        }
        return null;
    }
}
