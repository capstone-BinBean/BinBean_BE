package binbean.binbean_BE.infra;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setStringValue(String key, String value, Long expTime) {
        ValueOperations<String, Object> stringValueOperations = redisTemplate.opsForValue();
        // set(key, value, {ttl(expiration time)}
        stringValueOperations.set(key, value, expTime, TimeUnit.MILLISECONDS);
    }

    public Optional<String> getValues(String key) {
        ValueOperations<String, Object> stringValueOperations = redisTemplate.opsForValue();
        return Optional.ofNullable(stringValueOperations.get(key))
            .map(Object::toString);
    }

    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }
}
