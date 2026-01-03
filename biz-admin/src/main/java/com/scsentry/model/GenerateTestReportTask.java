package com.scsentry.model;

import com.scsentry.common.enums.TaskStatus;
import lombok.Builder;
import lombok.Data;

import java.io.File;

@Data
@Builder
public class GenerateTestReportTask {

    private String projectName;

    private int status;

    private String errorMessage;

    private String createTime;

    private File reportTemplate;

    public void success() {
        if (status == TaskStatus.DOING.getCode()) {
            status = TaskStatus.SUCCESS.getCode();
        }
    }

    public void fail(String errorMessage) {
        if (status == TaskStatus.DOING.getCode()) {
            status = TaskStatus.FAIL.getCode();
            this.errorMessage = errorMessage;
        }
    }
}
