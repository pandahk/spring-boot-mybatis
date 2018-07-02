package com.snowstone.spring.boot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import redis.clients.jedis.JedisPoolConfig;

@Configuration
//@EnableAutoConfiguration
//@EnableCaching //加上这个注解是的支持缓存注解
public class RedisCacheConfig extends CachingConfigurerSupport {

   @Value("${spring.a1.redis.host}")
   private String host;

   @Value("${spring.a1.redis.port}")
   private int port;

   @Value("${spring.a1.redis.timeout}")
   private int timeout;

   @Value("${spring.a1.redis.database}")
   private int database;

   @Value("${spring.a1.redis.password}")
   private String password;

//   @Value("${spring.redis.sentinel.nodes}")
//   private String redisNodes;
//
//   @Value("${spring.redis.sentinel.master}")
//   private String master;
   @Value("${spring.a2.redis.host}")
   private String host2;

   @Value("${spring.a2.redis.port}")
   private int port2;

   @Value("${spring.a2.redis.timeout}")
   private int timeout2;

   @Value("${spring.a2.redis.database}")
   private int database2;

   @Value("${spring.a2.redis.password}")
   private String password2;
   
   @Bean  
   public RedisConnectionFactory redisConnectionFactory(){  
       JedisPoolConfig poolConfig=new JedisPoolConfig();  
       poolConfig.setMaxIdle(8);  
       poolConfig.setMinIdle(0);  
       poolConfig.setTestOnBorrow(true);  
       poolConfig.setTestOnReturn(true);  
       poolConfig.setTestWhileIdle(true);  
       poolConfig.setNumTestsPerEvictionRun(10);  
       poolConfig.setTimeBetweenEvictionRunsMillis(60000);  
       JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(poolConfig);  
       jedisConnectionFactory.setHostName(host);  
       if(!password.isEmpty()){  
           jedisConnectionFactory.setPassword(password);  
       }  
       jedisConnectionFactory.setPort(port);  
       jedisConnectionFactory.setDatabase(database);  
       return jedisConnectionFactory;  
   }  
 
   @Bean  
   public RedisConnectionFactory redisConnectionFactory2(){  
       JedisPoolConfig poolConfig=new JedisPoolConfig();  
       poolConfig.setMaxIdle(8);  
       poolConfig.setMinIdle(0);  
       poolConfig.setTestOnBorrow(true);  
       poolConfig.setTestOnReturn(true);  
       poolConfig.setTestWhileIdle(true);  
       poolConfig.setNumTestsPerEvictionRun(10);  
       poolConfig.setTimeBetweenEvictionRunsMillis(60000);  
       JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(poolConfig);  
       jedisConnectionFactory.setHostName(host2);  
       if(!password.isEmpty()){  
           jedisConnectionFactory.setPassword(password);  
       }  
       jedisConnectionFactory.setPort(port);  
       jedisConnectionFactory.setDatabase(database);  
       return jedisConnectionFactory;  
   }  
 
   @Bean(name = "redisTemplate1")  
   public RedisTemplate<String, Object> redisTemplateObject() throws Exception {  
       RedisTemplate<String, Object> redisTemplateObject = new RedisTemplate<String, Object>();  
       redisTemplateObject.setConnectionFactory(redisConnectionFactory());  
       setSerializer(redisTemplateObject);  
       redisTemplateObject.afterPropertiesSet();  
       return redisTemplateObject;  
   }  
 
   @Bean(name = "redisTemplate2")  
   public RedisTemplate<String, Object> redisTemplateObject2() throws Exception {  
       RedisTemplate<String, Object> redisTemplateObject = new RedisTemplate<String, Object>();  
       redisTemplateObject.setConnectionFactory(redisConnectionFactory2());  
       setSerializer(redisTemplateObject);  
       redisTemplateObject.afterPropertiesSet();  
       return redisTemplateObject;  
   }  
 
 
 
   private void setSerializer(RedisTemplate<String, Object> template) {  
	   /*Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(  
               Object.class);  
       ObjectMapper om = new ObjectMapper();  
       om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);  
       om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);  
       jackson2JsonRedisSerializer.setObjectMapper(om);  
       template.setKeySerializer(template.getStringSerializer());  
       template.setValueSerializer(jackson2JsonRedisSerializer);  
       template.setHashValueSerializer(jackson2JsonRedisSerializer);  */
       //在使用String的数据结构的时候使用这个来更改序列化方式  
       RedisSerializer<String> stringSerializer = new StringRedisSerializer(); 
       template.setKeySerializer(stringSerializer ); 
       template.setValueSerializer(stringSerializer ); 
       template.setHashKeySerializer(stringSerializer ); 
       template.setHashValueSerializer(stringSerializer );  
 
   }  
}