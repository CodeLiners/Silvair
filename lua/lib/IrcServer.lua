IrcServer = LCS.class{name = "IrcServer"};
local objresolve = setmetatable({}, {__mode = "k"});

function IrcServer:init(conf)
    local this = {};
    objresolve[self] = this;

    this.host = conf.host;
    this.port = conf.port or 6667;
    this.pass = conf.pass; -- nil allowed too
    this.defnick = conf.nick;
    this.altnick = conf.altnick or this.defnick.."_";
    this.ident = conf.ident or "silvair";
    this.realname = conf.realname or ("Silvair v."..Core.VERSION.." - http://github.com/CodeLiners/Silvair");
    this.quitmsg = conf.quitmsg or ("Silvair v."..Core.VERSION.." - http://github.com/CodeLiners/Silvair");
end

function IrcServer:connected()
    return objresolve[self].socket and true;
end

function IrcServer:connect()
    local this = objresolve[self];

    _(this.socket or error("Already connected"));
end