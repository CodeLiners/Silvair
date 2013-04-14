package org.codeliners.silvair.scripting;

import java.util.LinkedList;
import java.util.List;

public class ScriptingRegistry {
    private static LinkedList<Class> classes = new LinkedList<Class>();
    public static List<Class> getApis() {
        return classes;
    }
    public static void registerClass(Class clazz) {
        classes.add(clazz);
    }
}
