
local key       = KEYS[1]
local capacity  = tonumber(ARGV[1])
local windowMs  = tonumber(ARGV[2])
local now       = tonumber(ARGV[3])
local count     = tonumber(redis.call('GET', key) or '0')
if count >= capacity then
  local ttl = redis.call('PTTL', key)
  return {0, 0, ttl}
end
redis.call('INCR', key)
if count == 0 then redis.call('PEXPIRE', key, windowMs) end
local ttl = redis.call('PTTL', key)
return {1, capacity - count - 1, ttl}