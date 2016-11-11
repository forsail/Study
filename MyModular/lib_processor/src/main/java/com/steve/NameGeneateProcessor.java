package com.steve;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * Created by yantinggeng on 2016/11/9.
 */

public class NameGeneateProcessor extends AbstractProcessor {

    public static final String CLASSNAME = "RouterList";
    public static final String PACKAGENAME = "com.lvmama.router";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        Messager messager = processingEnv.getMessager();
        for (Element element : env.getElementsAnnotatedWith(NameGenerate.class)) {
            PackageElement packageElement = (PackageElement) element.getEnclosingElement();
            String packageName = packageElement.getQualifiedName().toString();
            TypeElement classElement = (TypeElement) element;
            String className = classElement.getSimpleName().toString();
            String fullClassName = classElement.getQualifiedName().toString();
            String name = classElement.getAnnotation(NameGenerate.class).name();
            messager.printMessage(Diagnostic.Kind.NOTE, "Annotation class : packageName = " + packageName);
            messager.printMessage(Diagnostic.Kind.NOTE, "Annotation class : className = " + className);
            messager.printMessage(Diagnostic.Kind.NOTE, "Annotation class : fullClassName = " + fullClassName);
        }
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(NameGenerate.class.getCanonicalName());
        return types;
    }
}
