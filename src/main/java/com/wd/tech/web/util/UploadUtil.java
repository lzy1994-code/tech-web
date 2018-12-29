package com.wd.tech.web.util;

import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.core.MultivaluedMap;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

/**
 * @program: tech-web
 * @description: 上传工具类
 * @author: Lzy
 * @create: 2018-09-13 11:16
 **/
public class UploadUtil {

    /**
     * 生成随机的5位数
     * @return
     */
    public static String getRandom(){
        Random r = new Random();
        String four="";
        int tag[]=new int[10];
        int temp = 0;
        while (four.length()!=5){
            temp = r.nextInt(10);
            if(tag[temp]==0){
                four+=temp;
                tag[temp]=1;
            }
        }
        return four;
    }

}
