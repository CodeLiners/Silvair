do 
    local libpath = class.io.File.combine(paths.LUADIR, "lib")
    local function require_impl(lib)
        local file = class.io.File(libpath, lib..".lua")
        --local r = class.io.InputStream(file)
        --local f, m = load(r:readAll(), "lib: "..lib, "t" --[[no bytecode]], _ENV)
        --if not f then error(m); end
        --f()
        libf = assert(loadfile(file:getAbsName(), "bt", _G))
        return libf()
    end
    function require(...)
        local ret = {}
        for k, v in ipairs({...}) do
            ret[k] = require_impl(v)
        end
        return unpack(ret)
    end
    table.indexof = function (t, v)
        local ret = {}
        for k, v_ in pairs(t) do
            if v == v_ then
                table.insert(ret, k)
            end
        end
        return unpack(ret)
    end

    function _() end
end

-------------------------------------------------
print("Core: ", _ENV)
require ("LCS", "EventBus", "IrcClient", "IrcServer")
local eventBus = EventBus()
Core = {
    EVENT_BUS = eventBus,
    VERSION = "0.1"
}


function rehash()

    -- todo load config

    local port = 1234

    eventBus:post("rehash")
    if mainserver then eventBus:post("preServerClose"); mainserver:close(); end
    mainserver = class.net.ServerSocket(port)
    mainserver:on("accept", newClient)
    mainserver:setTextMode(true)

end

function newClient(textMode, sock)
    
end
