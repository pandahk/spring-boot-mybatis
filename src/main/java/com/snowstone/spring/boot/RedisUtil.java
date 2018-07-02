package com.snowstone.spring.boot;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

/**
 * redicache 工具类
 * 
 */
@SuppressWarnings("unchecked")
@Component
public class RedisUtil {
	//主
	@Autowired  
    @Resource(name = "redisTemplate1")  
    private RedisTemplate redisTemplate1;  
    //从
    @Autowired  
    @Resource(name = "redisTemplate2")  
    private RedisTemplate redisTemplate2;  
	/**
	 * 批量删除对应的value
	 * 
	 * @param keys
	 */
	public void remove(final String... keys) {
		for (String key : keys) {
			remove(key);
		}
	}

	/**
	 * 批量删除key
	 * 
	 * @param pattern
	 */
	public void removePattern(final String pattern) {
		Set<Serializable> keys = redisTemplate1.keys(pattern);
		if (keys.size() > 0)
			redisTemplate1.delete(keys);
	}

	/**
	 * 删除对应的value
	 * 
	 * @param key
	 */
	public void remove(final String key) {
		if (exists(key)) {
			redisTemplate1.delete(key);
		}
	}

	/**
	 * 判断缓存中是否有对应的key
	 * 
	 * @param key
	 * @return
	 */
	public boolean exists(final String key) {
		return redisTemplate2.hasKey(key);
	}
	/** 
     * 指定缓存失效时间 
     * @param key 键 
     * @param time 时间(秒) 
     * @return 
     */  
    public boolean expire(String key,long time){  
        try {  
            if(time>0){  
                redisTemplate1.expire(key, time, TimeUnit.SECONDS);  
            }  
            return true;  
        } catch (Exception e) {  
            e.printStackTrace();  
            return false;  
        }  
    }  
	/**
	 * 读取缓存
	 * 
	 * @param key
	 * @return
	 */
	public String get(final String key) {
		Object result = null;
		redisTemplate2.setValueSerializer(new StringRedisSerializer());
		ValueOperations<Serializable, Object> operations = redisTemplate2.opsForValue();
		result = operations.get(key);
		if (result == null) {
			return null;
		}
		return result.toString();
	}

	/**
	 * 写入缓存
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean set(final String key, Object value) {
		boolean result = false;
		try {
			ValueOperations<Serializable, Object> operations = redisTemplate1.opsForValue();
			operations.set(key, value);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 写入缓存
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean set(final String key, Object value, Long expireTime) {
		boolean result = false;
		try {
			ValueOperations<Serializable, Object> operations = redisTemplate1.opsForValue();
			operations.set(key, value);
			redisTemplate1.expire(key, expireTime, TimeUnit.SECONDS);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public boolean hmset(String key, Map<String, String> value) {
		boolean result = false;
		try {
			redisTemplate1.opsForHash().putAll(key, value);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public Map<String, String> hmget(String key) {
		Map<String, String> result = null;
		try {
			result = redisTemplate2.opsForHash().entries(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/** 
     * HashGet 
     * @param key 键 不能为null 
     * @param item 项 不能为null 
     * @return 值 
     */  
    public Object hget(String key,String item){  
        return redisTemplate1.opsForHash().get(key, item);  
    }  
	//-------------------hash end
	
	/** 
     * 递增 
     * @param key 键 
     * @param by 要增加几(大于0) 
     * @return 
     */  
    public long incr(String key, long delta){    
        if(delta<0){  
            throw new RuntimeException("递增因子必须大于0");  
        }  
        return redisTemplate1.opsForValue().increment(key, delta);  
    }  
      
    /** 
     * 递减 
     * @param key 键 
     * @param by 要减少几(小于0) 
     * @return 
     */  
    public long decr(String key, long delta){    
        if(delta<0){  
            throw new RuntimeException("递减因子必须大于0");  
        }  
        return redisTemplate1.opsForValue().increment(key, -delta);    
    }    
	
	
	
    /** 
     * 删除hash表中的值 
     * @param key 键 不能为null 
     * @param item 项 可以使多个 不能为null 
     */  
    public void hdel(String key, Object... item){    
        redisTemplate1.opsForHash().delete(key,item);  
    }   
      
