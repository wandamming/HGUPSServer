/*
package com.hgups.express.util;

*/
/**
 * @author fanc
 * 2020/8/7 0007-20:01
 *//*

import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

*/
/**
 * @author cx
 * @date 2020/02/20 13:04
 *//*

public class CodeUtil {

    */
/**
     * 生成6位数字验证码
     *//*

    public static String generateCode(String sign, String type, long time){
        Integer code = (int)((Math.random()*9+1)*100000);
        // 存入redis，过期时间5分钟
        redisTemplate.opsForValue().set(type + sign, code.toString(), time, TimeUnit.SECONDS);
        return code.toString();
    }

    */
/**
     * 获取对应邮箱的验证码
     *//*

    public static String getCode(StringRedisTemplate redisTemplate, String sign, String type){
        return redisTemplate.opsForValue().get(type + sign);
    }

    */
/**
     * 对比验证码
     *//*

    public static boolean compareCode(StringRedisTemplate redisTemplate, String sign, String type, String code){
        String currentCode = getCode(redisTemplate, sign, type);

        if (null == currentCode){
            return false;
        }

        if (!code.equals(currentCode)){
            return false;
        }

        return true;
    }
}
*/
