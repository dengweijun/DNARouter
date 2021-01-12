package com.ben.router_compiler.processor;

import com.ben.router_annotation.Router;
import com.ben.router_annotation.model.RouterMeta;
import com.ben.router_compiler.utils.Constants;
import com.ben.router_compiler.utils.Log;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * 路由注解处理器
 * 自定义的注解处理器只需继承AbstractProcessor
 *
 * @author: BD
 */

/**
 * 在这个类上添加了@AutoService注解，它的作用是用来生成
 * META-INF/services/javax.annotation.processing.Processor文件的，
 * 也就是我们在使用注解处理器的时候需要手动添加
 * META-INF/services/javax.annotation.processing.Processor，
 * 而有了@AutoService后它会自动帮我们生成。
 * AutoService是Google开发的一个库，使用时需要在
 * factory-compiler中添加依赖
 */
@AutoService(Processor.class)

/**
 * 标注需要处理的注解类
 */
@SupportedAnnotationTypes({Constants.ANN_TYPE_ROUTER})

/**
 * 指定java版本
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)

/**
 * 处理器接收的参数
 */
@SupportedOptions({Constants.ARGUMENTS_NAME})

public class RouterProcessor extends AbstractProcessor {

    private final static String TAG = "[RouterProcessor]-->";

    private Log log;
    /**
     * 节点/元素（类、方法、属性都属于节点/元素）
     */
    private Elements elementUtils;

    /**
     * 信息工具类
     */
    private Types typeUtils;

    /**
     * 文件生成器（类/资源）
     */
    private Filer filerUtils;

    /**
     * key：组名，value：类名
     */
    private Map<String, String> rootMap = new HashMap<>();

    /**
     * key：组名，value：对应组的路由信息
     */
    private Map<String, List<RouterMeta>> groupMap = new HashMap<>();

    /**
     * 参数
     */
    private String moduleName;

    /**
     * 初始化
     *
     * @param processingEnvironment
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        log = new Log(processingEnvironment.getMessager());
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        filerUtils = processingEnvironment.getFiler();
        log.i(TAG + "init()");
        // 参数是模块名称，为了防止多模块开发的时候，生成相同的 xx$$ROOT$$文件
        Map<String, String> options = processingEnvironment.getOptions();
        if (options != null && !options.isEmpty()) {
            moduleName = options.get(Constants.ARGUMENTS_NAME);
        }
        log.i(TAG + "moduleName = " + moduleName);
        if (moduleName == null || moduleName.length() == 0) {
            throw new RuntimeException("Not set Processor moduleName");
        }
    }

    /**
     * 在这里处理注解
     *
     * @param set
     * @param roundEnvironment
     * @return true 表示后续处理不再处理（已经处理）
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set != null && !set.isEmpty()) {
            log.i(TAG + "set.size = " + set.size());
            Set<? extends Element> routeElements = roundEnvironment.getElementsAnnotatedWith(Router.class);
            if (routeElements != null && !routeElements.isEmpty()) {
                log.i(TAG + "routeElements.size = " + routeElements.size());
                try {
                    parseRoutes(routeElements);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 处理Router注解
     *
     * @param routeElements
     * @throws IOException
     */
    private void parseRoutes(Set<? extends Element> routeElements) throws IOException {

        // 获取节点(Activity和IService)的描述信息
        TypeMirror activityTypeMirror = elementUtils.getTypeElement(Constants.ACTIVITY).asType();
        log.i(TAG + "activityTypeMirror = " + activityTypeMirror.toString());
        TypeMirror iServiceTypeMirror = elementUtils.getTypeElement(Constants.ISERVICE).asType();

        for (Element element : routeElements) {
            // 路由信息
            RouterMeta routerMeta;
            // 节点的描述信息
            TypeMirror typeMirror = element.asType();
            log.i(TAG + "typeMirror = " + typeMirror.toString());
            // 获取节点的注解：@Router
            Router router = element.getAnnotation(Router.class);
            if (typeUtils.isSubtype(typeMirror, activityTypeMirror)) {
                // 为Activity的注解
                routerMeta = new RouterMeta(RouterMeta.Type.ACTIVITY, router, element);
            } else if (typeUtils.isSubtype(typeMirror, iServiceTypeMirror)) {
                // 为IService的注解
                routerMeta = new RouterMeta(RouterMeta.Type.ISERVICE, router, element);
            } else {
                throw new RuntimeException("Just Support Activity/IService Router : " + element);
            }
            categories(routerMeta);
        }

        // 生成类需要实现的接口
        TypeElement iRouteGroup = elementUtils.getTypeElement(Constants.IROUTER_GROUP);
        log.i(TAG + "iRouteGroup = " + iRouteGroup.getSimpleName());
        TypeElement iRouteRoot = elementUtils.getTypeElement(Constants.IROUTER_ROOT);
        log.i(TAG + "iRouteRoot = " + iRouteRoot.getSimpleName());

        // ！！！重要方法1：生成Group类，记录<path地址，RouteMeta路由信息>
        generatedGroup(iRouteGroup);

        // ！！！重要方法2：生成Root类，记录<group组名，对应的Group类>
        generatedRoot(iRouteRoot, iRouteGroup);
    }

