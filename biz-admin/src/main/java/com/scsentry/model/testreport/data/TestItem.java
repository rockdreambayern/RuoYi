package com.scsentry.model.testreport.data;

import com.scsentry.common.enums.Result;
import com.scsentry.common.enums.SeverityLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestItem {

    private String item;

    private String content;

    private Result result;

    private SeverityLevel severityLevel;

    private String comment = "——";
}
