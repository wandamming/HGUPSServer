package com.hgups.express.util;

/**
 * @author fanc
 * 2020/6/14 0014-15:12
 */
public class NumberGenUtil {

    public static long getRandomNumber(){

        long timeSeed = System.nanoTime(); // to get the current date time value

        double randSeed = Math.random() * 1000; // random number generation

        long midSeed = (long) (timeSeed * randSeed); // mixing up the time and

        String s = midSeed + "";
        String subStr = s.substring(0, 9);

        long finalSeed = Integer.parseInt(subStr); // integer value

        return finalSeed;
    }
}
