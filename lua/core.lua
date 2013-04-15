-- This is just a test, implementing a simple proxy
-- config
local listenport = 8080;
local tarport = 80;
local tar = "localhost";

event.on("server_accept", function(server, textmode, sock)
    local sock2 = class.net.TextSocket(tar, tarport);
    sock.s = sock2;
    sock2.s = sock;
    print("Acceppted");
end)

event.on("socket_line", function(sock, line)
    sock.s:sendLine(line);
    print("Line: "..line);
end)

event.on("socket_close", function(sock)
    sock.s:close();
    print("Closed");
end)

local server = class.net.ServerSocket(listenport);
server:setTextMode(true);
print("Started");