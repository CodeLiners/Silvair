package org.codeliners.silvair.scripting;

import org.codeliners.silvair.core.Silvair;
import org.codeliners.silvair.scripting.lib.EventLib;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.*;
import org.luaj.vm2.lib.jse.JseIoLib;

import java.io.*;

public class LuaMachine {

    private final Silvair silvair;
    private Globals _G;
    private BaseLib baseLib;
    public static EventLib eventLib;

    public LuaMachine(Silvair silvair) {
        this.silvair = silvair;
        loadGlobals();
    }

    public void run() {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("core.lua")));
        } catch (Exception ex) {
            try {
                reader = new BufferedReader(new FileReader(new File("lua/core.lua")));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        String s = "";
        String line;
        try {
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) s += "\n";
                firstLine = false;
                s += line;
            }
            Varargs ret = _G.get("load").call(LuaValue.valueOf(s), LuaValue.valueOf("core"));
            if (ret.isfunction(1)) {
                ret.arg1().call();
            } else {
                System.err.println("Error in core.lua: " + ret.tojstring(2));
                System.exit(-1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadGlobals() {
        _G = new Globals();
        baseLib = new BaseLib();
        _G.load(baseLib);
        _G.load(new PackageLib());
        _G.load(new Bit32Lib());
        _G.load(new TableLib());
        _G.load(new StringLib());
        _G.load(new CoroutineLib());
        _G.load(new MathLib());
        _G.load(new JseIoLib());
        _G.load(new OsLib());
        eventLib = new EventLib();
        _G.load(eventLib);
        LuaC.install();
        _G.compiler = LuaC.instance;
        setUpClasses();
    }

    private void rawset(LuaTable table, String key, LuaTable value) {
        String tree[] = key.split("\\.");
        LuaTable t = table.opttable(new LuaTable());
        for (int i = 0; i < tree.length; i++) {
            String k = tree[i];
            if (i == tree.length - 1) {
                t.rawset(k, value);
                return;
            }
            LuaValue newt = t.get(k);
            if (!newt.istable()) {
                newt = new LuaTable();
                t.rawset(k, newt);
            }
            t = (LuaTable) newt;
        }

    }

    private void setUpClasses() {
        for (Class c : ScriptingRegistry.getApis()) {
            LuaClassStruct s = new LuaClassStruct(c);
            LuaClass cl = (LuaClass) c.getAnnotation(LuaClass.class);
            if (cl == null) {
                System.err.println("Warning: Class " + c.getName() + " is not annotated with @LuaClass, skipping");
                continue;
            }

            String name = cl.value();
            if (!name.equals(""))
                rawset(_G, name, s.getClassTable());
        }
    }

}
