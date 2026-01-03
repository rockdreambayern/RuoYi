package com.scsentry.model.testreport.data;

import lombok.Data;

@Data
public class System {

    private String name;

    private String version;

    private String language;

    private String devEnv;

    private String runEnv;
}
