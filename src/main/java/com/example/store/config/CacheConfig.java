package com.example.store.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@EnableCaching
@Configuration
public class CacheConfig {
    public static final String CUSTOMERS_CACHE = "customers";
    public static final String ORDERS_CACHE = "orders";
    public static final String PRODUCTS_CACHE = "products";

    @Bean
    public RedisCacheManager cacheManager(final RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
                // DefaultTyping.EVERYTHING is required (rather than the NON_FINAL default) because
                // Java records are final classes. NON_FINAL omits @class type metadata for record
                // instances, so Jackson cannot reconstruct the concrete type on deserialisation and
                // falls back to LinkedHashMap. EVERYTHING embeds @class for every object.
                .serializeValuesWith(SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer(buildTypedObjectMapper())));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }

    private static ObjectMapper buildTypedObjectMapper() {
        // Restrict deserialisation to known packages — prevents deserialization-gadget attacks
        // that would otherwise be possible via arbitrary @class values in cached JSON.
        BasicPolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("com.example.store")
                .allowIfSubType("java.util")
                .allowIfSubType("java.lang")
                .build();
        ObjectMapper om = new ObjectMapper();
        om.activateDefaultTypingAsProperty(typeValidator, ObjectMapper.DefaultTyping.EVERYTHING, "@class");
        return om;
    }
}
