package com.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by dell-pc on 2017/11/29.
 */

public class GenDimenTool {

    private static String w640file;
    private static String w768file;
    private static String w800file;

    private static String w820file;
    private static String w853file;
    private static String w960file;

    private static String w1280file;
    private static String w1442file;
    private static String w1920file;


    private static void initFile() {
        File directory = new File("");
        w640file = directory.getAbsolutePath() + "/app/src/main/res/values-w640dp-v13/dimens.xml";
        w768file = directory.getAbsolutePath() + "/app/src/main/res/values-w768dp-v13/dimens.xml";
        w800file = directory.getAbsolutePath() + "/app/src/main/res/values-w800dp-v13/dimens.xml";

        w820file = directory.getAbsolutePath() + "/app/src/main/res/values-w820dp-v13/dimens.xml";
        w853file = directory.getAbsolutePath() + "/app/src/main/res/values-w853dp-v13/dimens.xml";
        w960file = directory.getAbsolutePath() + "/app/src/main/res/values-w960dp-v13/dimens.xml";

        w1280file = directory.getAbsolutePath() + "/app/src/main/res/values-w1280dp-v13/dimens.xml";
        w1442file = directory.getAbsolutePath() + "/app/src/main/res/values-w1442dp-v13/dimens.xml";
        w1920file = directory.getAbsolutePath() + "/app/src/main/res/values-w1920dp-v13/dimens.xml";


        File file = new File(w640file);
        if(file.exists()){
            file.delete();
        }
        file = new File(w768file);
        if(file.exists()){
            file.delete();
        }
        file = new File(w800file);
        if(file.exists()){
            file.delete();
        }



        file = new File(w820file);
        if(file.exists()){
            file.delete();
        }
        file = new File(w853file);
        if(file.exists()){
            file.delete();
        }
        file = new File(w960file);
        if(file.exists()){
            file.delete();
        }


        file = new File(w1280file);
        if(file.exists()){
            file.delete();
        }
        file = new File(w1442file);
        if(file.exists()){
            file.delete();
        }
        file = new File(w1920file);
        if(file.exists()){
            file.delete();
        }

        System.out.println("init finished");
    }

    public static void main(String[] args){

        initFile();

        gen();

    }




    /**
     * 写入方法
     *
     */
    public static void writeFile(String file, String text) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            out.println(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.close();
    }
    public static void gen() {
        //以此文件夹下的dimens.xml文件内容为初始值参照
        File directory = new File("");
        File file = new File(directory.getAbsolutePath()+"/app/src/main/res/values/dimens.xml");

        BufferedReader reader = null;
        StringBuilder w640 = new StringBuilder();
        StringBuilder w768 = new StringBuilder();
        StringBuilder w800 = new StringBuilder();
        StringBuilder w820 = new StringBuilder();
        StringBuilder w853 = new StringBuilder();
        StringBuilder w960 = new StringBuilder();
        StringBuilder w1280 = new StringBuilder();
        StringBuilder w1442 = new StringBuilder();
        StringBuilder w1920 = new StringBuilder();

        try {
            System.out.println("生成不同分辨率：");
            reader = new BufferedReader(new FileReader(file));
            String tempString;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                if (tempString.contains("</dimen>")) {
                    //tempString = tempString.replaceAll(" ", "");

                    String start = tempString.substring(0, tempString.indexOf(">") + 1);

                    String end = tempString.substring(tempString.lastIndexOf("<") - 2);
                    //截取<dimen></dimen>标签内的内容，从>右括号开始，到左括号减2，取得配置的数字
                    Double num = Double.parseDouble
                            (tempString.substring(tempString.indexOf(">") + 1,
                                    tempString.indexOf("</dimen>") - 2));

                    //根据不同的尺寸，计算新的值，拼接新的字符串，并且结尾处换行。
                    w640.append(start).append( (int) (num * 0.5)).append(end).append("\r\n");
                    w768.append(start).append((int)(num * 0.6)).append(end).append("\r\n");
                    w800.append(start).append((int)(num * 0.625)).append(end).append("\r\n");

                    w820.append(start).append((int)(num * 0.64)).append(end).append("\r\n");
                    w853.append(start).append((int)(num * 0.666)).append(end).append("\r\n");
                    w960.append(start).append((int)(num * 0.75)).append(end).append("\r\n");

                    w1280.append(start).append((int)(num * 1)).append(end).append("\r\n");
                    w1442.append(start).append((int)(num * 1.126)).append(end).append("\r\n");
                    w1920.append(start).append((int)(num * 1.5)).append(end).append("\r\n");
                } else {
                    w640.append(tempString).append("\n");
                    w768.append(tempString).append("\n");
                    w800.append(tempString).append("\n");

                    w820.append(tempString).append("\n");
                    w853.append(tempString).append("\n");
                    w960.append(tempString).append("\n");

                    w1280.append(tempString).append("\n");
                    w1442.append(tempString).append("\n");
                    w1920.append(tempString).append("\n");
                }
                line++;
            }
            reader.close();

            //将新的内容，写入到指定的文件中去
            writeFile(w640file, w640.toString());
            writeFile(w768file, w768.toString());
            writeFile(w800file, w800.toString());

            writeFile(w820file, w820.toString());
            writeFile(w853file, w853.toString());
            writeFile(w960file, w960.toString());

            writeFile(w1280file, w1280.toString());
            writeFile(w1442file, w1442.toString());
            writeFile(w1920file, w1920.toString());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
