-- This is just a test, implementing a simple proxy
-- config
local listenport = 8080;
local tarport = 80;
local tar = "localhost";

local connections = {}

print("Test");

event.on("server_accepted", function(server, textmode, sock)
    local sock2 = class.net.TextSocket(tar, tarport);
    connections[sock] = sock2;
    connections[sock2] = sock;
    sock2:startListening();
    print("Accepted");
end)

event.on("socket_line", function(sock, line)
    connections[sock]:sendLine(line);
    print("Line: "..line);
end)

event.on("socket_close", function(sock)
    connections[sock]:close();
    print("Closed");
end)

local server = class.net.ServerSocket(listenport);
server:setTextMode(true);
print("Started");--]]