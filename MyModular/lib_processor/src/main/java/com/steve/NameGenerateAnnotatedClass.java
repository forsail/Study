package com.steve;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

/**
 * 处理在类上的 注解，本库的特点决定了他只能是 Activity
 *
 * Created by yantinggeng on 2016/11/14.
 */

public class NameGenerateAnnotatedClass {

    private TypeElement annotatedClassElement;
    private String fullName;
    private String packageName;
    private String className;

    public NameGenerateAnnotatedClass(TypeElement annotatedClassElement) {
        this.annotatedClassElement = annotatedClassElement;
        className = annotatedClassElement.getSimpleName().toString();
        fullName = annotatedClassElement.getQualifiedName().toString();

        Element enclosingElement = annotatedClassElement.getEnclosingElement();
        PackageElement packageElement = (PackageElement) enclosingElement;
        this.packageName = packageElement.getQualifiedName().toString();
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public TypeElement getAnnotatedClassElement() {
        return annotatedClassElement;
    }

    public String getFullName() {
        return fullName;
    }

    public String getClassName() {
        return className;
    }

}
