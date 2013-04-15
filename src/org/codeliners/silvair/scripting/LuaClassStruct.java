package org.codeliners.silvair.scripting;

import org.codeliners.silvair.util.WeakReference;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;

public class LuaClassStruct {

    private static HashMap<Object, LuaObject> objectLookup = new HashMap<Object, LuaObject>();
    private static HashMap<Class, LuaClassStruct> classLookup = new HashMap<Class, LuaClassStruct>();

    private Class javaClass;
    private LuaTable metatable = new LuaTable();
    private LuaTable classTable = new LuaTable();

    public LuaClassStruct(Class javaClass) {

        this.javaClass = javaClass;
        for (Method m : javaClass.getMethods()) {
            if (!(m.getParameterTypes().length == 1 && m.getParameterTypes()[0] == Varargs.class && m.getReturnType() == Varargs.class))
                continue;
            if (Modifier.isStatic(m.getModifiers())) {
                classTable.set(m.getName(), new LuaMethod(m, true));
            } else {
                metatable.set(m.getName(), new LuaMethod(m, false));
            }
        }
        metatable.set("__index", metatable);
        LuaTable classmt = new LuaTable();
        classmt.set("__call", new FunctionConstructor());
        classTable.setmetatable(classmt);
        classLookup.put(javaClass, this);
    }

    public LuaObject createNewInstance(Varargs args) {

        try {
            Constructor c = javaClass.getConstructor(Varargs.class);
            Object o = null;
            try {
                o = c.newInstance(args);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
            LuaObject obj = new LuaObject(o);
            obj.setmetatable(metatable);
            objectLookup.put(o, obj);
            return obj;
        } catch (LuaError error) {
            throw error;
        } catch (Throwable ex) {
            LuaError e = new LuaError("Could not create instance: " + ex.getClass().getName() + ": " + ex.getMessage());
            throw e;
        }

    }

    public Class getJavaClass() {
        return javaClass;
    }

    public LuaTable getClassTable() {
        return classTable;
    }

    public LuaTable getMetatable() {
        return metatable;
    }

    private class LuaMethod extends VarArgFunction {
        private final Method m;
        private final boolean isStatic;

        public LuaMethod(Method m, boolean isStatic) {
            this.m = m;
            this.isStatic = isStatic;
        }

        @Override
        public Varargs invoke(Varargs args) {
            try {
                if (isStatic) { // static function
                    return (Varargs) m.invoke(null, args);
                } else {
                    LuaValue[] a = new LuaValue[args.narg() - 1];
                    for (int i = 0; i < a.length; i++) {
                        a[i] = args.arg(i + 2);
                    }
                    if (!(args.arg1() instanceof  LuaObject))
                        throw new LuaError("Invalid object");
                    return (Varargs) m.invoke(((LuaObject) args.arg1()).getObject(), LuaValue.varargsOf(a));
                }

            } catch (LuaError error) {
                throw error;
            } catch (Exception ex) {
                System.err.println("Start of lua->java call stacktrace");
                ex.printStackTrace();
                System.err.println("End of lua->java call stacktrace");
                throw new LuaError("vm error: " + ex.getClass().getName() + ": " + ex.getMessage());
            }
        }
    }

    public static LuaObject toNewLuaObject(Object o) {
        LuaObject ret = new LuaObject(o);
        objectLookup.put(o, (ret));
        ret.setmetatable(classLookup.get(o.getClass()).metatable);

        return ret;
    }

    private class FunctionConstructor extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            LuaValue[] a = new LuaValue[args.narg() - 1];
            for (int i = 0; i < a.length; i++) {
                a[i] = args.arg(i + 2);
            }
            return LuaValue.varargsOf(new LuaValue[]{createNewInstance(LuaValue.varargsOf(a))});
        }
    }

    public static LuaObject getLuaObjectOf(Object o) {
        if (o.getClass().getAnnotation(LuaClass.class) == null)
            System.out.println("WARNING: Tried to get Lua Object if a non-luaclass object: " + o);
        return objectLookup.get(o);
    }
}
