--local libpath = class.io.File.combine(paths.LUADIR, "lib")
local r = require;
function require(lib)
    r("lua.lib."..lib);
end
--[[function require(...)
    local ret = {}
    for k, v in ipairs({...}) do
        ret[k] = require_impl(v)
    end
    return unpack(ret)
end]]
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

-------------------------------------------------
print("Core: ", _ENV)
require ("LCS") require("EventBus") require("IrcClient") require "IrcServer"

for k, v in ipairs(_G) do print(k, v) end

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
