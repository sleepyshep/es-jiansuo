package com.yang.service;

import java.io.*;

public class readJson {
    public static String read_Json(String jsonPath) {
//        File jsonFile = new File(jsonPath);
//        try {
//            FileReader fileReader = new FileReader(jsonFile);
//            BufferedReader reader = new BufferedReader(fileReader);
//            StringBuilder sb = new StringBuilder();
//            while (true) {
//                int ch = reader.read();
//                if (ch != -1) {
//                    sb.append((char) ch);
//                } else {
//                    break;
//                }
//            }
//            fileReader.close();
//            reader.close();
//            return sb.toString();
//        } catch (IOException e) {
//            return "";
//        }

        File file = new File(jsonPath);
        StringBuffer stringBuffer = new StringBuffer();
        try {
            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(new FileInputStream(file),"GBK"));
            //设置utf-8即可以解决中文乱码
            String line;
            while((line=bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //开始解析

        return stringBuffer.toString();
    }
}
