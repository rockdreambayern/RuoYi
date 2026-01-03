package com.scsentry.service.testreport;

import com.scsentry.model.testreport.TestReportMetaData;

import java.io.File;

public interface TestReportBuilder {

    File build(TestReportMetaData testReportMetaData);
}
