package com.scsentry.service.testreport;

import com.scsentry.model.GenerateTestReportTask;
import com.scsentry.model.testreport.TestReportMetaData;
import com.scsentry.service.testreport.impl.TestReportBuilderV2_5;
import org.springframework.util.Assert;

import java.io.File;

/**
 * 检测报告服务
 *
 * @author luokun
 */
public class TestReportService {

    /**
     * 生成检测报告
     * @param task 生成检测报告任务
     */
    public void generateTestReport(GenerateTestReportTask task) {
        Assert.notNull(task.getReportTemplate(), "报告模板文件为空");

        TestReportMetaData testReportMetaData = parseMetaData(task.getReportTemplate());

        testReportMetaData.check();

        TestReportBuilder testReportBuilder = getTestReportBuilder();

        File report = testReportBuilder.build(testReportMetaData);

        saveReport(report);
    }

    private TestReportMetaData parseMetaData(File file) {
        return TestReportMetaData.parse(file);
    }

    private void saveReport(File file) {

    }

    private TestReportBuilder getTestReportBuilder() {
        return new TestReportBuilderV2_5();
    }


}
