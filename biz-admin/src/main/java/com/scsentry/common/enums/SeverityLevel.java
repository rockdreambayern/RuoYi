package com.scsentry.common.enums;

import lombok.Getter;

@Getter
public enum SeverityLevel {

    CRITICAL(0, "致命"),
    MAJOR(1, "严重"),
    MINOR(2, "一般"),
    TRIVIAL(3, "轻微");


    private int code;

    private String desc;

    SeverityLevel(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static SeverityLevel findByDesc(String desc) {
        for (SeverityLevel level : values()) {
            if (level.getDesc().equals(desc)) {
                return level;
            }
        }

        return SeverityLevel.MINOR;
    }
}
