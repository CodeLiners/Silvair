package org.codeliners.silvair.scripting;

import org.codeliners.silvair.scripting.lib.EventLib;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public abstract class EventClass {

    private EventLib eventLib = new EventLib();

    public Varargs on(Varargs args) {
        eventLib.on(args.checkjstring(1), (LuaFunction) args.checkfunction(2));
        return LuaValue.varargsOf(new LuaValue[0]);
    }

    protected void raiseEvent(String name, Varargs args) {
        eventLib.raise(name, args);
    }
}
