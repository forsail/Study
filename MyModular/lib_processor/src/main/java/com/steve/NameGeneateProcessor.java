package com.steve;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * Created by yantinggeng on 2016/11/9.
 */

public class NameGeneateProcessor extends AbstractProcessor {

    public static final String CLASSNAME = "RouterList";
    public static final String PACKAGENAME = "com.lvmama.router";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        try {
            JavaFileObject classFile = processingEnv.getFiler().createClassFile(CLASSNAME);
            Writer writer = classFile.openWriter();
            PrintWriter pw = new PrintWriter(writer);
            for (TypeElement annotation : annotations) {
                pw.println("package " + PACKAGENAME + ";");
                pw.println("\npublic class " + CLASSNAME + " { ");
                for (Element element : env.getElementsAnnotatedWith(annotation)) {
                    NameGenerate nameGenerate = element.getAnnotation(NameGenerate.class);

                    String name = nameGenerate.name();
                    String value = element.toString();

                    pw.println("    public static final String " + name + " = \"" + value + "\";");
                }
                pw.println("}");
                pw.flush();
                pw.close();
            }
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
        }

        return true;
    }
}
