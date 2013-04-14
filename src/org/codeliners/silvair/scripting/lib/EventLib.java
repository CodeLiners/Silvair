package org.codeliners.silvair.scripting.lib;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

import java.util.HashMap;
import java.util.LinkedList;

public class EventLib extends OneArgFunction {
    HashMap<String, LinkedList<LuaFunction>> handlers = new HashMap<String, LinkedList<LuaFunction>>();
    @Override
    public LuaValue call(LuaValue luaValue) {
        LuaTable lib = new LuaTable();
        lib.set("on", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue luaValue, LuaValue luaValue2) {
                if (handlers.get(luaValue.checkjstring()) == null)
                    handlers.put(luaValue.checkjstring(), new LinkedList<LuaFunction>());
                handlers.get(luaValue.checkjstring()).add((LuaFunction) luaValue2.checkfunction());
                return NIL;
            }
        });
        luaValue.set("event", lib);
        return lib;
    }

    public void raise(String event, final Varargs args) {
        if (handlers.get(event) == null) return;
        for (final LuaFunction f : handlers.get(event)) {
            new Thread() {
                @Override
                public void run() {
                    f.invoke(args);
                }
            }.start();
        }
    }
}
