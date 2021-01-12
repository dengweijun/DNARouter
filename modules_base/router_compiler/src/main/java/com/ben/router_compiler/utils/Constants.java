package com.ben.router_compiler.utils;

import com.squareup.javapoet.ClassName;

/**
 * 常量类
 *
 * @author: BD
 */
public class Constants {

    public static final ClassName ROUTER = ClassName.get(
            "com.ben.lib_arouter", "DNRouter");

    /**
     * module名称
     */
    public static final String ARGUMENTS_NAME = "moduleName";

    /**
     * 注解类
     */
    public static final String ANN_TYPE_ROUTER = "com.ben.router_annotation.Router";
    public static final String ANN_TYPE_EXTRA = "com.ben.router_annotation.Extra";

    /**
     * 支持路由配置的类型
     */
    public static final String ACTIVITY = "android.app.Activity";
    public static final String ISERVICE = "com.ben.lib_arouter.template.IService";

    /**
     * 生成java代码，需要实现的接口
     */
    public static final String IROUTER_GROUP = "com.ben.lib_arouter.template.IRouterGroup";
    public static final String IROUTER_ROOT = "com.ben.lib_arouter.template.IRouterRoot";
    public static final String IEXTRA = "com.ben.lib_arouter.template.IExtra";

    /**
     * 接口定义的方法
     */
    public static final String METHOD_LOAD_INTO = "loadInto";
    public static final String METHOD_LOAD_EXTRA = "loadExtra";

    /**
     * 定义生成java文件的包名
     */
    public static final String SEPARATOR = "$$";
    public static final String PROJECT = "DNRouter";
    public static final String NAME_OF_ROOT = PROJECT + SEPARATOR + "Root" + SEPARATOR;
    public static final String NAME_OF_GROUP = PROJECT + SEPARATOR + "Group" + SEPARATOR;
    public static final String NAME_OF_EXTRA = SEPARATOR + "Extra";

    public static final String PACKAGE_OF_GENERATE_FILE = "com.ben.lib_arouter";

    /**
     * 数据类型
     */

    private static final String LANG = "java.lang";
    public static final String BYTE = LANG + ".Byte";
    public static final String SHORT = LANG + ".Short";
    public static final String INTEGER = LANG + ".Integer";
    public static final String LONG = LANG + ".Long";
    public static final String FLOAT = LANG + ".Float";
    public static final String DOUBEL = LANG + ".Double";
    public static final String BOOLEAN = LANG + ".Boolean";
    public static final String STRING = LANG + ".String";
    public static final String ARRAY = "ARRAY";

    public static final String BYTEARRAY = "byte[]";
    public static final String SHORTARRAY = "short[]";
    public static final String BOOLEANARRAY = "boolean[]";
    public static final String CHARARRAY = "char[]";
    public static final String DOUBLEARRAY = "double[]";
    public static final String FLOATARRAY = "float[]";
    public static final String INTARRAY = "int[]";
    public static final String LONGARRAY = "long[]";
    public static final String STRINGARRAY = "java.lang.String[]";

    public static final String ARRAYLIST = "java.util.ArrayList";
    public static final String LIST = "java.util.List";


    public static final String PARCELABLE = "android.os.Parcelable";

}
