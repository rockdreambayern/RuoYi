package com.scsentry.service.testreport;

import org.docx4j.TraversalUtil;
import org.docx4j.wml.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TextExtractor {

    private static final TextTraversalCallback callback = new TextTraversalCallback();

    public String extractText(Object root) {
        callback.clear();
        new TraversalUtil(root, callback);

        return callback.getText();
    }

    public List<String> extractTexts(Object root) {
        callback.clear();
        new TraversalUtil(root, callback);

        return callback.getTexts();
    }

    static class TextTraversalCallback extends TraversalUtil.CallbackImpl {

        private final List<String> texts = new ArrayList<>();

        @Override
        public List<Object> apply(Object o) {
            if (o instanceof Text) {
                Text t = (Text)o;
                if (t.getValue() != null) {
                    texts.add(t.getValue());
                }
            }

            // 返回 null 表示继续遍历
            return Collections.emptyList();
        }

        public void clear() {
            texts.clear();
        }

        public String getText() {
            return String.join("", texts).trim();
        }

        public List<String> getTexts() {
            return texts;
        }
    }
}
