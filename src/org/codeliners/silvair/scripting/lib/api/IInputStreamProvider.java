package org.codeliners.silvair.scripting.lib.api;

import org.codeliners.silvair.scripting.lib.LuaInputStream;

import java.io.InputStream;

public interface IInputStreamProvider {
    public InputStream getInputStream();
}
