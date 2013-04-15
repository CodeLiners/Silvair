package org.codeliners.silvair.scripting.lib;

import org.codeliners.silvair.scripting.LuaClass;
import org.codeliners.silvair.scripting.lib.api.IInputStreamProvider;
import org.codeliners.silvair.scripting.lib.api.IOutputStreamProvider;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.io.*;

@LuaClass("class.io.File")
public class LuaFile implements IOutputStreamProvider, IInputStreamProvider{

    private final File f;

    public LuaFile(Varargs args) {
        if (args.narg() == 1)
            f = new File(args.checkjstring(1));
        else
            f = new File(args.checkjstring(1), args.checkjstring(2));
    }

    public Varargs touch(Varargs args) {
        try {
            return LuaValue.varargsOf(new LuaValue[]{LuaValue.valueOf(f.createNewFile())});
        } catch (IOException e) {
            return LuaValue.varargsOf(new LuaValue[]{LuaValue.valueOf(false)});
        }
    }

    public Varargs delete(Varargs args) {
        return LuaValue.varargsOf(new LuaValue[]{LuaValue.valueOf(f.delete())});
    }

    public Varargs listDirs(Varargs args) {
        if (f.isDirectory())
            return LuaValue.varargsOf(new LuaValue[]{LuaValue.NIL});
        LuaTable ret = new LuaTable();
        for (String s : f.list()) {
            ret.insert(ret.length() + 1, LuaValue.valueOf(s));
        }
        return LuaValue.varargsOf(new LuaValue[]{ret});
    }

    @Override
    public InputStream getInputStream() {
        try {
            return new FileInputStream(f);
        } catch (FileNotFoundException e) {
            throw new LuaError(e.getMessage());
        }
    }

    @Override
    public OutputStream getOutputStream() {
        try {
            return new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            throw new LuaError(e.getMessage());
        }
    }

    public static Varargs combine(Varargs args) {
        return LuaValue.varargsOf(new LuaValue[]{LuaValue.valueOf(new File(args.checkjstring(1), args.checkjstring(2)).getAbsolutePath())});
    }

    public Varargs getAbsName(Varargs args) {
        return LuaValue.varargsOf(new LuaValue[]{LuaValue.valueOf(f.getAbsolutePath())});
    }
}
