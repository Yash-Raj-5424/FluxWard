
local cur_key   = KEYS[1]
local prev_key  = KEYS[2]
local capacity  = tonumber(ARGV[1])
local windowMs  = tonumber(ARGV[2])
local now       = tonumber(ARGV[3])
local cur_ttl   = redis.call('PTTL', cur_key)
local elapsed   = windowMs - math.max(cur_ttl, 0)
local prev_w    = tonumber(redis.call('GET', prev_key) or '0')
local cur_w     = tonumber(redis.call('GET', cur_key)  or '0')
local weighted  = math.floor(prev_w * (windowMs - elapsed) / windowMs + cur_w)
if weighted >= capacity then
  return {0, 0, math.max(cur_ttl, 0)}
end
redis.call('INCR', cur_key)
if cur_w == 0 then redis.call('PEXPIRE', cur_key, windowMs * 2) end
local ttl = redis.call('PTTL', cur_key)
return {1, capacity - weighted - 1, ttl}