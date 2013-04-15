do 
    local libpath = class.io.File.combine(paths.LUADIR, "lib");
    local cache = setmetatable({}, {__mode = "w"});
    local function require_impl(lib)
        if cache[lib] then return cache[lib]; end
        local file = class.io.File(libpath, lib..".lua");
        local r = class.io.InputStream(file);
        local env = setmetatable({}, {__index = _G});
        local f, m = load(r:readAll(), "lib: "..lib, "t" --[[no bytecode]], env);
        if not f then error(m); end
        cache[lib] = env;
        f();
        return env;
    end
    function require(...)
        local ret = {};
        for k, v in ipairs({...}) do
            ret[k] = require_impl(v);
        end
        return unpack(ret);
    end
    table.indexof = function (t, v)
        local ret = {};
        for k, v_ in pairs(t) do
            if v == v_ then
                table.insert(ret, k);
            end
        end
        return unpack(ret);
    end

    function _() end
end

-------------------------------------------------
local LCS, EventBus, IrcClient, IrcServer = require ("LCS", "EventBus", "IrcClient", "IrcServer");
local eventBus = EventBus();
Core = {
    EVENT_BUS = eventBus,
    VERSION = "0.1"
};


function rehash()

    -- todo load config

    local port = 1234;

    eventBus:post("rehash");
    if mainserver then eventBus:post("preServerClose"); mainserver:close(); end
    mainserver = class.net.ServerSocket(port);
    mainserver:on("accept", newClient);
    mainserver:setTextMode(true);

end

function newClient(textMode, sock)
    
end