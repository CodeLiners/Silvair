package org.codeliners.silvair.core;

import org.codeliners.silvair.irc.LuaTextSocket;
import org.codeliners.silvair.scripting.LuaMachine;
import org.codeliners.silvair.scripting.LuaServerSocket;
import org.codeliners.silvair.scripting.ScriptingRegistry;
import org.codeliners.silvair.scripting.lib.LuaSocket;

public class Silvair {

    String[] args;
    LuaMachine luaMachine;

    public Silvair(String args[]) {
        this.args = args;

        ScriptingRegistry.registerClass(LuaSocket.class);
        ScriptingRegistry.registerClass(LuaTextSocket.class);
        ScriptingRegistry.registerClass(LuaServerSocket.class);

        luaMachine = new LuaMachine(this);
    }

    public void run() {
        luaMachine.run();
    }
}
