package org.codeliners.silvair.scripting.lib;

import org.codeliners.silvair.scripting.LuaClass;
import org.codeliners.silvair.scripting.LuaObject;
import org.codeliners.silvair.scripting.lib.api.IOutputStreamProvider;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

@LuaClass("class.io.OutputStream")
public class LuaOutputStream {
    private final BufferedWriter writer;

    public LuaOutputStream(Varargs args) {
        if (args.arg1() instanceof LuaObject && ((LuaObject) args.arg1()).getObject() instanceof IOutputStreamProvider) {
            IOutputStreamProvider op = (IOutputStreamProvider) ((LuaObject) args.arg1()).getObject();
            this.writer = new BufferedWriter(new OutputStreamWriter(op.getOutputStream()));
        } else {
            throw new LuaError("Argument 1 needs to be something that has an output stream");
        }
    }

    public Varargs write(Varargs args) {
        try {
            if (args.isnumber(1))
                writer.write(args.tochar(1));
            else if (args.isstring(1))
                writer.write(args.tojstring(1));
            else
                throw new LuaError("string or number expected, got " + args.arg1().typename());
        } catch (IOException e) {
            throw new LuaError(e.getMessage());
        }
        return LuaValue.varargsOf(new LuaValue[0]);
    }
}
