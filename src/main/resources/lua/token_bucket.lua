
local key        = KEYS[1]
local capacity   = tonumber(ARGV[1])
local refill     = tonumber(ARGV[2])
local now        = tonumber(ARGV[3])
local data       = redis.call('HMGET', key, 'tokens', 'last_refill')
local tokens     = tonumber(data[1]) or capacity
local last       = tonumber(data[2]) or now
local delta      = math.max(0, now - last)
tokens           = math.min(capacity, tokens + delta * refill)
local allowed    = 0
if tokens >= 1 then tokens = tokens - 1; allowed = 1 end
redis.call('HMSET', key, 'tokens', tokens, 'last_refill', now)
redis.call('EXPIRE', key, math.ceil(capacity / refill) + 1)
return {allowed, math.floor(tokens)}