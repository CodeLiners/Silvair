package org.codeliners.silvair.scripting.lib;

import org.codeliners.silvair.scripting.LuaClass;
import org.codeliners.silvair.scripting.lib.api.IInputSteamProvider;
import org.codeliners.silvair.scripting.lib.api.IOutputStreamProvider;
import org.codeliners.silvair.utils.IStartable;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.Varargs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

@LuaClass("class.net.Socket")
public class LuaSocket implements IInputSteamProvider, IOutputStreamProvider, IStartable {

    private Socket socket;

    public LuaSocket(Varargs args) {
        try {
            socket = new Socket(args.checkjstring(1), args.checkint(2));
        } catch (IOException e) {
            throw new LuaError("Could not connect: " + e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public LuaSocket(Socket socket) {

    }

    @Override
    public InputStream getInputStream() {
        return null;
    }

    @Override
    public OutputStream getOutputStream() {
        return null;
    }

    @Override
    public void start() {
    }
}
