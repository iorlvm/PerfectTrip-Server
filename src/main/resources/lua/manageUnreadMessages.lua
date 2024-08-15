-- 獲取操作者id, 以及現在時間
local mappingUserId = ARGV[1]
local now = ARGV[2]

-- 獲取所有的哈希key value
local hashFields = redis.call('HGETALL', KEYS[1])

-- 每兩個元素一組, i 是hash key，i+1 是value
for i = 1, #hashFields, 2 do
    local field = hashFields[i]
    local jsonString = hashFields[i + 1]

    -- 解析 JSON 字串
    local data = cjson.decode(jsonString)

    -- 根據hash key執行對應的操作
    if mappingUserId == field then
        data["unreadMessages"] = 0
        data["lastReadingAt"] = now
    else
        if type(data["unreadMessages"]) == "number" then
            data["unreadMessages"] = data["unreadMessages"] + 1
        end
    end

    -- 將資料轉回 JSON 字串
    local newJsonString = cjson.encode(data)

    -- 回存進 redis 中
    redis.call('HSET', KEYS[1], field, newJsonString)
end

return 0
