package com.scsentry.model.testreport.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Requirement {

    private String item;

    private String content;
}