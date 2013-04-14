package org.codeliners.silvair.scripting.lib.api;

import org.codeliners.silvair.scripting.lib.LuaOutputStream;

import java.io.OutputStream;

public interface IOutputStreamProvider {
    public OutputStream getOutputStream();
}
