package com.scsentry.common.enums;

import lombok.Getter;

/**
 * 任务状态
 */
@Getter
public enum TaskStatus {

    DOING(-1, "进行中"),
    SUCCESS(0, "成功"),
    FAIL(1, "失败");

    private int code;

    private String description;

    TaskStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }
}
