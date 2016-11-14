package com.steve;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * Created by yantinggeng on 2016/11/9.
 */

public class NameGenerateProcessor extends AbstractProcessor {

    public static final String CLASSNAME = "NameGeneateList";
    public static final String PACKAGENAME = "com.lvmama.router";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        Messager messager = processingEnv.getMessager();

        try {
            JavaFileObject f = processingEnv.getFiler().createSourceFile(CLASSNAME);
            Writer w = f.openWriter();
            PrintWriter pw = new PrintWriter(w);

            pw.println("package " + PACKAGENAME + ";");
            pw.println("\npublic class " + CLASSNAME + " { ");

            for (Element element : env.getElementsAnnotatedWith(NameGenerate.class)) {
                PackageElement packageElement = (PackageElement) element.getEnclosingElement();
                String packageName = packageElement.getQualifiedName().toString();
                TypeElement classElement = (TypeElement) element;
                String className = classElement.getSimpleName().toString();
                String fullClassName = classElement.getQualifiedName().toString();
                pw.print("public String " + className + "=\"" + fullClassName + "\";");
            }

            pw.println("}");
            pw.flush();
            pw.close();

        } catch (IOException x) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, x.toString());
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
