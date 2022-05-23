package com.hgups.express.util;

import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.Sha1Hash;

public class MD5Utils {

	public static void main(String[] args) {
		
		//MD5加密
		//d367fc655d83ba12fc068b0b6eeb53b7
		String password = "111111";
		String username = "3mDjn+P049GzpryEt1SXHQ==";
		/*//*Md5Hash hash1 = new Md5Hash(password);
		System.out.println("使用MD5加密后的结果："+hash1.toString());
		
		//MD5加密、加盐
		Md5Hash hash2 = new Md5Hash(password,username);
		System.out.println("使用MD5加密并加盐后的结果："+hash2.toString());*/
		
		//MD5加密、加盐、散列
		Md5Hash hash5 = new Md5Hash(password,username,2);
		System.out.println("使用MD5加密加盐并散列1024次后的结果："+hash5.toString());

	
	}
	/**
	 * 
	 * 对密码加密MD5
	 * 
	 * @param source 要加密的明文
	 * @param salt  盐
	 * @param hashIterations 散列次数
	 * @return
	 */
	public static String md5(String source,Object salt,Integer hashIterations){
		return new Md5Hash(source, salt, hashIterations).toString();
	}
	/**
	 * 
	 * 对密码加密sha1
	 * 
	 * @param source 要加密的明文
	 * @param salt  盐
	 * @param hashIterations 散列次数
	 * @return
	 */
	public static String sha1(String source,Object salt,Integer hashIterations){
		return new Sha1Hash(source, salt, hashIterations).toString();
	}
	
}
