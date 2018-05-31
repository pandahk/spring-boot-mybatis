package com.snowstone.spring.boot;
import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

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
   
   @Autowired
   private JedisPoolConfig jedisPoolConfig;
   /**
    * redis哨兵配置
    * @return
    */
//   @Bean
//   public RedisSentinelConfiguration redisSentinelConfiguration(){
//       RedisSentinelConfiguration configuration = new RedisSentinelConfiguration();
//       String[] host = redisNodes.split(",");
//       for(String redisHost : host){
//           String[] item = redisHost.split(":");
//           String ip = item[0];
//           String port = item[1];
//           configuration.addSentinel(new RedisNode(ip, Integer.parseInt(port)));
//       }
//       configuration.setMaster(master);
//       return configuration;
//   }
   @Bean
   public JedisPoolConfig jedisPoolConfig() {
       JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
       jedisPoolConfig.setMaxIdle(8);
       jedisPoolConfig.setMaxTotal(12);
       jedisPoolConfig.setMinIdle(10);
       jedisPoolConfig.setMaxWaitMillis(-1);
       jedisPoolConfig.setTestOnBorrow(false);
       return jedisPoolConfig;
   }
   /**
    * 连接redis的工厂类
    *
    * @return
    */
   @Bean
   public JedisConnectionFactory jedisConnectionFactory1() {
       JedisConnectionFactory factory = new JedisConnectionFactory();
       factory.setPoolConfig(jedisPoolConfig);
       factory.setHostName(host);
       factory.setPort(port);
       factory.setTimeout(timeout);
       factory.setPassword(password);
       factory.setDatabase(database);
       return factory;
   }
   @Bean
   public JedisConnectionFactory jedisConnectionFactory2() {
       JedisConnectionFactory factory = new JedisConnectionFactory();
       factory.setPoolConfig(jedisPoolConfig);
       factory.setHostName(host2);
       factory.setPort(port2);
       factory.setTimeout(timeout2);
       factory.setPassword(password2);
       factory.setDatabase(database2);
       return factory;
   }
   /**
    * 配置RedisTemplate
    * 设置添加序列化器
    * key 使用string序列化器
    * value 使用Json序列化器
    * 还有一种简答的设置方式，改变defaultSerializer对象的实现。
    *
    * @return
    */
   @Bean
   public RedisTemplate<Object, Object> redisTemplate1() {
       //StringRedisTemplate的构造方法中默认设置了stringSerializer
       RedisTemplate<Object, Object> template = new RedisTemplate<>();
       //设置开启事务
       template.setEnableTransactionSupport(true);
       //set key serializer
       StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
       template.setKeySerializer(stringRedisSerializer);
       template.setHashKeySerializer(stringRedisSerializer);

       template.setConnectionFactory(jedisConnectionFactory1());
       template.afterPropertiesSet();
       return template;
   }
   @Bean
   public RedisTemplate<Object, Object> redisTemplate2() {
       //StringRedisTemplate的构造方法中默认设置了stringSerializer
       RedisTemplate<Object, Object> template = new RedisTemplate<>();
       //设置开启事务
       template.setEnableTransactionSupport(true);
       //set key serializer
       StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
       template.setKeySerializer(stringRedisSerializer);
       template.setHashKeySerializer(stringRedisSerializer);

       template.setConnectionFactory(jedisConnectionFactory2());
       template.afterPropertiesSet();
       return template;
   }
   /**
    * 设置RedisCacheManager
    * 使用cache注解管理redis缓存
    *
    * @return
    */
//   @Override
//   @Bean
//   public RedisCacheManager cacheManager() {
//       RedisCacheManager redisCacheManager = new RedisCacheManager(redisTemplate());
//       return redisCacheManager;
//   }

   /**
    * 自定义生成redis-key
    *
    * @return
    */
   @Override
   public KeyGenerator keyGenerator() {
       return new KeyGenerator() {
           @Override
           public Object generate(Object o, Method method, Object... objects) {
               StringBuilder sb = new StringBuilder();
               sb.append(o.getClass().getName()).append(".");
               sb.append(method.getName()).append(".");
               for (Object obj : objects) {
                   sb.append(obj.toString());
               }
               System.out.println("keyGenerator=" + sb.toString());
               return sb.toString();
           }
       };
   }
}