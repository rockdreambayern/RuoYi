package com.scsentry.common.enums;

import lombok.Getter;

@Getter
public enum TestParameterType {

    FUNCTION("func", false, 1, "产品质量-功能性"),
    PERFORMANCE("perf", false, 2, "产品质量-性能效率"),
    SECURITY_FUNCTION("securityFunc", true, 3, "产品质量-信息安全性（安全功能）"),
    PENETRATION_TEST("penetrationTest", true, 4, "产品质量-信息安全性（渗透测试）"),
    VULNERABILITY_SCAN("vulnerabilityScan", true, 5, "产品质量-信息安全性（漏洞扫描）"),
    CODE_AUDIT("codeAudit", true, 6, "产品质量-信息安全性（代码审计）");

    private String code;

    //是否为信息安全测试项
    private boolean isInfoSecurity;

    private int order;

    private String description;

    TestParameterType(String code, boolean isInfoSecurity, int order, String description) {
        this.code = code;
        this.isInfoSecurity = isInfoSecurity;
        this.order = order;
        this.description = description;
    }

    public static TestParameterType findByCode(String code) {
        for (TestParameterType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