    /** 
     * 判断hash表中是否有该项的值 
     * @param key 键 不能为null 
     * @param item 项 不能为null 
     * @return true 存在 false不存在 
     */  
    public boolean hHasKey(String key, String item){  
        return redisTemplate2.opsForHash().hasKey(key, item);  
    }   
      
    /** 
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回 
     * @param key 键 
     * @param item 项 
     * @param by 要增加几(大于0) 
     * @return 
     */  
    public double hincr(String key, String item,double by){    
        return redisTemplate1.opsForHash().increment(key, item, by);  
    }  
      
    /** 
     * hash递减 
     * @param key 键 
     * @param item 项 
     * @param by 要减少记(小于0) 
     * @return 
     */  
    public double hdecr(String key, String item,double by){    
        return redisTemplate1.opsForHash().increment(key, item,-by);    
    }    
      
    //============================set=============================  
    /** 
     * 根据key获取Set中的所有值 
     * @param key 键 
     * @return 
     */  
    public Set<Object> sGet(String key){  
        try {  
            return redisTemplate2.opsForSet().members(key);  
        } catch (Exception e) {  
            e.printStackTrace();  
            return null;  
        }  
    }  
      
    /** 
     * 根据value从一个set中查询,是否存在 
     * @param key 键 
     * @param value 值 
     * @return true 存在 false不存在 
     */  
    public boolean sHasKey(String key,Object value){  
        try {  
            return redisTemplate2.opsForSet().isMember(key, value);  
        } catch (Exception e) {  
            e.printStackTrace();  
            return false;  
        }  
    }  
      
    /** 
     * 将数据放入set缓存 
     * @param key 键 
     * @param values 值 可以是多个 
     * @return 成功个数 
     */  
    public long sSet(String key, Object...values) {  
        try {  
            return redisTemplate1.opsForSet().add(key, values);  
        } catch (Exception e) {  
            e.printStackTrace();  
            return 0;  
        }  
    }  
      
    /** 
     * 将set数据放入缓存 
     * @param key 键 
     * @param time 时间(秒) 
     * @param values 值 可以是多个 
     * @return 成功个数 
     */  
    public long sSetAndTime(String key,long time,Object...values) {  
        try {  
            Long count = redisTemplate1.opsForSet().add(key, values);  
            if(time>0) expire(key, time);  
            return count;  
        } catch (Exception e) {  
            e.printStackTrace();  
            return 0;  
        }  
    }  
      
    /** 
     * 获取set缓存的长度 
     * @param key 键 
     * @return 
     */  
    public long sGetSetSize(String key){  
        try {  
            return redisTemplate2.opsForSet().size(key);  
        } catch (Exception e) {  
            e.printStackTrace();  
            return 0;  
        }  
    }  
      
    /** 
     * 移除值为value的 
     * @param key 键 
     * @param values 值 可以是多个 
     * @return 移除的个数 
     */  
    public long setRemove(String key, Object ...values) {  
        try {  
            Long count = redisTemplate1.opsForSet().remove(key, values);  
            return count;  
        } catch (Exception e) {  
            e.printStackTrace();  
            return 0;  
        }  
    }  
    //===============================list=================================  
      
    /** 
     * 获取list缓存的内容 
     * @param key 键 
     * @param start 开始 
     * @param end 结束  0 到 -1代表所有值 
     * @return 
     */  
    public List<Object> lGet(String key,long start, long end){  
        try {  
            return redisTemplate2.opsForList().range(key, start, end);  
        } catch (Exception e) {  
            e.printStackTrace();  
            return null;  
        }  
    }  
      
    /** 
     * 获取list缓存的长度 
     * @param key 键 
     * @return 
     */  
    public long lPushGetListSize(String key){  
        try {  
            return redisTemplate2.opsForList().size(key);  
        } catch (Exception e) {  
            e.printStackTrace();  
            return 0;  
        }  
    }  
      