    /**
     * ！！！重要方法1：生成实现接口IRouterGroup的类文件
     *
     * @param iRouteGroup
     */
    private void generatedGroup(TypeElement iRouteGroup) throws IOException {
        // 参数类型 Map<String, RouteMeta>
        ParameterizedTypeName atlas = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouterMeta.class));
        // 参数 Map<String, RouteMeta> atlas
        ParameterSpec parameterSpec = ParameterSpec.builder(atlas, "atlas").build();
        // 遍历分组，每一个分组创建一个 $$Group$$ 类
        for (Map.Entry<String, List<RouterMeta>> entry : groupMap.entrySet()) {
            // 组名
            String groupName = entry.getKey();
            // 组内的路由数组
            List<RouterMeta> routerMetaList = entry.getValue();

            // 构建loadInfo方法
            MethodSpec.Builder loadInfoMethodBuilder = MethodSpec.methodBuilder(Constants.METHOD_LOAD_INTO)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(parameterSpec);

            // 遍历组中的路由
            for (RouterMeta routerMeta : routerMetaList) {
                // 组装方法体，在loadInfo方法里面添加代码，
                // 会将组内所有路由，以下面的代码形式添加代码，put添加到groupMap中：
                // atlas.put("/app/testWelcome", RouterMeta.build(RouterMeta.Type.ACTIVITY,WelcomeActivity.class, "/app/testwelcome", "app"));
                loadInfoMethodBuilder.addStatement(
                        "atlas.put($S, $T.build($T.$L, $T.class, $S, $S))",
                        routerMeta.getPath(),
                        ClassName.get(RouterMeta.class),
                        ClassName.get(RouterMeta.Type.class),
                        routerMeta.getType(),
                        ClassName.get((TypeElement) routerMeta.getElement()),
                        routerMeta.getPath().toLowerCase(),
                        routerMeta.getGroup().toLowerCase());
            }
            // 创建java文件 ($$Group&&) 组
            String groupClassName = Constants.NAME_OF_GROUP + groupName;
            JavaFile.builder(Constants.PACKAGE_OF_GENERATE_FILE,
                    TypeSpec.classBuilder(groupClassName)
                            .addSuperinterface(ClassName.get(iRouteGroup))
                            .addModifiers(Modifier.PUBLIC)
                            .addMethod(loadInfoMethodBuilder.build())
                            .build()
            ).build().writeTo(filerUtils);
            log.i(TAG + "Generated RouteGroup: " + Constants.PACKAGE_OF_GENERATE_FILE + "." +
                    groupClassName);
            // 分组名和生成对应的group类名
            rootMap.put(groupName, groupClassName);
        }
    }

    /**
     * ！！！重要方法2：生成实现接口IRouterRoot的类文件
     *
     * @param iRouteRoot
     */
    private void generatedRoot(TypeElement iRouteRoot, TypeElement iRoutGroup) throws IOException {
        // 参数类型 Map<String, Class< ? extends IRouteGroup >>
        ParameterizedTypeName routes = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(
                        ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(iRoutGroup))
                )
        );
        // 参数 Map<String, Class< ? extends IRouteGroup >> routes
        ParameterSpec parameterSpec = ParameterSpec.builder(routes, "routes").build();
        // 构建loadInfo方法
        MethodSpec.Builder loadInfoMethodBuilder = MethodSpec.methodBuilder(Constants.METHOD_LOAD_INTO)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(parameterSpec);
        // 方法体
        for (Map.Entry<String, String> entry : rootMap.entrySet()) {
            // 组名
            String groupName = entry.getKey();
            // 组类名
            String groupClassName = entry.getValue();
            loadInfoMethodBuilder.addStatement(
                    "routes.put($S, $T.class)",
                    groupName,
                    ClassName.get(Constants.PACKAGE_OF_GENERATE_FILE, groupClassName)
            );
        }
        // 生成 $$Root$$ 类文件
        String rootClassName = Constants.NAME_OF_ROOT + moduleName;
        JavaFile.builder(Constants.PACKAGE_OF_GENERATE_FILE,
                TypeSpec.classBuilder(rootClassName)
                        .addSuperinterface(ClassName.get(iRouteRoot))
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(loadInfoMethodBuilder.build())
                        .build()
        ).build().writeTo(filerUtils);
        log.i(TAG + "Generated RouteRoot: " + Constants.PACKAGE_OF_GENERATE_FILE + "." + rootClassName);
    }

    /**
     * groupMap集合
     *
     * @param routerMeta
     */
    private void categories(RouterMeta routerMeta) {
        if (!routeVerify(routerMeta)) {
            log.i("Group Info is Error:" + routerMeta.getPath());
            return;
        }
        List<RouterMeta> routerMetaList = groupMap.get(routerMeta.getGroup());
        if (routerMetaList == null) {
            groupMap.put(routerMeta.getGroup(), new ArrayList<RouterMeta>());
        }
        groupMap.get(routerMeta.getGroup()).add(routerMeta);
        log.i(TAG + "groupMap.size = " + groupMap.size());
        log.i(TAG + "routerMeta after = " + routerMeta.toString());
    }

    /**
     * 校验path是否符合规则
     *
     * @param routerMeta
     * @return
     */
    private boolean routeVerify(RouterMeta routerMeta) {
        String path = routerMeta.getPath();
        String group = routerMeta.getGroup();
        // path不能为空 && 以 / 开头
        if (path == null || path.isEmpty() || !path.startsWith("/")) {
            return false;
        }

        // 如果没有设置分组，那么需要有两个 / ，第一个 / 后面的字符串为组名
        // 例如：path = /main/MainActivity，那么组名为 main
        if (group == null || group.isEmpty()) {
            String defaultGroup = path.substring(1, path.indexOf("/", 1));
            if (defaultGroup == null || defaultGroup.isEmpty()) {
                return false;
            }
            // 将组名设置进去
            routerMeta.setGroup(defaultGroup);
            return true;
        }

        return true;
    }
}
