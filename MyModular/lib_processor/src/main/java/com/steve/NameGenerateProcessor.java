package com.steve;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * Created by yantinggeng on 2016/11/9.
 */
@AutoService(Processor.class)
public class NameGenerateProcessor extends AbstractProcessor {

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;
    private ProcessorUtil processorUtil;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        processorUtil = new ProcessorUtil(messager, filer);
    }

    /**
     * 处理注解的过程
     *
     * @param annotations 当前注解处理器所能处理的所有注解的集合
     * @param env         当前的 round 环境，可以用来获取当前round中使用某一个注解的元素
     * @return 是否产生了新代码，修改了语法树
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        // 当前我们这个处理器只处理一个NameGenerate注解，所以就只获取那些被NameGenerate注解的 element
        Set<? extends Element> annotatedWithNameGenerate = env.getElementsAnnotatedWith(NameGenerate.class);
        for (Element element : annotatedWithNameGenerate) {
            // 并且我们知道这个注解只能在 activity上使用，所以直接强转成 TypeElement。
            NameGenerateAnnotatedClass nameGenerateAnnotatedClass = new NameGenerateAnnotatedClass((TypeElement) element);
            processorUtil.addNameGenerateAnnotatedClass(nameGenerateAnnotatedClass);
        }
        processorUtil.generateCode();
        processorUtil.clear();
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(NameGenerate.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
