package com.hgups.express.util.utiltest;

import com.hgups.express.domain.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fanc
 * 2020/7/22 0022-19:33
 */
public class ReadWriterUtil {

    private Map<String,String> checkCodeMap = new HashMap<>();

    private User nn;

    public static void main(String[] args) {
        //readFile();
        /*List<String> list= Arrays.asList("a","b","c","d");

        List collect =list.stream().map(String::toUpperCase).collect(Collectors.toList());
        System.out.println(collect); //[A, B, C, D]

        //数组所有元素，按某种规律计算：
        List<Integer> num = Arrays.asList(1,2,3,4,5);
        List collect1 = num.stream().map(n -> n * 2).collect(Collectors.toList());
        System.out.println(collect1); //[2, 4, 6, 8, 10]*/

    }


    public static void readFile(){
        String pathName = "C:/Users/Administrator/Desktop/tt.txt";
//      Java7的try-with-resources可以优雅关闭文件，异常时自动关闭文件;
        try(FileReader reader = new FileReader(pathName);
            BufferedReader br = new BufferedReader(reader)){
            String line;
//           按行读取数据
            FileWriter fw = new FileWriter("C:/Users/Administrator/Desktop/ttoo.txt", true);
            while ((line = br.readLine()) != null){
                System.out.println(line);
                fw.write(line+",0);\r\n");
                fw.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
