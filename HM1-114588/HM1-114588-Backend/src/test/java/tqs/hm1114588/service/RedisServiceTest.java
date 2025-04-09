package tqs.hm1114588.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class RedisServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @Mock
    private ListOperations<String, Object> listOperations;

    @Mock
    private SetOperations<String, Object> setOperations;

    @InjectMocks
    private RedisService redisService;

    private final String testKey = "testKey";
    private final String testValue = "testValue";
    private final String hashKey = "hashKey";
    private final Object hashValue = "hashValue";

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        lenient().when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        lenient().when(redisTemplate.opsForList()).thenReturn(listOperations);
        lenient().when(redisTemplate.opsForSet()).thenReturn(setOperations);
    }

    @Test
    void testSet() {
        // Act
        redisService.set(testKey, testValue);

        // Assert
        verify(valueOperations).set(testKey, testValue);
    }

    @Test
    void testSetWithExpiration() {
        // Arrange
        long timeout = 60;
        TimeUnit unit = TimeUnit.SECONDS;

        // Act
        redisService.set(testKey, testValue, timeout, unit);

        // Assert
        verify(valueOperations).set(testKey, testValue, timeout, unit);
    }

    @Test
    void testGet() {
        // Arrange
        when(valueOperations.get(testKey)).thenReturn(testValue);

        // Act
        Object result = redisService.get(testKey);

        // Assert
        assertThat(result).isEqualTo(testValue);
        verify(valueOperations).get(testKey);
    }

    @Test
    void testDelete() {
        // Arrange
        when(redisTemplate.delete(testKey)).thenReturn(true);

        // Act
        Boolean result = redisService.delete(testKey);

        // Assert
        assertThat(result).isTrue();
        verify(redisTemplate).delete(testKey);
    }

    @Test
    void testDeleteList() {
        // Arrange
        List<String> keys = Arrays.asList("key1", "key2", "key3");
        when(redisTemplate.delete(keys)).thenReturn(3L);

        // Act
        Long result = redisService.delete(keys);

        // Assert
        assertThat(result).isEqualTo(3L);
        verify(redisTemplate).delete(keys);
    }

    @Test
    void testHasKey() {
        // Arrange
        when(redisTemplate.hasKey(testKey)).thenReturn(true);

        // Act
        Boolean result = redisService.hasKey(testKey);

        // Assert
        assertThat(result).isTrue();
        verify(redisTemplate).hasKey(testKey);
    }

    @Test
    void testExpire() {
        // Arrange
        long timeout = 60;
        TimeUnit unit = TimeUnit.SECONDS;
        when(redisTemplate.expire(testKey, timeout, unit)).thenReturn(true);

        // Act
        Boolean result = redisService.expire(testKey, timeout, unit);

        // Assert
        assertThat(result).isTrue();
        verify(redisTemplate).expire(testKey, timeout, unit);
    }

    @Test
    void testHSet() {
        // Act
        redisService.hSet(testKey, hashKey, hashValue);

        // Assert
        verify(hashOperations).put(testKey, hashKey, hashValue);
    }

    @Test
    void testHSetAll() {
        // Arrange
        Map<String, Object> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");

        // Act
        redisService.hSetAll(testKey, map);

        // Assert
        verify(hashOperations).putAll(testKey, map);
    }

    @Test
    void testHGet() {
        // Arrange
        when(hashOperations.get(testKey, hashKey)).thenReturn(hashValue);

        // Act
        Object result = redisService.hGet(testKey, hashKey);

        // Assert
        assertThat(result).isEqualTo(hashValue);
        verify(hashOperations).get(testKey, hashKey);
    }

    @Test
    void testHGetAll() {
        // Arrange
        Map<Object, Object> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        when(hashOperations.entries(testKey)).thenReturn(map);

        // Act
        Map<Object, Object> result = redisService.hGetAll(testKey);

        // Assert
        assertThat(result).isEqualTo(map);
        verify(hashOperations).entries(testKey);
    }

    @Test
    void testHDelete() {
        // Arrange
        Object[] hashKeys = {"key1", "key2"};
        when(hashOperations.delete(testKey, hashKeys)).thenReturn(2L);

        // Act
        Long result = redisService.hDelete(testKey, hashKeys);

        // Assert
        assertThat(result).isEqualTo(2L);
        verify(hashOperations).delete(testKey, hashKeys);
    }

    @Test
    void testLPush() {
        // Arrange
        when(listOperations.leftPush(testKey, testValue)).thenReturn(1L);

        // Act
        Long result = redisService.lPush(testKey, testValue);

        // Assert
        assertThat(result).isEqualTo(1L);
        verify(listOperations).leftPush(testKey, testValue);
    }

    @Test
    void testRPush() {
        // Arrange
        when(listOperations.rightPush(testKey, testValue)).thenReturn(1L);

        // Act
        Long result = redisService.rPush(testKey, testValue);

        // Assert
        assertThat(result).isEqualTo(1L);
        verify(listOperations).rightPush(testKey, testValue);
    }

    @Test
    void testLPop() {
        // Arrange
        when(listOperations.leftPop(testKey)).thenReturn(testValue);

        // Act
        Object result = redisService.lPop(testKey);

        // Assert
        assertThat(result).isEqualTo(testValue);
        verify(listOperations).leftPop(testKey);
    }

    @Test
    void testRPop() {
        // Arrange
        when(listOperations.rightPop(testKey)).thenReturn(testValue);

        // Act
        Object result = redisService.rPop(testKey);

        // Assert
        assertThat(result).isEqualTo(testValue);
        verify(listOperations).rightPop(testKey);
    }

    @Test
    void testLRange() {
        // Arrange
        List<Object> list = Arrays.asList("value1", "value2", "value3");
        when(listOperations.range(testKey, 0, 2)).thenReturn(list);

        // Act
        List<Object> result = redisService.lRange(testKey, 0, 2);

        // Assert
        assertThat(result).isEqualTo(list);
        verify(listOperations).range(testKey, 0, 2);
    }

    @Test
    void testSAdd() {
        // Arrange
        Object[] values = {"value1", "value2"};
        doReturn(2L).when(setOperations).add(eq(testKey), eq(values));

        // Act
        Long result = redisService.sAdd(testKey, values);

        // Assert
        assertThat(result).isEqualTo(2L);
        verify(setOperations).add(eq(testKey), eq(values));
    }

    @Test
    void testSMembers() {
        // Arrange
        Set<Object> set = new HashSet<>(Arrays.asList("value1", "value2"));
        when(setOperations.members(testKey)).thenReturn(set);

        // Act
        Set<Object> result = redisService.sMembers(testKey);

        // Assert
        assertThat(result).isEqualTo(set);
        verify(setOperations).members(testKey);
    }

    @Test
    void testSRemove() {
        // Arrange
        Object[] values = {"value1", "value2"};
        doReturn(2L).when(setOperations).remove(eq(testKey), eq(values));

        // Act
        Long result = redisService.sRemove(testKey, values);

        // Assert
        assertThat(result).isEqualTo(2L);
        verify(setOperations).remove(eq(testKey), eq(values));
    }

    @Test
    void testClearAllCaches() {
        // Arrange
        Set<String> keys = new HashSet<>(Arrays.asList("key1", "key2", "key3"));
        when(redisTemplate.keys("*")).thenReturn(keys);

        // Act
        redisService.clearAllCaches();

        // Assert
        verify(redisTemplate).keys("*");
        verify(redisTemplate).delete(keys);
    }

    @Test
    void testClearAllCaches_NoKeys() {
        // Arrange
        when(redisTemplate.keys("*")).thenReturn(new HashSet<>());

        // Act
        redisService.clearAllCaches();

        // Assert
        verify(redisTemplate).keys("*");
        verify(redisTemplate, times(0)).delete(anyCollection());
    }
} 