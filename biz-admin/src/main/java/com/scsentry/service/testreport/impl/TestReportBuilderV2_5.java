package com.scsentry.service.testreport.impl;

import com.ruoyi.common.exception.base.BaseException;
import com.scsentry.model.testreport.TestReportMetaData;
import com.scsentry.model.testreport.data.*;
import com.scsentry.service.testreport.TestReportBuilder;
import org.docx4j.model.datastorage.migration.VariablePrepare;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.WordprocessingML.HeaderPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.relationships.RelationshipsPart;
import org.docx4j.relationships.Relationship;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class TestReportBuilderV2_5 implements TestReportBuilder {
    @Override
    public File build(TestReportMetaData testReportMetaData) {
        try {
            Map<String, Object> map = testReportMetaData.getMetadata();
            Map<String, String> parameters = new HashMap<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() instanceof String) {
                    parameters.put(entry.getKey(), (String)entry.getValue());
                }
            }

            // 2. 将渲染后的 XML 写入到 WordprocessingMLPackage
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new File("template_freemarker.docx"));;
            MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();

            VariablePrepare.prepare(wordMLPackage);
            mdp.variableReplace(parameters);
            replaceInHeaders(wordMLPackage, parameters);

            List<RequirementCollection> requirementCollections = buildRequirementCollections(testReportMetaData.getTestContentCollectionList());
            for (RequirementCollection requirementCollection : requirementCollections) {
                requirementCollection.generateTable(wordMLPackage);
            }

            TopologyPicture topologyPicture = new TopologyPicture();
            topologyPicture.generatePicture(wordMLPackage, mdp, testReportMetaData.getImage());

            Environment environment = testReportMetaData.getEnvironment();
            environment.generateTable(wordMLPackage);

            List<TestContentCollection> testContentCollectionList = testReportMetaData.getTestContentCollectionList();
            testContentCollectionList.forEach(testContentCollection -> testContentCollection.generateTable(wordMLPackage));

            Statistic statistic = new Statistic(testContentCollectionList);
            statistic.generateTestCaseTable(wordMLPackage);

        } catch (Exception e) {
            throw new BaseException("生成检测报告失败");
        }


        return null;
    }

    private List<RequirementCollection> buildRequirementCollections(List<TestContentCollection> testContentCollectionList) {
        boolean needInfoSecurity = false;
        List<RequirementCollection> requirementCollections = new ArrayList<>();
        for (TestContentCollection testContentCollection : testContentCollectionList) {
            if (testContentCollection.getTestParameterType().isInfoSecurity()) {
                if (!needInfoSecurity) {
                    requirementCollections.add(generateInfoSecurityRequirementCollection());
                    needInfoSecurity = true;
                }
                continue;
            }

            List<Requirement> requirements = testContentCollection.getTestContents().stream().map(content -> new Requirement(content.getRequirement(), content.getContent())).collect(Collectors.toList());
            RequirementCollection requirementCollection = new RequirementCollection(testContentCollection.getTestContentName(), requirements);
            requirementCollections.add(requirementCollection);
        }
        return requirementCollections;
    }

    private static RequirementCollection generateInfoSecurityRequirementCollection() {
        List<Requirement> requirements = Arrays.asList(
                new Requirement("保密性", "指产品或系统确保数据只有在被授权才能被访问的程度。"),
                new Requirement("完整性", "指系统、产品或组件防止未被授权访问、篡改计算机程序或数据的程度。"),
                new Requirement("抗抵赖性", "指活动或事件发生后可以被证实且不可被否认的程度。"),
                new Requirement("可核查性", "指实体的活动可以被唯一地追溯到该实体的程度。"),
                new Requirement("真实性", "指对象或资源的身份标识能够被证实符合其声明的程度。"),
                new Requirement("依从性", "指产品或系统遵循与信息全息性相关标准、约定或法规以及类似规定的程度。")
        );
        return new RequirementCollection("产品质量-信息安全性", requirements);
    }

    private void replaceInHeaders(WordprocessingMLPackage wordMLPackage, Map<String, String> vars) {
        try {
            MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();

            // RelationshipsPart 包含了 main doc 的所有 child parts（包括 header/footer）
            RelationshipsPart rp = mdp.getRelationshipsPart();

            if (rp == null) return;

            List<Relationship> rels = rp.getJaxbElement().getRelationship();
            for (Relationship r : rels) {
                // 关系类型以 Namespaces.HEADER 标识 header
                String type = r.getType();
                // 也可以用: org.docx4j.openpackaging.parts.relationships.Namespaces.HEADER_URI
                if (type != null && type.endsWith("/header")) { // 简单判断 header 类型
                    // 通过 relationship 获取对应的 Part
                    Part p = rp.getPart(r);
                    if (p instanceof HeaderPart) {
                        HeaderPart hp = (HeaderPart) p;
                        // variableReplace 可直接替换 HeaderPart 中的 ${...}
                        hp.variableReplace(vars);
                    }
                }
            }
        } catch (Exception e) {
            throw new BaseException("系统内部错误");
        }

    }
}
