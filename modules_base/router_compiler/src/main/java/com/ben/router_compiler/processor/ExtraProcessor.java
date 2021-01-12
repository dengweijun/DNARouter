package com.ben.router_compiler.processor;

import com.ben.router_annotation.Extra;
import com.ben.router_compiler.utils.Constants;
import com.ben.router_compiler.utils.LoadExtraBuilder;
import com.ben.router_compiler.utils.Log;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

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
 * 参数注解处理器
 *
 * @author: BD
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(Constants.ARGUMENTS_NAME)
@SupportedAnnotationTypes(Constants.ANN_TYPE_EXTRA)
public class ExtraProcessor extends AbstractProcessor {

    private Log log;
    private Elements elementUtils;
    private Types typeUtils;
    private Filer filerUtils;

    /**
     * 记录所有需要注入的属性 key：类节点，value：需要注入的属性节点集合
     */
    private Map<TypeElement, List<Element>> parentAndChild = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        log = new Log(processingEnvironment.getMessager());
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        filerUtils = processingEnvironment.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set != null && !set.isEmpty()) {
            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Extra.class);
            if (elements != null && !elements.isEmpty()) {
                try {
                    // 记录需要生成的类与属性
                    categories(elements);
                    // 生成属性文件 xx$$Extra
                    generateAutoWired();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        return false;
    }

    /**
     * // 生成属性文件 xx$$Extra
     */
    private void generateAutoWired() throws IOException {
        TypeMirror activityTypeMirror = elementUtils.getTypeElement(Constants.ACTIVITY).asType();
        TypeMirror extraTypeMirror = elementUtils.getTypeElement(Constants.IEXTRA).asType();
        // 参数 Object target
        ParameterSpec objParamSpec = ParameterSpec.builder(TypeName.OBJECT, "target").build();
        // 遍历所有需要注入的类：属性
        if (!parentAndChild.isEmpty()) {
            for (Map.Entry<TypeElement, List<Element>> entry : parentAndChild.entrySet()) {
                TypeElement typeElement = entry.getKey();
                List<Element> elementList = entry.getValue();
                // 校验节点(类)是否为Activity或Activity的子类
                // 如果不是，则抛出异常
                if (!typeUtils.isSubtype(typeElement.getSuperclass(), activityTypeMirror)) {
                    throw new RuntimeException("Just Support Activity Field : " + typeElement);
                }
                // 封装方法体
                LoadExtraBuilder loadExtraBuilder = new LoadExtraBuilder(objParamSpec);
                loadExtraBuilder.setElementUtils(elementUtils);
                loadExtraBuilder.setTypeUtils(typeUtils);
                ClassName className = ClassName.get(typeElement);
                loadExtraBuilder.injectTarget(className);
                // 遍历属性
                for (Element element : elementList) {
                    loadExtraBuilder.buildStatement(element);
                }
                // 生成java类文件 xx$$Extra
                String extraClassName = typeElement.getSimpleName() + Constants.NAME_OF_EXTRA;
                JavaFile.builder(className.packageName(),
                        TypeSpec.classBuilder(extraClassName)
                                .addSuperinterface(ClassName.get(extraTypeMirror))
                                .addModifiers(Modifier.PUBLIC)
                                .addMethod(loadExtraBuilder.build())
                                .build()
                ).build().writeTo(filerUtils);
                log.i("Generated Extra: " + className.packageName() + "." + extraClassName);
            }
        }
    }

    /**
     * 记录需要生成的类与属性
     *
     * @param elements
     */
    private void categories(Set<? extends Element> elements) {
        for (Element element : elements) {
            // 获取父节点（类）
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            if (parentAndChild.get(typeElement) == null) {
                parentAndChild.put(typeElement, new ArrayList<Element>());
            }
            parentAndChild.get(typeElement).add(element);
        }
    }
}