    /** 
     * 通过索引 获取list中的值 
     * @param key 键 
     * @param index 索引  index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推 
     * @return 
     */  
    public Object lPushGetIndex(String key,long index){  
        try {  
            return redisTemplate2.opsForList().index(key, index);  
        } catch (Exception e) {  
            e.printStackTrace();  
            return null;  
        }  
    }  
      
    /** 
     * 将list放入缓存 
     * @param key 键 
     * @param value 值 
     * @param time 时间(秒) 
     * @return 
     */  
    public boolean lPush(String key, Object value) {  
        try {  
            redisTemplate1.opsForList().rightPush(key, value);  
            return true;  
        } catch (Exception e) {  
            e.printStackTrace();  
            return false;  
        }  
    }  
      
    /** 
     * 将list放入缓存 
     * @param key 键 
     * @param value 值 
     * @param time 时间(秒) 
     * @return 
     */  
    public boolean lPush(String key, Object value, long time) {  
        try {  
            redisTemplate1.opsForList().rightPush(key, value);  
            if (time > 0) expire(key, time);  
            return true;  
        } catch (Exception e) {  
            e.printStackTrace();  
            return false;  
        }  
    }  
      
    /** 
     * 将list放入缓存 
     * @param key 键 
     * @param value 值 
     * @param time 时间(秒) 
     * @return 
     */  
    public boolean lPush(String key, List<Object> value) {  
        try {  
            redisTemplate1.opsForList().rightPushAll(key, value);  
            return true;  
        } catch (Exception e) {  
            e.printStackTrace();  
            return false;  
        }  
    }  
      
    /** 
     * 将list放入缓存 
     * @param key 键 
     * @param value 值 
     * @param time 时间(秒) 
     * @return 
     */  
    public boolean lPush(String key, List<Object> value, long time) {  
        try {  
            redisTemplate1.opsForList().rightPushAll(key, value);  
            if (time > 0) expire(key, time);  
            return true;  
        } catch (Exception e) {  
            e.printStackTrace();  
            return false;  
        }  
    }  
      
    /** 
     * 根据索引修改list中的某条数据 
     * @param key 键 
     * @param index 索引 
     * @param value 值 
     * @return 
     */  
    public boolean lPushUpdateIndex(String key, long index,Object value) {  
        try {  
            redisTemplate1.opsForList().set(key, index, value);  
            return true;  
        } catch (Exception e) {  
            e.printStackTrace();  
            return false;  
        }  
    }   
      
    /** 
     * 移除N个值为value  
     * @param key 键 
     * @param count 移除多少个 
     * @param value 值 
     * @return 移除的个数 
     */  
    public long lPushRemove(String key,long count,Object value) {  
        try {  
            Long remove = redisTemplate1.opsForList().remove(key, count, value);  
            return remove;  
        } catch (Exception e) {  
            e.printStackTrace();  
            return 0;  
        }  
    }  
	/**
	 * 增加数据或者覆盖排行数据
	 * @param zsetName 队列名称
	 * @param key 如用户userid
	 * @param score 分数入算力
	 * @return
	 */
	public Boolean zadd(String zsetName, String key, Double score) {
		Boolean result = null;
		try {
			result = redisTemplate1.opsForZSet().add(zsetName, key, score);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 判断zset中的key是否存在
	 * @param zsetName
	 * @param key
	 * @return
	 */
	public Long zexists(String zsetName, String key) {
		Long result = null;
		try {
			result = redisTemplate1.opsForZSet().rank(zsetName, key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 查询排行前几的列表
	 * @param zsetName 队列名称
	 * @param left 排名范围开始
	 * @param right 排名范围结束
	 * @return
	 */
	public Set zrange(String zsetName, Integer left, Integer right) {
		Set result = null;
		try {
			result = redisTemplate2.opsForZSet().reverseRangeWithScores(zsetName, left, right);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 查出当前用户排行
	 * @param zsetName 队列名称
	 * @param key 如用户userId
	 * @return
	 */
	public Long zrevrank (String zsetName, String key) {
		Long result = null;
		try {
			result = redisTemplate2.opsForZSet().reverseRank(zsetName, key);
			//由于是从零开始的所以加1
			result+=1L;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
