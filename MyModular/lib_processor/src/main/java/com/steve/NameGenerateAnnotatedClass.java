package com.steve;

import javax.lang.model.element.TypeElement;

/**
 * 处理在类上的 注解，本库的特点决定了他只能是 Activity
 *
 * Created by yantinggeng on 2016/11/14.
 */

public class NameGenerateAnnotatedClass {

    private TypeElement annotatedClassElement;
    private String packageName;
    private String className;

    public NameGenerateAnnotatedClass(TypeElement annotatedClassElement) {
        this.annotatedClassElement = annotatedClassElement;
        className = annotatedClassElement.getSimpleName().toString();
        packageName = annotatedClassElement.getQualifiedName().toString();
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
    }

}
