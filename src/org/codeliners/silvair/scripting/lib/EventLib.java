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
                try {
                    if (handlers.get(luaValue.checkjstring()) == null)
                        handlers.put(luaValue.checkjstring(), new LinkedList<LuaFunction>());
                    handlers.get(luaValue.checkjstring()).add((LuaFunction) luaValue2.checkfunction());
                    return NIL;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return NIL;
            }
        });
        luaValue.set("event", lib);
        return lib;
    }

    public void raise(String event, Varargs args) {
        if (args == null)
            throw new NullPointerException("args");
        if (handlers.get(event) == null) {
            System.out.println("Unhandled event: " + event);
            return;
        }
        for (LuaFunction f : handlers.get(event)) {
            new HandlerThread(f, args, event).run();// temporary, till i figure out how to improve thread-safety //start();
        }
    }

    private class HandlerThread extends Thread {
        private final LuaFunction f;
        private final Varargs args;
        private final String event;

        public HandlerThread(LuaFunction f, Varargs args, String event) {
            this.f = f;
            this.args = args;
            this.event = event;
        }

        public void run() {
            try {
                f.invoke(args);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
