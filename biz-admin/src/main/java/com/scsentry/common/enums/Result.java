package com.scsentry.common.enums;

import lombok.Getter;

@Getter
public enum Result {

    PASS("pass", "通过"),
    FAIL("fail", "不通过");

    private final String code;

    private final String desc;

    Result(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static Result findByDesc(String desc) {
        for (Result result : values()) {
            if (result.getDesc().equals(desc)) {
                return result;
            }
        }

        return Result.PASS;
    }
}
