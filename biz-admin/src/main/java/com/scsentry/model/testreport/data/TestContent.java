package com.scsentry.model.testreport.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestContent {

    private String requirement;

    private String content;

    private List<TestItem> testItems;
}
