package org.codeliners.silvair.scripting;

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
import java.util.WeakHashMap;

public class LuaClassStruct {

    private static WeakHashMap<Object, LuaObject> objectLookup = new WeakHashMap<Object, LuaObject>();
    private static HashMap<Class, LuaClassStruct> classLookup = new HashMap<Class, LuaClassStruct>();

    private Class javaClass;
    private LuaTable metatable;
    private LuaTable classTable;

    public LuaClassStruct(Class javaClass) {

        this.javaClass = javaClass;
        for (Method m : javaClass.getMethods()) {
            if (!(m.getParameterTypes().equals(Varargs.class) && m.getReturnType().equals(Varargs.class)))
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
            Object o = c.newInstance(args);
            LuaObject obj = new LuaObject(o);
            obj.setmetatable(metatable);
            objectLookup.put(o, obj);
            return obj;
        } catch (LuaError error) {
            throw error;
        } catch (Exception ex) {
            throw new LuaError("Could not create instance: " + ex.getClass().getName() + ": " + ex.getMessage());
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
                    return (Varargs) m.invoke(((LuaObject) args.arg1()).getObject(), LuaValue.varargsOf(a));
                }

            } catch (LuaError error) {
                throw error;
            } catch (Exception ex) {
                throw new LuaError("vm error: " + ex.getClass().getName() + ": " + ex.getMessage());
            }
        }
    }

    public static LuaObject toLuaObject(Object o) {
        LuaObject ret = new LuaObject(o);
        objectLookup.put(o, ret);
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
        return objectLookup.get(o);
    }
}
