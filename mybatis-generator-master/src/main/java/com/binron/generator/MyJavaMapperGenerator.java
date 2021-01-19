package com.binron.generator;

import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.codegen.mybatis3.javamapper.JavaMapperGenerator;

import java.util.List;

/**
 * @Description: 生成实体  去除tab 可以根据需求去除
 *
 * @author xub
 * @date 2019/8/7 下午1:59
 */
public class MyJavaMapperGenerator extends JavaMapperGenerator {

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        introspectedTable.setMyBatis3JavaMapperType(introspectedTable.getMyBatis3JavaMapperType().replace("Tab", ""));
        introspectedTable.setMyBatis3XmlMapperFileName(introspectedTable.getMyBatis3XmlMapperFileName().replace("Tab", ""));
        return super.getCompilationUnits();
    }

}
