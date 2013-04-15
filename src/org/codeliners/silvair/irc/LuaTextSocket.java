package org.codeliners.silvair.irc;

import org.codeliners.silvair.scripting.*;
import org.codeliners.silvair.scripting.lib.LuaSocket;
import org.codeliners.silvair.utils.IStartable;
import org.luaj.vm2.*;

import java.io.*;
import java.net.Socket;

@LuaClass("class.net.TextSocket")
public class LuaTextSocket extends EventClass implements IStartable {

    private final String server;
    private final int port;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private LuaTextSocket parent = this;
    private boolean started = false;

    public LuaTextSocket(Socket s) throws IOException {
        socket = s;
        server = null;
        port = -1;
        setup();
    }

    private void setup() throws IOException {
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public LuaTextSocket(Varargs args) {
        server = args.checkjstring(1);
        port = args.checkint(2);

        try {
            socket = new Socket(server, port);
            setup();
        } catch (IOException e) {
            throw new LuaError(e);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void start() {
        new ReadThread().start();
        started = true;
    }

    public Varargs startListening(Varargs args) {
        if (!started)
            start();
        return LuaValue.varargsOf(new LuaValue[0]);
    }

    private class ReadThread extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        raiseEvent("closed", LuaValue.varargsOf(new LuaValue[]{}));
                        break;
                    }
                    raiseEvent("line", LuaValue.varargsOf(new LuaValue[]{
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return LuaValue.varargsOf(new LuaValue[0]);
    }

    public Varargs close(Varargs args) {
        try {
            socket.close();
        } catch (IOException e) {
            throw new LuaError(e.getMessage());
        }
        return LuaValue.varargsOf(new LuaValue[0]);
    }
}
