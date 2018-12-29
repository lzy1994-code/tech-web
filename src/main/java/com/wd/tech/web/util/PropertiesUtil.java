package com.wd.tech.web.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * Created by xyj on 2017/2/9.
 */
public class PropertiesUtil {

    public static void main(String  [] args)
    {
        try {
//            Properties p = getProperties("properties/message.key","utf-8");
            System.out.println(getRsaFileContent("properties/message.key","utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getRsaFileContent(String filePath,String charset) {
        Reader reader = null;
        InputStream is = null;
        try {
            // 一次读一个字符
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
            reader = new InputStreamReader(is, Charset.forName(charset));
            int tempChar;
            StringBuffer sb = new StringBuffer();
            while ((tempChar = reader.read()) != -1) {
                // 对于windows下，\r\n这两个字符在一起时，表示一个换行。
                // 但如果这两个字符分开显示时，会换两次行。
                // 因此，屏蔽掉\r，或者屏蔽\n。否则，将会多出很多空行。
                if (((char) tempChar) != '\r') {
                    sb.append((char) tempChar);
                }
            }
            reader.close();
            is.close();
            String str = sb.toString();
            str = str.replace("—–BEGIN PRIVATE KEY—–", "");
            str = str.replace("—–END PRIVATE KEY—–", "");
            str = str.replace("—–BEGIN PUBLIC KEY—–", "");
            str = str.replace("—–END PUBLIC KEY—–", "");
            str = str.replace("\n", "");
            return str;
        } catch (Exception e) {
            return null;
        }
    }

    public static Properties getProperties(String filepath,String charset) throws IOException {
        Properties pro = new Properties();
        Reader reader = null;
        InputStream is = null;
        try{
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filepath);
            reader = new InputStreamReader(is, Charset.forName(charset));
            pro.load(reader);
        }finally{
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return pro;
    }

}
