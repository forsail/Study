package com.steve;


import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 * Created by yantinggeng on 2016/11/14.
 */

public class ProcessorUtil {

    public static final String CLASSNAME = "RouterList";
    public static final String PACKAGENAME = "com.lvmama.router";

    private Messager messager;
    private Filer filer;
    private ArrayList<NameGenerateAnnotatedClass> list = new ArrayList<>();

    public ProcessorUtil(Messager messager, Filer filer) {
        this.messager = messager;
        this.filer = filer;
    }


    private void error(Element e, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }

    /**
     * 检查是否合法，只能是 activity 和 fragment
     */
    private boolean isValidClass(Element item) {
        // 转换为TypeElement, 含有更多特定的方法
        TypeElement classElement = (TypeElement) item.getEnclosingElement();

        // 检查是否是一个抽象类
        if (classElement.getModifiers().contains(Modifier.ABSTRACT)) {
            error(classElement, "The class %s is abstract. You can't annotate abstract classes with @%", classElement.getQualifiedName().toString(), NameGenerate.class.getSimpleName());
            return false;
        }

        TypeMirror superclass = classElement.getSuperclass();
        if (superclass.toString().equals("android.app.Activity")) {
            error(classElement, "The class %s does not extends Activity ", classElement.getQualifiedName().toString());
            return false;
        }

        return true;
    }

    public void generateCode() {
        TypeSpec.Builder builder = TypeSpec.classBuilder(CLASSNAME).addModifiers(Modifier.PUBLIC);
        for (NameGenerateAnnotatedClass annotatedClass : list) {
            FieldSpec fieldSpec = FieldSpec.builder(String.class, annotatedClass.getClassName())
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$S", annotatedClass.getPackageName())
                    .build();
            builder.addField(fieldSpec);
        }
        TypeSpec typeSpec = builder.build();
        JavaFile.Builder javaFileBuilder = JavaFile.builder(PACKAGENAME, typeSpec);
        JavaFile javaFile = javaFileBuilder.build();
        try {
//            javaFile.writeTo(System.out); //输出到控制台
            javaFile.writeTo(filer); //输出到默认的目录
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addNameGenerateAnnotatedClass(NameGenerateAnnotatedClass item) {
        list.add(item);
    }

    public void clear() {
        list.clear();
    }
}
