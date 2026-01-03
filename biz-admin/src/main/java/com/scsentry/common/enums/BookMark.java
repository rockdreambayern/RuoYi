package com.scsentry.common.enums;

import lombok.Getter;

@Getter
public enum BookMark {

    CONTENT("content"),
    REQUIREMENTS("requirements"),
    TOPO("topo"),
    ENVIRONMENT("environment"),
    SOFTWARE_TO_TEST("software_to_test"),
    TOOL("tool"),
    TEST_CONTENTS("test_contents"),
    TEST_CASE_STAT("test_case_stat"),
    BUG_TABLE_BY_TYPE("bug_table_by_type"),
    BUG_TABLE_BY_LEVEL("bug_table_by_level"),
    SUMMARY("summary"),
    PROJECT_DESCRIPTION("projectDescription"),
    STUFF_CONFIG("stuff_config");

    private String code;

    BookMark(String code) {
        this.code = code;
    }
}
