package com.scsentry.service.testreport;

import com.scsentry.common.enums.TestParameterType;

import java.util.*;
import java.util.stream.Collectors;

public class TestParametersParser {

    private static final String CHECKED = "☒";

    public void parse(Map<String, Object> metadata) {
        TestParameters testParameters = new TestParameters();
        for (Map.Entry<String, Object> entry : metadata.entrySet()) {
            TestParameterType type = TestParameterType.findByCode(entry.getKey());
            if (type != null) {
                if (CHECKED.equals(entry.getValue())) {
                    testParameters.addParameter(type);
                }
            }
        }


        metadata.put("testParameters", testParameters);

        String testParametersText = testParameters.getParameters().stream().map(p -> {
                    if (p.isInfoSecurity()) {
                        return "产品质量-信息安全性";
                    }
                    return p.getDescription();
                }).distinct().filter(Objects::nonNull)
                .map(s -> "“" + s + "”")    // -> "xxx"
                .collect(Collectors.joining("、"));

        metadata.put("testParametersText", testParametersText);
    }

    public static class TestParameters {

        private List<TestParameterType> parameters = new ArrayList<>();

        private boolean sorted = false;

        public void addParameter(TestParameterType parameter) {
            if (!parameters.contains(parameter)) {
                parameters.add(parameter);
            }
        }

        public List<TestParameterType> getParameters() {
            if (!sorted) {
                parameters.sort(Comparator.comparingInt(TestParameterType::getOrder));
                sorted = true;
            }
            return parameters;
        }
    }
}
