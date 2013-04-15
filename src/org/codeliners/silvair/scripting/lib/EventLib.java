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
        lib.set("on", new FunctionOn());
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

    public void on(String event, LuaFunction callback) {
        try {
            if (handlers.get(event) == null)
                handlers.put(event, new LinkedList<LuaFunction>());
            handlers.get(event).add(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class FunctionOn extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue luaValue, LuaValue luaValue2) {
            on(luaValue.checkjstring(1), (LuaFunction) luaValue2.checkfunction(2));
            return NIL;
        }
    }
}
