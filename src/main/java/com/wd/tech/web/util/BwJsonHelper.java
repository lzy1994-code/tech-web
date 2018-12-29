package com.wd.tech.web.util;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by xyj on 2017/9/24.
 */
public class BwJsonHelper {

    /**
     * 功能描述:生成JSON返回字符串.
     * @param status	返回码
     * @param message	返回描述
     * @return
     */
    public static String returnJSON(String status,String message){
        JSONObject json = new JSONObject();
        json.put("status", status);
        json.put("message", message);
        return json.toJSONString();
    }

    /**
     * 功能描述:生成JSON返回字符串.
     * @param status	返回码
     * @param message	返回描述
     * @return
     */
    public static String returnJSON(String status,String message,String key,Object value){
        JSONObject json = new JSONObject();
        json.put("status", status);
        json.put("message", message);
        json.put(key, value);
        return json.toJSONString();
    }

    /**
     * 功能描述:生成JSON返回字符串.
     * @param status	返回码
     * @param message	返回描述
     * @param result		对象
     * @return
     */
    public static String returnJSON(String status,String message,Object result){
        return returnJSON(status,message,"result",result);
    }

    public static String returnJSON(String status,String message,String key,Object value,Object result){
        return returnJSON(status,message,key,value,"result",result);
    }

    public static String returnJSON(String status,String message,String key,Object value,String key2,Object value2){
        JSONObject json = new JSONObject();
        json.put("status", status);
        json.put("message", message);
        json.put(key, value);
        json.put(key2, value2);
        return json.toJSONString();
    }

}
