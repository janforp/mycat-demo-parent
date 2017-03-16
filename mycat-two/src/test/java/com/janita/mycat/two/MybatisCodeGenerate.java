package com.janita.mycat.two;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 生成一张表的Modal / Dao / DaoImpl /sql**-mapper.xml
 *
 * @Author : Janita
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MybatisCodeGenerate {
    // -custom
    private static String[] tableNames = new String[]{"orders","order_detail"};
    // 程序会自动找到basePath
    private static String basePath = null;

    private static Logger log = LoggerFactory.getLogger(MybatisCodeGenerate.class);

    private static final String DAO_PACKAGE = "com.janita.mycat.two.dao" ;

    @Autowired
    private DruidDataSource dataSource;


    @Test
    public void codegen() throws Exception {
        try {
            if (basePath == null || basePath.trim().length() == 0) {
                // /home/wuqiang/DEV/idea-workspace/server/lswuyou-daemon/trunk
                basePath = System.getProperty("user.dir");
            }
            if (basePath == null || basePath.trim().length() == 0) {
                System.err.println("[ERROR] 请配置basePath。");
                return;
            }
            if (!basePath.endsWith(File.separator)) {
                basePath = basePath + File.separator;
            }
            for (String tableName : tableNames) {
                codegenForOneTable(tableName);
                System.out.println(tableName + " - 生成完毕！");
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (dataSource != null) {
                try {
                    dataSource.close();
                } catch (Exception e) {
                    throw e;
                }
            }
        }
    }

    public void codegenForOneTable(String oneTableName) throws Exception {
        String sourcePath = "src/main/java/";
        String sqlmapBasePath = "src/main/resources/mapper/";
        String schema = "node2";
        String fileCharset = "utf-8";
        String modalPackage = "com.janita.mycat.two.bean";
        String daoPackage = "com.janita.mycat.two.dao";
//        String daoImplPackage = "com.hongbao.api.dao.impl";
        String modalFilePath;
        String daoFilePath;
        String daoImplFilePath;
        String sqlMapperFilePath;
        String customSqlMapperFilePath;

        sourcePath = basePath + sourcePath;
        sqlmapBasePath = basePath + sqlmapBasePath;
        if (!sourcePath.endsWith(File.separator)) {
            sourcePath = sourcePath + File.separator;
        }
        if (!sqlmapBasePath.endsWith(File.separator)) {
            sqlmapBasePath = sqlmapBasePath + File.separator;
        }
        modalFilePath =
                sourcePath + modalPackage.replace(".", File.separator) + File.separator
                        + getMobalNameByTableName(oneTableName) + ".java";
        daoFilePath =
                sourcePath + daoPackage.replace(".", File.separator) + File.separator
                        + getDaoNameByTableName(oneTableName) + ".java";
//        daoImplFilePath =
//                sourcePath + daoImplPackage.replace(".", File.separator) + File.separator
//                        + getDaoImplNameByTableName(oneTableName) + ".java";
        sqlMapperFilePath =
                sqlmapBasePath + getSqlmapFileNameByTableName(oneTableName);
        customSqlMapperFilePath =
                sqlmapBasePath + getCustomSqlmapFileNameByTableName(oneTableName);
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            TableInfo tableInfo = getTableInfo(conn, schema, oneTableName);
            if (tableInfo.getPrimaryKeys() == null || tableInfo.getPrimaryKeys().size() == 0) {
                System.err.println("[ERROR] " + oneTableName + " 没有主键，无法生成。");
                return;
            }
            if (new File(modalFilePath).exists()) {
                System.err
                        .println("[WARN] 实体类 " + getMobalNameByTableName(oneTableName) + " 已存在，将会覆盖。");
            }
            String modalSource = buildModal(tableInfo, modalPackage);
            writeText(new File(modalFilePath), modalSource, fileCharset);
            {
                if (new File(sqlMapperFilePath).exists()) {
                    moveFile(new File(sqlMapperFilePath), new File(sqlMapperFilePath + ".bak"));
                    System.err.println(
                            "[WARN] " + sqlMapperFilePath + " 已存在，备份至：" + getSqlmapFileNameByTableName(
                                    oneTableName)
                                    + ".bak");
                }
                String sqlmapSource = buildSqlMapper(tableInfo, modalPackage);
                writeText(new File(sqlMapperFilePath), sqlmapSource, fileCharset);
            }
            {
                if (!new File(customSqlMapperFilePath).exists()) {
                    // 自定义SQL文件不存在,才创建
                    String customSqlmapSource = buildCustomSqlMapper(tableInfo, modalPackage);
                    writeText(new File(customSqlMapperFilePath), customSqlmapSource, fileCharset);
                }
            }

            {
                if (!new File(daoFilePath).exists()) {
                    // 不存在才创建
                    String daoSource = buildDao(tableInfo, daoPackage, modalPackage);
                    writeText(new File(daoFilePath), daoSource, fileCharset);
                }
            }
            {
//                if (!new File(daoImplFilePath).exists()) {
//                    // 不存在才创建
//                    String
//                            daoImplSource =
//                            buildDaoImpl(tableInfo, daoImplPackage, daoPackage, modalPackage);
//                    writeText(new File(daoImplFilePath), daoImplSource, fileCharset);
//                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    throw e;
                }
            }
        }
    }

    private String getAuthorInfo() {
        String newLine = "\n";
        StringBuilder buf = new StringBuilder(128);
        buf.append("/**").append(newLine);
        buf.append(" * Created by " + this.getClass().getName() + " on ").append(new SimpleDateFormat("yyyy-MM-dd").format(new Date())).append(newLine);
        buf.append(" */").append(newLine);
        return buf.toString();
    }

    private String buildModal(TableInfo tableInfo, String modalPackage) {
        StringBuilder buf = new StringBuilder(4096);
        String modalName = getMobalNameByTableName(tableInfo.getTableName());
        String newLine = "\n";
        buf.append("package ").append(modalPackage).append(";").append(newLine);
        buf.append(newLine);
        if (tableInfo.isImportUtil() || tableInfo.isImportSql() || tableInfo.isImportMath()) {
            if (tableInfo.isImportSql()) {
                buf.append("import java.sql.*;").append(newLine);
            }
            if (tableInfo.isImportUtil()) {
                buf.append("import java.util.*;").append(newLine);
            }
            if (tableInfo.isImportMath()) {
                buf.append("import java.math.*;").append(newLine);
            }
            buf.append(newLine);
        }
        buf.append(getAuthorInfo());
        buf.append("public class ").append(modalName).append(" implements java.io.Serializable {")
                .append(newLine);
        buf.append(newLine);
        buf.append("    // Fields").append(newLine);
        buf.append(newLine);
        StringBuilder methods = new StringBuilder(2048);
        StringBuilder constructors = new StringBuilder(2048);
        StringBuilder constructorInners = new StringBuilder(1024);
        constructors.append("    ").append("// Constructors").append(newLine);
        {
            // default constructor
            constructors.append(newLine);
            constructors.append("    ").append("/**").append(newLine);
            constructors.append("    ").append(" * default constructor").append(newLine);
            constructors.append("    ").append(" */").append(newLine);
            constructors.append("    ").append("public ").append(modalName).append("() {").append(
                    newLine);
            constructors.append("    ").append("}").append(newLine);
        }
        Map<String, String> columnCommentMap = tableInfo.getColumnCommentMap();
        int i = 0;
        int fullConstructorAddFieldIndex = 0;
        for (String column : tableInfo.getColumns()) {
            String propertyName = underlineToCamel(column);
            String propertyNameInitCap = initCap(propertyName);
            String javaType = getJavaTypeByJdbcType(tableInfo.getColumnTypes().get(column));

            String comment = columnCommentMap.get(column);
            if (comment != null) {
                buf.append("    ").append("// ").append(comment).append(newLine);
            }
            buf.append("    ").append("private ").append(javaType).append(" ").append(propertyName)
                    .append(";").append(newLine);

            if (i == 0) {
                // 第一个属性
                constructors.append(newLine);
                constructors.append("    ").append("/**").append(newLine);
                constructors.append("    ").append(" * full constructor").append(newLine);
                constructors.append("    ").append(" */").append(newLine);
                constructors.append("    ").append("public ").append(modalName).append("(");
                methods.append("    ").append("// Property accessors").append(newLine);
            }
            if (tableInfo.isPrimaryKeyAutoIncrement() && tableInfo.getPrimaryKeys()
                    .contains(column)) {
                // 这个字段是主键,且是自增长,就不添加到full constructor
            } else {
                constructorInners.append("        ").append("this.").append(propertyName)
                        .append(" = ")
                        .append(propertyName).append(";").append(newLine);
                if (fullConstructorAddFieldIndex == 0) {
                    constructors.append(javaType).append(" ").append(propertyName);
                }
                if (fullConstructorAddFieldIndex != 0) {
                    constructors.append(", ").append(javaType).append(" ").append(propertyName);
                }
                fullConstructorAddFieldIndex++;
            }
            {
                methods.append(newLine);
                if (comment != null) {
                    methods.append("    ").append("/**").append(newLine);
                    methods.append("    ").append(" * ").append(comment).append(newLine);
                    methods.append("    ").append(" */").append(newLine);
                }
                methods.append("    ").append("public ").append(javaType).append(" get")
                        .append(propertyNameInitCap)
                        .append("() {").append(newLine);
                methods.append("        ").append("return this.").append(propertyName).append(";")
                        .append(newLine);
                methods.append("    ").append("}").append(newLine);
                methods.append(newLine);
                if (comment != null) {
                    methods.append("    ").append("/**").append(newLine);
                    methods.append("    ").append(" * ").append(comment).append(newLine);
                    methods.append("    ").append(" */").append(newLine);
                }
                methods.append("    ").append("public void set")
                        .append(propertyNameInitCap)
                        .append("(").append(javaType).append(" ").append(propertyName).append(
                        ") {").append(newLine);
                methods.append("        ").append("this.").append(propertyName).append(" = ")
                        .append(propertyName).append(";")
                        .append(newLine);
                methods.append("    ").append("}").append(newLine);
            }
            if (i == tableInfo.getColumns().size() - 1) {
                // 最后一个属性
                constructors.append(") {").append(newLine);
                constructors.append(constructorInners);
                constructors.append("    ").append("}").append(newLine);
            }
            i++;
        }
        buf.append(newLine);
        buf.append(constructors);
        buf.append(newLine);
        buf.append(methods);
        buf.append(newLine);
        buf.append("}");
//        System.out.println(buf);
        return buf.toString();
    }

    private String buildDao(TableInfo tableInfo, String daoPackage, String modalPackage) {
        StringBuilder buf = new StringBuilder(1024);
        String tableName = tableInfo.getTableName();
        String daoName = getDaoNameByTableName(tableName);
        String modalName = getMobalNameByTableName(tableInfo.getTableName());
        String modalNameWithPackage = modalPackage + "." + modalName;
        String newLine = "\n";
        buf.append("package ").append(daoPackage).append(";").append(newLine);
        buf.append(newLine);
        buf.append("import ").append(modalNameWithPackage).append(";").append(newLine);
        buf.append(newLine);
        buf.append("import java.util.List;").append(newLine);
        buf.append(newLine);
        buf.append(getAuthorInfo());
        buf.append("public interface " + daoName + " {").append(newLine);
        {
            buf.append("    ").append("int deleteByPrimaryKey(");
            int i = 0;
            for (String column : tableInfo.getPrimaryKeys()) {
                String propertyName = underlineToCamel(column);
                String javaType = getJavaTypeByJdbcType(
                        tableInfo.getColumnTypes().get(column));
                if (i > 0) {
                    buf.append(", ");
                }
                buf.append(javaType).append(" ").append(propertyName);
                i++;
            }
            buf.append(");").append(newLine).append(newLine);
        }
        {

            buf.append("    ").append("void insert(" + modalName + " record);").append(newLine)
                    .append(newLine);
            buf.append("    ").append("void insertSelective(" + modalName + " record);")
                    .append(newLine).append(newLine);
            buf.append("    ").append("void insertBatch(List<" + modalName + "> records);").append(newLine)
                    .append(newLine);
        }
        {
            buf.append("    ").append(modalName + " selectByPrimaryKey(");
            int i = 0;
            for (String column : tableInfo.getPrimaryKeys()) {
                String propertyName = underlineToCamel(column);
                String javaType = getJavaTypeByJdbcType(
                        tableInfo.getColumnTypes().get(column));
                if (i > 0) {
                    buf.append(", ");
                }
                buf.append(javaType).append(" ").append(propertyName);
                i++;
            }
            buf.append(");")
                    .append(newLine).append(newLine);
        }
        {
            buf.append("    ").append("int updateByPrimaryKeySelective(" + modalName + " record);")
                    .append(newLine).append(newLine);
            buf.append("    ").append("int updateByPrimaryKey(" + modalName + " record);")
                    .append(newLine);
        }

        buf.append("}");
//        System.out.println(buf);
        return buf.toString();
    }

    private String buildDaoImpl(TableInfo tableInfo, String daoImplPackage, String daoPackage,
                                String modalPackage) {
        StringBuilder buf = new StringBuilder(1024);
        String tableName = tableInfo.getTableName();
        String daoNameWithPackage = daoPackage + "." + getDaoNameByTableName(tableName);
        String daoName = getDaoNameByTableName(tableName);
        String daoImplName = getDaoImplNameByTableName(tableName);
        String modalName = getMobalNameByTableName(tableInfo.getTableName());
        String modalNameWithPackage = modalPackage + "." + modalName;
        String newLine = "\n";
        buf.append("package ").append(daoImplPackage).append(";").append(newLine);
        buf.append(newLine);
        buf.append("import " + daoNameWithPackage + ";").append(newLine);
        buf.append("import com.hongbao.api.dao.BaseSqlSessionDaoSupport;").append(newLine);
        buf.append("import ").append(modalNameWithPackage).append(";").append(newLine);
        buf.append(newLine);
        buf.append("import org.springframework.stereotype.Repository;").append(newLine);
        buf.append(newLine);
        buf.append("import java.util.List;").append(newLine);
        buf.append(newLine);
        buf.append(getAuthorInfo());
        buf.append("@Repository").append(newLine);
        buf.append("public class " + daoImplName
                + " extends BaseSqlSessionDaoSupport\n        implements " + daoName + " {")
                .append(newLine);
        {
            buf.append("    ").append("public int deleteByPrimaryKey(");
            int i = 0;
            for (String column : tableInfo.getPrimaryKeys()) {
                String propertyName = underlineToCamel(column);
                String javaType = getJavaTypeByJdbcType(
                        tableInfo.getColumnTypes().get(column));
                if (i > 0) {
                    buf.append(", ");
                }
                buf.append(javaType).append(" ").append(propertyName);
                i++;
            }
            buf.append(") {").append(newLine);
            buf.append("        ").append(modalName + " _key = new " + modalName + "();")
                    .append(newLine);

            i = 0;
            for (String column : tableInfo.getPrimaryKeys()) {
                String propertyName = underlineToCamel(column);
                String javaType = getJavaTypeByJdbcType(
                        tableInfo.getColumnTypes().get(column));
                if (i > 0) {
                    buf.append(newLine);
                }
                buf.append("        ")
                        .append("_key.set" + initCap(propertyName) + "(" + propertyName + ");");
                i++;
            }
            buf.append(newLine);
            buf.append("        ").append("return getSqlSession().delete(\"" + tableName
                    + ".deleteByPrimaryKey\", _key);")
                    .append(newLine);
            buf.append("    ").append("}").append(newLine);
            buf.append(newLine);
        }
        {
            buf.append("    ").append("public void insert(" + modalName + " record) {").append(
                    newLine);
            buf.append("        ")
                    .append("getSqlSession().insert(\"" + tableName + ".insert\", record);")
                    .append(newLine);
            buf.append("    ").append("}").append(newLine);
            buf.append(newLine);
        }
        {
            buf.append("    ").append("public void insertSelective(" + modalName + " record) {")
                    .append(
                            newLine);
            buf.append("        ")
                    .append("getSqlSession().insert(\"" + tableName
                            + ".insertSelective\", record);")
                    .append(newLine);
            buf.append("    ").append("}").append(newLine);
            buf.append(newLine);
        }
        {
            buf.append("    ").append("public void insertBatch(List<" + modalName + "> records) {")
                    .append(
                            newLine);
            buf.append("        ").append("if (records == null || records.isEmpty()) {").append(newLine);
            buf.append("            ").append("return;").append(newLine);
            buf.append("        ").append("}").append(newLine);
            buf.append("        ")
                    .append("getSqlSession().insert(\"" + tableName
                            + ".insertBatch\", records);")
                    .append(newLine);
            buf.append("    ").append("}").append(newLine);
            buf.append(newLine);
        }

        {
            buf.append("    ").append("public " + modalName + " selectByPrimaryKey(");
            int i = 0;
            for (String column : tableInfo.getPrimaryKeys()) {
                String propertyName = underlineToCamel(column);
                String javaType = getJavaTypeByJdbcType(
                        tableInfo.getColumnTypes().get(column));
                if (i > 0) {
                    buf.append(", ");
                }
                buf.append(javaType).append(" ").append(propertyName);
                i++;
            }
            buf.append(") {").append(newLine);
            buf.append("        ").append(modalName + " _key = new " + modalName + "();")
                    .append(newLine);

            i = 0;
            for (String column : tableInfo.getPrimaryKeys()) {
                String propertyName = underlineToCamel(column);
                String javaType = getJavaTypeByJdbcType(
                        tableInfo.getColumnTypes().get(column));
                if (i > 0) {
                    buf.append(newLine);
                }
                buf.append("        ")
                        .append("_key.set" + initCap(propertyName) + "(" + propertyName + ");");
                i++;
            }
            buf.append(newLine);
            buf.append("        ").append(
                    "return getSqlSession().selectOne(\"" + tableName
                            + ".selectByPrimaryKey\", _key);")
                    .append(newLine);
            buf.append("    ").append("}").append(newLine);
            buf.append(newLine);
        }
        {
            buf.append("    ")
                    .append("public int updateByPrimaryKeySelective(" + modalName + " record) {")
                    .append(
                            newLine);
            buf.append("        ")
                    .append("return getSqlSession().update(\"" + tableName
                            + ".updateByPrimaryKeySelective\", record);")
                    .append(newLine);
            buf.append("    ").append("}").append(newLine);
            buf.append(newLine);
        }
        {
            buf.append("    ").append("public int updateByPrimaryKey(" + modalName + " record) {")
                    .append(
                            newLine);
            buf.append("        ")
                    .append("return getSqlSession().update(\"" + tableName
                            + ".updateByPrimaryKey\", record);")
                    .append(newLine);
            buf.append("    ").append("}").append(newLine);
        }

        buf.append("}");

//        System.out.println(buf);
        return buf.toString();
    }

    private String buildCustomSqlMapper(TableInfo tableInfo, String modalPackage) {
        StringBuilder buf = new StringBuilder(4096);
        String tableName = tableInfo.getTableName();
        String modalName = modalPackage + "." + getMobalNameByTableName(tableInfo.getTableName());
        String daoName = DAO_PACKAGE+"."+getDaoNameByTableName(tableName);
        String newLine = "\n";
        buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>").append(newLine);
        buf.append(
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >")
                .append(newLine);
        buf.append(
                "<mapper namespace=\"").append(daoName).append("\">")
                .append(newLine);
        buf.append("</mapper>");
        return buf.toString();
    }

    private String buildSqlMapper(TableInfo tableInfo, String modalPackage) {
        StringBuilder buf = new StringBuilder(4096);
        String tableName = tableInfo.getTableName();
        String modalName = modalPackage + "." + getMobalNameByTableName(tableInfo.getTableName());
        String daoName = DAO_PACKAGE+"."+getDaoNameByTableName(tableName);
        String newLine = "\n";
        buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>").append(newLine);
        buf.append(
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >")
                .append(newLine);
        buf.append(
                "<mapper namespace=\"").append(daoName).append("\">")
                .append(newLine);
        List<String> columns = tableInfo.getColumns();
        int columnsSize = columns.size();
        List<String> columnsIds = new ArrayList<>(columnsSize);
        List<String> columnsNotIds = new ArrayList<>(columnsSize);
        for (String column : columns) {
            if (tableInfo.getPrimaryKeys().contains(column)) {
                columnsIds.add(column);
            } else {
                columnsNotIds.add(column);
            }
        }
        {
            List<String> newColumnList = new ArrayList<>(columnsSize);
            newColumnList.addAll(columnsIds);
            newColumnList.addAll(columnsNotIds);
            columns = newColumnList;
        }
        {
            // BaseResultMap
            buf.append("    ").append("<resultMap id=\"BaseResultMap\" type=\"").append(modalName)
                    .append(
                            "\">").append(newLine);
            int i = 0;

            for (String column : columns) {
                String propertyName = underlineToCamel(column);
                String jdbcType = getJdbcTypeByJdbcTypeForSqlMap(
                        tableInfo.getColumnTypes().get(column));
                buf.append("        ");
                if (tableInfo.getPrimaryKeys().contains(column)) {
                    buf.append("<id");
                } else {
                    buf.append("<result");
                }
                buf.append(" column=\"").append(column)
                        .append("\" property=\"").append(propertyName).append(
                        "\" jdbcType=\"").append(jdbcType).append("\"/>").append(newLine);
                i++;
            }
            buf.append("    ").append("</resultMap>").append(newLine);
        }
        {
            // SELECT_All_Column
            buf.append("    ").append("<sql id=\"SELECT_All_Column\">").append(newLine);
            buf.append("        ");
            buf.append("SELECT ");
            int i = 0;
            for (String column : columns) {
                if (i > 0) {
                    buf.append(", ");
                    if (i % 5 == 0) {
                        buf.append(newLine).append("        ");
                    }
                }
                buf.append(caseDbSensitiveWords(column));
                i++;
            }
            buf.append(newLine);
            buf.append("    ").append("</sql>").append(newLine);
        }
        {
            // selectByPrimaryKey
            buf.append("    ").append(
                    "<select id=\"selectByPrimaryKey\" resultMap=\"BaseResultMap\" parameterType=\"")
                    .append(
                            modalName)
                    .append("\">").append(newLine);
            buf.append("        ").append("SELECT *")
                    .append(
                            newLine);
            buf.append("        ").append("FROM ").append(tableName).append(newLine);
            buf.append("        ").append("WHERE ");
            int i = 0;
            for (String column : tableInfo.getPrimaryKeys()) {
                String propertyName = underlineToCamel(column);
                String jdbcType = getJdbcTypeByJdbcTypeForSqlMap(
                        tableInfo.getColumnTypes().get(column));
                if (i > 0) {
                    buf.append(newLine).append("        ").append("AND ");
                }
                buf.append(caseDbSensitiveWords(column)).append(" = ").append("#{")
                        .append(propertyName).append(",jdbcType=").append(jdbcType).append("}");
                i++;
            }
            buf.append(newLine);
            buf.append("    ").append("</select>").append(newLine);
        }
        {
            // deleteByPrimaryKey
            buf.append("    ").append(
                    "<delete id=\"deleteByPrimaryKey\" parameterType=\"")
                    .append(
                            modalName)
                    .append("\">").append(newLine);
            buf.append("        ").append("DELETE FROM ").append(tableName).append(newLine);
            buf.append("        ").append("WHERE ");
            int i = 0;
            for (String column : tableInfo.getPrimaryKeys()) {
                String propertyName = underlineToCamel(column);
                String jdbcType = getJdbcTypeByJdbcTypeForSqlMap(
                        tableInfo.getColumnTypes().get(column));
                if (i > 0) {
                    buf.append(newLine).append("        ").append("AND ");
                }
                buf.append(caseDbSensitiveWords(column)).append(" = ").append("#{")
                        .append(propertyName).append(",jdbcType=").append(jdbcType).append("}");
                i++;
            }
            buf.append(newLine);
            buf.append("    ").append("</delete>").append(newLine);
        }
        String selectKeyBuf = null;
        {
            // insert
            buf.append("    ").append(
                    "<insert id=\"insert\" parameterType=\"")
                    .append(
                            modalName)
                    .append("\">").append(newLine);
            buf.append("        ").append("INSERT INTO ").append(tableName).append(" ( ");
            int i = 0;
            for (String column : columns) {
                if (tableInfo.isPrimaryKeyAutoIncrement() && tableInfo.getPrimaryKeys()
                        .contains(column)) {
                    // 主键是自增长的就不插入主键
                    continue;
                }
                String caseColumn = caseDbSensitiveWords(column);
                String propertyName = underlineToCamel(column);
                String jdbcType = getJdbcTypeByJdbcTypeForSqlMap(
                        tableInfo.getColumnTypes().get(column));
                if (i > 0) {
                    buf.append(", ");
                    if ((i + 2) % 3 == 0) {
                        buf.append(newLine).append("          ");
                    }
                }
                buf.append(caseColumn);
                i++;
            }
            buf.append(" )").append(newLine);
            buf.append("        ").append("VALUES ( ");
            i = 0;
            for (String column : columns) {
                if (tableInfo.isPrimaryKeyAutoIncrement() && tableInfo.getPrimaryKeys()
                        .contains(column)) {
                    // 主键是自增长的就不插入主键
                    continue;
                }
                String propertyName = underlineToCamel(column);
                String jdbcType = getJdbcTypeByJdbcTypeForSqlMap(
                        tableInfo.getColumnTypes().get(column));
                if (i > 0) {
                    buf.append(", ");
                    if ((i + 2) % 3 == 0) {
                        buf.append(newLine).append("          ");
                    }
                }
                buf.append("#{").append(propertyName).append(",jdbcType=").append(jdbcType).append("}");
                i++;
            }
            buf.append(" )").append(newLine);
            if (tableInfo.isPrimaryKeyAutoIncrement() && tableInfo.getPrimaryKeys().size() == 1) {
                StringBuilder selectKeyBufTmp = new StringBuilder();
                // 主键是否是自增长(且只有一个主键)
                String column = tableInfo.getPrimaryKeys().get(0);
                String propertyName = underlineToCamel(column);
                String javaType = getJavaTypeByJdbcType(tableInfo.getColumnTypes().get(column));
                if ("Integer".equals(javaType)) {
                    javaType = "int";
                } else {
                    javaType = javaType.toLowerCase();
                }
                selectKeyBufTmp.append("        ").append("<selectKey keyProperty=\"" + propertyName + "\" resultType=\"" + javaType + "\">").append(newLine);
                selectKeyBufTmp.append("            ").append("SELECT LAST_INSERT_ID() AS " + propertyName).append(newLine);
                selectKeyBufTmp.append("        ").append("</selectKey>").append(newLine);
                selectKeyBuf = selectKeyBufTmp.toString();
                buf.append(selectKeyBuf);
            }
            buf.append("    ").append("</insert>").append(newLine);
        }
        {
            // insertSelective
            buf.append("    ").append(
                    "<insert id=\"insertSelective\" parameterType=\"")
                    .append(
                            modalName)
                    .append("\">").append(newLine);
            buf.append("        ").append("INSERT INTO ").append(tableName).append(newLine);
            int i = 0;
            buf.append("        ").append("<trim prefix=\"(\" suffixOverrides=\",\" suffix=\")\">").append(newLine);
            for (String column : columns) {
                if (tableInfo.isPrimaryKeyAutoIncrement() && tableInfo.getPrimaryKeys()
                        .contains(column)) {
                    // 主键是自增长的就不插入主键
                    i++;
                    continue;
                }
                String caseColumn = caseDbSensitiveWords(column);
                String propertyName = underlineToCamel(column);
                String jdbcType = getJdbcTypeByJdbcTypeForSqlMap(
                        tableInfo.getColumnTypes().get(column));
                boolean isPk = tableInfo.getPrimaryKeys().contains(column);
                if (!isPk) {
                    buf.append("            ")
                            .append("<if test=\"" + propertyName + " != null\">")
                            .append(newLine);
                }
                buf.append("                ").append(caseColumn);
                buf.append(",");
                buf.append(newLine);
                if (!isPk) {
                    buf.append("            ").append("</if>").append(newLine);
                }
                i++;
            }
            buf.append("        ").append("</trim>").append(newLine);
            buf.append("        ").append("VALUES").append(newLine);
            i = 0;
            buf.append("        ").append("<trim prefix=\"(\" suffixOverrides=\",\" suffix=\")\">").append(newLine);
            for (String column : columns) {
                if (tableInfo.isPrimaryKeyAutoIncrement() && tableInfo.getPrimaryKeys()
                        .contains(column)) {
                    // 主键是自增长的就不插入主键
                    i++;
                    continue;
                }
                String propertyName = underlineToCamel(column);
                String jdbcType = getJdbcTypeByJdbcTypeForSqlMap(
                        tableInfo.getColumnTypes().get(column));
                boolean isPk = tableInfo.getPrimaryKeys().contains(column);
                if (!isPk) {
                    buf.append("            ")
                            .append("<if test=\"" + propertyName + " != null\">")
                            .append(newLine);
                }
                buf.append("                ").append("#{").append(propertyName).append(",jdbcType=").append(jdbcType).append("}");
                buf.append(",");
                buf.append(newLine);
                if (!isPk) {
                    buf.append("            ").append("</if>").append(newLine);
                }
                i++;
            }
            buf.append("        ").append("</trim>").append(newLine);
            if (selectKeyBuf != null && selectKeyBuf.length() > 0) {
                buf.append(selectKeyBuf);
            }
            buf.append("    ").append("</insert>").append(newLine);
        }
        {
            // insertBatch
            buf.append("    ").append(
                    "<insert id=\"insertBatch\" parameterType=\"java.util.List\">").append(newLine);
            buf.append("        ").append("INSERT INTO ").append(tableName).append(" ( ");
            int i = 0;
            for (String column : columns) {
                if (tableInfo.isPrimaryKeyAutoIncrement() && tableInfo.getPrimaryKeys()
                        .contains(column)) {
                    // 主键是自增长的就不插入主键
                    continue;
                }
                String caseColumn = caseDbSensitiveWords(column);
                String propertyName = underlineToCamel(column);
                String jdbcType = getJdbcTypeByJdbcTypeForSqlMap(
                        tableInfo.getColumnTypes().get(column));
                if (i > 0) {
                    buf.append(", ");
                    if ((i + 2) % 3 == 0) {
                        buf.append(newLine).append("          ");
                    }
                }
                buf.append(caseColumn);
                i++;
            }
            buf.append(" )").append(newLine);
            buf.append("        ").append("VALUES").append(newLine);
            buf.append("        ").append("<foreach collection=\"list\" item=\"item\" index=\"index\" separator=\",\" >").append(newLine);
            buf.append("            ").append("(");
            i = 0;
            for (String column : columns) {
                if (tableInfo.isPrimaryKeyAutoIncrement() && tableInfo.getPrimaryKeys()
                        .contains(column)) {
                    // 主键是自增长的就不插入主键
                    continue;
                }
                String propertyName = underlineToCamel(column);
                String jdbcType = getJdbcTypeByJdbcTypeForSqlMap(
                        tableInfo.getColumnTypes().get(column));
                if (i > 0) {
                    buf.append(", ");
                    if ((i + 2) % 3 == 0) {
                        buf.append(newLine).append("            ");
                    }
                } else {
                    buf.append(newLine).append("            ");
                }
                buf.append("#{item.").append(propertyName).append(",jdbcType=").append(jdbcType).append("}");
                i++;
            }
            buf.append(newLine);
            buf.append("            ").append(")").append(newLine);
            buf.append("        ").append("</foreach>").append(newLine);
            buf.append("    ").append("</insert>").append(newLine);
        }
        {
            // updateByPrimaryKeySelective
            buf.append("    ").append(
                    "<update id=\"updateByPrimaryKeySelective\" parameterType=\"")
                    .append(
                            modalName)
                    .append("\">").append(newLine);
            buf.append("        ").append("UPDATE ").append(tableName).append(newLine);
            int i = 0;
            buf.append("        ").append("<set>").append(newLine);
            for (String column : columns) {
                if (tableInfo.getPrimaryKeys().contains(column)) {
                    i++;
                    continue;
                }
                String caseColumn = caseDbSensitiveWords(column);
                String propertyName = underlineToCamel(column);
                String
                        jdbcType =
                        getJdbcTypeByJdbcTypeForSqlMap(tableInfo.getColumnTypes().get(column));
                buf.append("            ")
                        .append("<if test=\"" + propertyName + " != null\">")
                        .append(newLine);
                buf.append("                ").append(caseColumn).append(" = ").append("#{")
                        .append(propertyName).append(",jdbcType=").append(jdbcType).append(
                        "}");
                buf.append(",");
                buf.append(newLine);
                buf.append("            ").append("</if>").append(newLine);
                i++;
            }
            buf.append("        ").append("</set>").append(newLine);
            buf.append("        ").append("WHERE ");
            i = 0;
            for (String column : tableInfo.getPrimaryKeys()) {
                String propertyName = underlineToCamel(column);
                String
                        jdbcType =
                        getJdbcTypeByJdbcTypeForSqlMap(tableInfo.getColumnTypes().get(column));
                if (i > 0) {
                    buf.append(newLine).append("        ").append("AND ");
                }
                buf.append(caseDbSensitiveWords(column)).append(" = ").append("#{")
                        .append(propertyName).append(",jdbcType=").append(jdbcType).append("}");
                i++;
            }
            buf.append(newLine);
            buf.append("    ").append("</update>").append(newLine);
        }
        {
            // updateByPrimaryKey
            buf.append("    ").append(
                    "<update id=\"updateByPrimaryKey\" parameterType=\"")
                    .append(
                            modalName)
                    .append("\">").append(newLine);
            buf.append("        ").append("UPDATE ").append(tableName).append(newLine);
            int i = 0;
            for (String column : columns) {
                if (tableInfo.getPrimaryKeys().contains(column)) {
                    continue;
                }
                String caseColumn = caseDbSensitiveWords(column);
                String propertyName = underlineToCamel(column);
                String
                        jdbcType =
                        getJdbcTypeByJdbcTypeForSqlMap(tableInfo.getColumnTypes().get(column));
                if (i == 0) {
                    buf.append("        ").append("SET ");
                } else {
                    buf.append(",").append(newLine).append("            ");
                }
                buf.append(caseColumn).append(" = ").append("#{")
                        .append(propertyName).append(",jdbcType=").append(jdbcType).append("}");
                i++;
            }
            buf.append(newLine);
            buf.append("        ").append("WHERE ");
            i = 0;
            for (String column : tableInfo.getPrimaryKeys()) {
                String propertyName = underlineToCamel(column);
                String
                        jdbcType =
                        getJdbcTypeByJdbcTypeForSqlMap(tableInfo.getColumnTypes().get(column));
                if (i > 0) {
                    buf.append(newLine).append("        ").append("AND ");
                }
                buf.append(caseDbSensitiveWords(column)).append(" = ").append("#{")
                        .append(propertyName).append(",jdbcType=").append(jdbcType).append("}");
                i++;
            }
            buf.append(newLine);
            buf.append("    ").append("</update>").append(newLine);
        }
        buf.append("</mapper>");
//        System.out.println(buf);
        return buf.toString();
    }

    private String caseDbSensitiveWords(String column) {
        if (column.equalsIgnoreCase("desc") || column.equalsIgnoreCase("order")) {
            return "`" + column + "`";
        } else {
            return column;
        }
    }

    private String getJavaTypeByJdbcType(String jdbcType) {
        if (jdbcType.equalsIgnoreCase("int") || jdbcType.equalsIgnoreCase("integer") || jdbcType
                .equalsIgnoreCase("tinyint")) {
            return "Integer";
        } else if (jdbcType.equalsIgnoreCase("bigint")) {
            return "Long";
        } else if (jdbcType.equalsIgnoreCase("varchar") || jdbcType.equalsIgnoreCase("text")) {
            return "String";
        } else if (jdbcType.equalsIgnoreCase("double")) {
            return "Double";
        } else if (jdbcType.equalsIgnoreCase("float")) {
            return "Float";
        } else if (jdbcType.equalsIgnoreCase("decimal")) {
            return "BigDecimal";
        } else if (jdbcType.equalsIgnoreCase("datetime") || jdbcType
                .equalsIgnoreCase("timestamp") || jdbcType
                .equalsIgnoreCase("date")) {
            // java.util.Date
            return "Date";
        }
        throw new RuntimeException("not supported JDBC Type : \"" + jdbcType + "\"!");
    }

    private String getJdbcTypeByJdbcTypeForSqlMap(String jdbcType) {
        if (jdbcType.equalsIgnoreCase("int")) {
            return "INTEGER";
        } else if (jdbcType.equalsIgnoreCase("datetime")) {
            return "TIMESTAMP";
        } else {
            return jdbcType;
        }
    }

    private TableInfo getTableInfo(Connection conn, String schema, String tableName)
            throws Exception {
        //查要生成实体类的表
        String sql = "SELECT * FROM " + schema + "." + tableName;
        PreparedStatement pStemt = null;
        TableInfo tableInfo = null;
        try {
            pStemt = conn.prepareStatement(sql);
            ResultSetMetaData rsmd = pStemt.getMetaData();
            int size = rsmd.getColumnCount();//统计列
            List<String> columns = new ArrayList<String>(size);
            Map<String, String> columnTypes = new HashMap<String, String>(size);
            Map<String, Integer> columnSizes = new HashMap<String, Integer>(size);
            Map<String, String> columnCommentMap = new HashMap<String, String>(size);
            // 是否需要导入包java.util.*
            boolean importUtil = false;
            // 是否需要导入包java.sql.*
            boolean importSql = false;
            // 是否需要导入包java.math.*
            boolean importMath = false;
            {
                String
                        commentSql =
                        "SELECT column_name,column_comment FROM information_schema.COLUMNS WHERE table_name='"
                                + tableName + "' and table_schema = '" + schema + "'";
                PreparedStatement pStemt2 = conn.prepareStatement(commentSql);
                ResultSet rs = pStemt2.executeQuery();
                while (rs.next()) {
                    String column_name = rs.getString("column_name").toLowerCase();
                    String column_comment = rs.getString("column_comment");
                    if (column_comment != null && column_comment.length() > 0) {
                        columnCommentMap.put(column_name, column_comment);
                    }
                }
            }

            for (int i = 0; i < size; i++) {
                String columnName = rsmd.getColumnName(i + 1).toLowerCase();
                ;
                columns.add(columnName);
                String colType = rsmd.getColumnTypeName(i + 1);
                colType =
                        colType.replace("UNSIGNED", "").replace("unsigned", "").trim().toUpperCase();
                columnTypes.put(columnName, colType);
                int colSize = rsmd.getColumnDisplaySize(i + 1);
                columnSizes.put(columnName, colSize);
                if (colType.equalsIgnoreCase("datetime") || colType.equalsIgnoreCase("timestamp")) {
                    importUtil = true;
                } else if (colType.equalsIgnoreCase("decimal")) {
                    importMath = true;
                }
//                if (colTypes[i].equalsIgnoreCase("image") || colTypes[i].equalsIgnoreCase("text")) {
//                    importSql = true;
//                }
            }
            List<String> primaryKeys;
            boolean primaryKeyAutoIncrement = false;
            {
                String
                        selectPrimaryKeysSql =
                        "SELECT column_name FROM information_schema.KEY_COLUMN_USAGE WHERE constraint_name='PRIMARY' AND table_name='"
                                + tableName + "' and table_schema = '" + schema + "'";
                PreparedStatement pStemt2 = conn.prepareStatement(selectPrimaryKeysSql);
                ResultSet rs = pStemt2.executeQuery();
                primaryKeys = new ArrayList<String>();
                while (rs.next()) {
                    String primaryKey = rs.getString("column_name");
                    primaryKeys.add(primaryKey);
                }
            }
            if (primaryKeys != null && primaryKeys.size() <= 0) {
                primaryKeys = null;
            }
            if (primaryKeys != null && primaryKeys.size() == 1) {
                // 有主键，且主键只有一个，才判断主键是否是自增长
                String
                        selectPrimaryKeyAutoIncrementSql =
                        "SELECT auto_increment FROM information_schema.TABLES WHERE table_name='"
                                + tableName + "' and table_schema = '" + schema + "'";
                PreparedStatement pStemt3 = conn.prepareStatement(selectPrimaryKeyAutoIncrementSql);
                ResultSet rs = pStemt3.executeQuery();
                while (rs.next()) {
                    Long autoIncrement = rs.getLong("auto_increment");
                    Object autoIncrementObj = rs.getObject("auto_increment");
                    if (autoIncrement != null && autoIncrementObj != null) {
                        // 自增长主键
                        primaryKeyAutoIncrement = true;
                    }
                    break;
                }
            } else {
                // 否则主键一定不是自增长
                primaryKeyAutoIncrement = false;
            }
            tableInfo = new TableInfo();
            tableInfo.setTableName(tableName);
            tableInfo.setColumns(columns);
            tableInfo.setColumnTypes(columnTypes);
            tableInfo.setColumnSizes(columnSizes);
            tableInfo.setImportSql(importSql);
            tableInfo.setImportUtil(importUtil);
            tableInfo.setImportMath(importMath);
            tableInfo.setPrimaryKeyAutoIncrement(primaryKeyAutoIncrement);
            tableInfo.setPrimaryKeys(primaryKeys);
            tableInfo.setColumnCommentMap(columnCommentMap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return tableInfo;
    }


    private static String getSqlmapFileNameByTableName(String tableName) {
        return tableName + "-mapper.xml";
    }

    private static String getCustomSqlmapFileNameByTableName(String tableName) {
        return tableName + "-custom-mapper.xml";
    }

    private static String getMobalNameByTableName(String tableName) {
        return initCapAndUnderlineToCamel(tableName);
    }

    private static String getDaoNameByTableName(String tableName) {
        return getMobalNameByTableName(tableName) + "DAO";
    }

    private static String getDaoImplNameByTableName(String tableName) {
        return getMobalNameByTableName(tableName) + "DAOImpl";
    }

    /**
     * @param targetFile
     * @param content
     * @return
     */
    private static boolean writeText(File targetFile, String content,
                                     String charSet) {
        boolean flag = false;
        OutputStreamWriter out = null;
        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }
        try {
            out = new OutputStreamWriter(new FileOutputStream(targetFile),
                    charSet);
            out.write(content.toCharArray());
            flag = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    private static void moveFile(File fromFile, File toFile) {// 复制文件
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = new FileInputStream(fromFile);// 创建文件输入流
            fos = new FileOutputStream(toFile);// 文件输出流
            byte[] buffer = new byte[1024];// 字节数组
            int len = -1;
            while ((len = is.read(buffer)) != -1) {// 将文件内容写到文件中
                fos.write(buffer, 0, len);
            }
            fromFile.delete();
        } catch (Exception e) {// 捕获异常
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();// 输入流关闭
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();// 输出流关闭
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String initCap(String src) {
        if (src == null) {
            return null;
        }
        if (src.length() > 1) {
            return src.substring(0, 1).toUpperCase() + src.substring(1);
        } else {
            return src.toUpperCase();
        }
    }

    /**
     * 驼峰处理shop_name -> shopName
     */
    private static String underlineToCamel(String param) {
        if (param == null || param.equals("")) {
            return "";
        }
        Pattern p = Pattern.compile("_");
        StringBuilder builder = new StringBuilder(param);
        Matcher mc = p.matcher(param);
        int i = 0;
        Character found = null;
        while (mc.find()) {
            builder.deleteCharAt(mc.start() - i);
            found = builder.charAt(mc.start() - i);
            builder.replace(mc.start() - i, mc.start() - i + 1, found
                    .toString().toUpperCase());
            i++;
        }
        return builder.toString();
    }

    /**
     * 驼峰处理ly_share_user -> LyShareUser
     */
    private static String initCapAndUnderlineToCamel(String param) {
        String rs = underlineToCamel(param);
        return initCap(rs);
    }

    private static class TableInfo {

        private String tableName;
        private List<String> primaryKeys;
        private boolean primaryKeyAutoIncrement;
        private List<String> columns;
        private Map<String, String> columnTypes;
        private Map<String, String> columnCommentMap;
        private Map<String, Integer> columnSizes;
        // 是否需要导入包java.util.*
        private boolean importUtil = false;
        // 是否需要导入包java.sql.*
        private boolean importSql = false;
        // 是否需要导入包java.math.*
        private boolean importMath = false;

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public List<String> getPrimaryKeys() {
            return primaryKeys;
        }

        public void setPrimaryKeys(List<String> primaryKeys) {
            this.primaryKeys = primaryKeys;
        }

        public boolean isPrimaryKeyAutoIncrement() {
            return primaryKeyAutoIncrement;
        }

        public void setPrimaryKeyAutoIncrement(boolean primaryKeyAutoIncrement) {
            this.primaryKeyAutoIncrement = primaryKeyAutoIncrement;
        }

        public List<String> getColumns() {
            return columns;
        }

        public void setColumns(List<String> columns) {
            this.columns = columns;
        }

        public Map<String, Integer> getColumnSizes() {
            return columnSizes;
        }

        public void setColumnSizes(Map<String, Integer> columnSizes) {
            this.columnSizes = columnSizes;
        }

        public Map<String, String> getColumnTypes() {
            return columnTypes;
        }

        public void setColumnTypes(Map<String, String> columnTypes) {
            this.columnTypes = columnTypes;
        }

        public boolean isImportUtil() {
            return importUtil;
        }

        public void setImportUtil(boolean importUtil) {
            this.importUtil = importUtil;
        }

        public boolean isImportSql() {
            return importSql;
        }

        public void setImportSql(boolean importSql) {
            this.importSql = importSql;
        }

        public Map<String, String> getColumnCommentMap() {
            return columnCommentMap;
        }

        public void setColumnCommentMap(Map<String, String> columnCommentMap) {
            this.columnCommentMap = columnCommentMap;
        }

        public boolean isImportMath() {
            return importMath;
        }

        public void setImportMath(boolean importMath) {
            this.importMath = importMath;
        }
    }
}
