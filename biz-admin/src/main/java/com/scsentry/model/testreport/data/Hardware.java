package com.scsentry.model.testreport.data;

import lombok.Data;

import java.util.List;

@Data
public class Hardware {

    private String name;

    private String configuration;

    private int count;

    private String usage;

    private String comment = "——";

    private List<Software> softwares;
}
