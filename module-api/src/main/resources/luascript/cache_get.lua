
return {redis.call('mget', KEYS[1], KEYS[2]), redis.call('pttl', KEYS[1])};