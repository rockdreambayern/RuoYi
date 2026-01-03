package com.scsentry.model.testreport.data;

import lombok.Data;

@Data
public class Tool {

    private String type;

    private String name;

    private String version;

    private String usage;

    private String comment = "——";
}
