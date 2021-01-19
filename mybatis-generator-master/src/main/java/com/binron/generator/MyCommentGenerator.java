package com.binron.generator;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.PropertyRegistry;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;

/**
 * @Description: Mapper 修改 生成指定的mapper
 *
 * @author xub
 * @date 2019/8/7 下午2:01
 */
public class MyCommentGenerator implements CommentGenerator {
    private Properties properties;
    private Properties systemPro;
    private boolean suppressDate;
    private boolean suppressAllComments;
    private String currentDateStr;

    public MyCommentGenerator() {
        super();
        properties = new Properties();
        systemPro = System.getProperties();
        suppressDate = false;
        suppressAllComments = false;
        currentDateStr = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
    }

    public void addJavaFileComment(CompilationUnit compilationUnit) {

    }

    public void addComment(XmlElement xmlElement) {

    }

    public void addRootComment(XmlElement rootElement) {

    }

    public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> set) {

    }

    public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> set) {

    }

    public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> set) {

    }

    public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> set) {

    }

    public void addClassAnnotation(InnerClass innerClass, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> set) {

    }


    public void addConfigurationProperties(Properties properties) {
        this.properties.putAll(properties);
        suppressDate = isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_DATE));
        suppressAllComments = isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_ALL_COMMENTS));
    }

    private String getDateString() {
        String result = null;
        if (!suppressDate) {
            result = currentDateStr;
        }
        return result;
    }

    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        innerClass.addJavaDocLine("/**");
        sb.append(" * ");
        sb.append(introspectedTable.getFullyQualifiedTable());
        sb.append(" ");
        sb.append(getDateString());
        innerClass.addJavaDocLine(sb.toString().replace("\n", " "));
        innerClass.addJavaDocLine(" */");
    }

    public void addEnumComment(InnerEnum innerEnum, IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        innerEnum.addJavaDocLine("/**");
        sb.append(" * ");
        sb.append(introspectedTable.getFullyQualifiedTable());
        innerEnum.addJavaDocLine(sb.toString().replace("\n", " "));
        innerEnum.addJavaDocLine(" */");
    }

    public void addFieldComment(Field field, IntrospectedTable introspectedTable,
                                IntrospectedColumn introspectedColumn) {
        if (suppressAllComments) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        field.addJavaDocLine("/**");
        sb.append(" * ");
        sb.append(introspectedColumn.getRemarks());
        field.addJavaDocLine(sb.toString().replace("\n", " "));
        field.addJavaDocLine(" */");
    }

    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        field.addJavaDocLine("/**");
        sb.append(" * ");
        sb.append(introspectedTable.getFullyQualifiedTable());
        field.addJavaDocLine(sb.toString().replace("\n", " "));
        field.addJavaDocLine(" */");
    }


    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addJavaDocLine("/**");
        String sb = " * " + introspectedTable.getFullyQualifiedTable();
        topLevelClass.addJavaDocLine(sb.replace("\n", " "));
        topLevelClass.addJavaDocLine(" */");
    }

    private final static String MAPPER_METHOD_DOC = "/**\n" +
            "     * %s\n" +
            "     *\n" +
            "     * @param %s %s\n" +
            "     * @return %s\n" +
            "     */";

    private final static Map<String, String[]> MAPPER_METHOD_DOC_MAP;

    static {
        MAPPER_METHOD_DOC_MAP = new HashMap();
        MAPPER_METHOD_DOC_MAP.put("deleteByPrimaryKey", new String[]{"根据主键删除", "主键", "更新条目数"});
        MAPPER_METHOD_DOC_MAP.put("insert", new String[]{"插入一条记录", "实体对象", "更新条目数"});
        MAPPER_METHOD_DOC_MAP.put("insertSelective", new String[]{"动态插入一条记录", "实体对象", "更新条目数"});
        MAPPER_METHOD_DOC_MAP.put("selectByPrimaryKey", new String[]{"根据主键查询", "主键", "实体对象"});
        MAPPER_METHOD_DOC_MAP.put("updateByPrimaryKeySelective", new String[]{"根据主键动态更新记录", "实体对象", "更新条目数"});
        MAPPER_METHOD_DOC_MAP.put("updateByPrimaryKeyWithBLOBs", new String[]{"根据主键更新记录，包括二进制大对象", "实体对象", "更新条目数"});
        MAPPER_METHOD_DOC_MAP.put("updateByPrimaryKey", new String[]{"根据主键更新记录", "实体对象", "更新条目数"});
    }

    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
        if (MAPPER_METHOD_DOC_MAP.containsKey(method.getName())) {
            String[] strings = MAPPER_METHOD_DOC_MAP.get(method.getName());
            method.addJavaDocLine(String.format(MAPPER_METHOD_DOC, strings[0], method.getParameters().iterator().next().getName(), strings[1], strings[2]));
        }
    }

    public void addGetterComment(Method method, IntrospectedTable introspectedTable,
                                 IntrospectedColumn introspectedColumn) {
    }

    public void addSetterComment(Method method, IntrospectedTable introspectedTable,
                                 IntrospectedColumn introspectedColumn) {
    }

    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
        if (suppressAllComments) {
            return;
        }
        innerClass.addJavaDocLine("/**");
        String sb = " * " + introspectedTable.getFullyQualifiedTable();
        innerClass.addJavaDocLine(sb.replace("\n", " "));
        innerClass.addJavaDocLine(" */");
    }
}