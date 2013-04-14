package org.codeliners.silvair.scripting;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;

public class LuaObject extends LuaTable {
    private Object obj;
    public LuaObject(Object o) {
        this.obj = o;
    }
    public Object getObject() {
        return obj;
    }
}
