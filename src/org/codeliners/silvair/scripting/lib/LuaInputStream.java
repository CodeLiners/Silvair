package org.codeliners.silvair.scripting.lib;

import org.codeliners.silvair.scripting.LuaClass;
import org.codeliners.silvair.scripting.LuaObject;
import org.codeliners.silvair.scripting.lib.api.IInputStreamProvider;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@LuaClass("class.io.InputStream")
public class LuaInputStream {
    private final BufferedReader reader;

    public LuaInputStream(Varargs args) {
        if (args.arg1() instanceof LuaObject && ((LuaObject) args.arg1()).getObject() instanceof IInputStreamProvider) {
            IInputStreamProvider ip = (IInputStreamProvider) ((LuaObject) args.arg1()).getObject();
            this.reader = new BufferedReader(new InputStreamReader(ip.getInputStream()));
        } else {
            throw new LuaError("Argument 1 needs to be something that has an input stream");
        }
    }

    public Varargs readLine(Varargs args) {
        try {
            return LuaValue.varargsOf(new LuaValue[]{LuaValue.valueOf(reader.readLine())});
        } catch (IOException e) {
            throw new LuaError(e.getMessage());
        }
    }

    public Varargs read(Varargs args) {
        try {
            return LuaValue.varargsOf(new LuaValue[]{LuaValue.valueOf(reader.read())});
        } catch (IOException e) {
            throw new LuaError(e.getMessage());
        }
    }

    public Varargs readAll(Varargs args) {
        try {
            String line, s = "";
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (!firstLine) s += "\n";
                firstLine = false;
                s += line;
            }
            return LuaValue.varargsOf(new LuaValue[]{LuaValue.valueOf(s)});
        } catch (IOException e) {
            throw new LuaError(e.getMessage());
        }
    }
}
