package com.express.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
/**
 * redis的操作
 * @author jaciyu
 *
 */
@Component
public class RedisClientTemplate {
	@Resource
	private RedisTemplate<String, Object> redisTemplate;

	public void delete(String key){
		if(this.hasKey(key)){
			this.delete(key);
		}
	}
	public void deletePattern(String pattern){
		Set<String> keys = this.getKeys(pattern);
		if(keys.size()>0){
			redisTemplate.delete(keys);
		}
	}
	public Set<String> getKeys(String pattern){
		return redisTemplate.keys(pattern);
	}
	public boolean hasKey(String key){
		return this.redisTemplate.hasKey(key);
	}
	public long delete(final String... keys){
		return redisTemplate.execute(new RedisCallback<Long>() {
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                long result = 0;
                for (int i = 0; i < keys.length; i++) {
                    result = connection.del(keys[i].getBytes());
                }
                return result;
            }
        });
		
	}
    public boolean put(final byte[] key,final byte[] value) {  
           return redisTemplate.execute(new RedisCallback<Boolean>() {   
 	            public Boolean doInRedis(RedisConnection connection)throws DataAccessException {   
 	            	return connection.setNX(key,value);  
 	            }   
 	        });  
     }
    public boolean put(String key,String value){
    	return this.put(key.getBytes(), value.getBytes());
    }
    public boolean put(String key,Object object){
    	return this.put(key.getBytes(),object2Byte(object));
    }
    /*
     * 对象要继承Serializable接口
     */
	public  Object byte2Object(byte[] bytes) {
		try {
			ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
			ObjectInputStream oi = new ObjectInputStream(bi);
			Object obj = oi.readObject();
			bi.close();
			oi.close();
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
    /*
     * 对象要继承Serializable接口
     */	
	public  byte[] object2Byte(Object obj) {
		try {
			// object to bytearray
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream oo = new ObjectOutputStream(bo);
			oo.writeObject(obj);
			byte[] bytes = bo.toByteArray();
			bo.close();
			oo.close();
			return bytes;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
    
}
