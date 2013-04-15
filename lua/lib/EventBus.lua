EventBus = LCS.class{name = "util.EventBus"};

function EventBus:init()
    self.callbacks = {};
end

function EventBus:on(name, callback)
    self.callbacks[name] = self.callbacks[name] or {};
    table.insert(self.callbacks[name], callback);
end

function EventBus:off(name, callback)
    if not self.callbacks[name] then return end
    local i = table.indexof(self.callbacks[name]);
    table.remove(self.callbacks[name], i);
    if #self.callbacks[name] <= 0 then self.callbacks[name] = nil end
end

function EventBus:post(event, ...)
    if not self.callbacks[event] then return end
    for k, v in ipairs(self.callbacks[name]) do
        v(...);
    end
end