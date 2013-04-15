package org.codeliners.silvair.scripting.lib;

import org.codeliners.silvair.scripting.LuaClass;
import org.codeliners.silvair.scripting.lib.api.IInputStreamProvider;
import org.codeliners.silvair.scripting.lib.api.IOutputStreamProvider;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@LuaClass("class.io.File")
public class LuaFile extends File implements IOutputStreamProvider, IInputStreamProvider{


    public LuaFile(Varargs args) {
        super(args.checkjstring(1));
    }

    public Varargs touch(Varargs args) {
        try {
            return LuaValue.varargsOf(new LuaValue[]{LuaValue.valueOf(createNewFile())});
        } catch (IOException e) {
            return LuaValue.varargsOf(new LuaValue[]{LuaValue.valueOf(false)});
        }
    }

    public Varargs delete(Varargs args) {
        return LuaValue.varargsOf(new LuaValue[]{LuaValue.valueOf(delete())});
    }

    public Varargs listDirs(Varargs args) {
        if (isDirectory())
            return LuaValue.varargsOf(new LuaValue[]{LuaValue.NIL});
        LuaTable ret = new LuaTable();
        for (String s : list()) {
            ret.insert(ret.length() + 1, LuaValue.valueOf(s));
        }
        return LuaValue.varargsOf(new LuaValue[]{ret});
    }

    @Override
    public InputStream getInputStream() {
        return null;
    }

    @Override
    public OutputStream getOutputStream() {
        return null;
    }
}
