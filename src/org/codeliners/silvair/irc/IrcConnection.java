package org.codeliners.silvair.irc;

import org.codeliners.silvair.scripting.LuaClass;
import org.codeliners.silvair.scripting.LuaClassStruct;
import org.codeliners.silvair.scripting.LuaMachine;
import org.luaj.vm2.*;

import java.io.*;
import java.net.Socket;

@LuaClass("class.net.Socket")
public class IrcConnection {

    private final String server;
    private final int port;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    public IrcConnection(Varargs args) {
        server = args.checkjstring(1);
        port = args.checkint(2);

        try {
            socket = new Socket(server, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            new ReadThread();
        } catch (IOException e) {
            throw new LuaError(e);
        }
    }

    private class ReadThread extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        LuaMachine.eventLib.raise("server_connection_closed", LuaValue.varargsOf(new LuaValue[]{
                                LuaClassStruct.getLuaObjectOf(this)
                        }));
                    }
                    LuaMachine.eventLib.raise("server_line_arrive", LuaValue.varargsOf(new LuaValue[]{
                            LuaClassStruct.getLuaObjectOf(this),
                            LuaValue.valueOf(line)
                    }));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Varargs sendLine(Varargs args) {
        try {
            writer.write(args.checkjstring(1) + "\n");
            writer.flush();
        } catch (IOException e) {
            throw new LuaError(e.getMessage());
        }
        return LuaValue.varargsOf(new LuaValue[0]);
    }
}
