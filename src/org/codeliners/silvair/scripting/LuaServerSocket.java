package org.codeliners.silvair.scripting;

import org.codeliners.silvair.irc.LuaTextSocket;
import org.codeliners.silvair.scripting.lib.LuaSocket;
import org.codeliners.silvair.utils.IStartable;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@LuaClass("class.net.ServerSocket")
public class LuaServerSocket {

    ServerSocket server;
    boolean textmode = false;
    private Object parent = this;

    public LuaServerSocket(Varargs args) {
        try {
            server = new ServerSocket(args.checkint(1));
            new Thread() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            Socket socket = server.accept();
                            IStartable lsock;
                            try {
                                if (textmode)
                                    lsock = new LuaTextSocket(socket);
                                else
                                    lsock = new LuaSocket(socket);
                                LuaMachine.eventLib.raise("server_accepted", LuaValue.varargsOf(new LuaValue[]{
                                        LuaClassStruct.getLuaObjectOf(parent),
                                        LuaValue.valueOf(textmode),
                                        LuaClassStruct.toNewLuaObject(lsock)
                                }));
                                lsock.start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (IOException e) {
                        LuaMachine.eventLib.raise("server_closed", LuaValue.varargsOf(new LuaValue[]{
                                LuaClassStruct.getLuaObjectOf(this)
                        }));
                    }
                }
            }.start();
        } catch (IOException e) {
            throw new LuaError(e.getMessage());
        }
    }

    public Varargs setTextMode(Varargs args) {
        textmode = args.checkboolean(1);
        return LuaValue.varargsOf(new LuaValue[0]);
    }

}
