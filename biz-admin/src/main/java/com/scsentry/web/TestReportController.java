package com.scsentry.web;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.scsentry.model.GenerateTestReportTask;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 检测报告Controller
 *
 * @author luokun
 */
@Controller
public class TestReportController extends BaseController {

    // 系统首页
    @GetMapping("/generateTestReport")
    public String generateTestReport(ModelMap mmap, HttpServletRequest request) {
        return "/testReport/generateTestReport";
    }

    @PostMapping("/testReport/task/list")
    @ResponseBody
    public TableDataInfo listGenerateTestReportTasks() {
        List<GenerateTestReportTask> tasks = new ArrayList<>();
        GenerateTestReportTask task = GenerateTestReportTask.builder()
                .projectName("供应链系统")
                .status(0)
                .errorMessage("")
                .createTime("2025年05月02日 20:20:00")
                .build();
        tasks.add(task);
        task = GenerateTestReportTask.builder()
                .projectName("收银系统")
                .status(1)
                .errorMessage("测试负责人未指定")
                .createTime("2025年08月02日 20:28:00")
                .build();
        tasks.add(task);
        return getDataTable(tasks);
    }
}
