package com.wd.tech.web.util;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xyj on 2017/9/27.
 */
public class WebUtil {

    /**
     *  解析表单数据
     * @param uploadForm
     * @param field
     * @return
     */
    public static String getString(Map<String, List<InputPart>> uploadForm, String field){
        try {
            List<InputPart> list = uploadForm.get(field);
            if(null!=list){
                String temp = list.get(0).getBodyAsString();
                return temp;
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    /**
     * 写文件
     * @Title: writeFile
     * @return
     */
    public static void writeFile(byte[] content, String filename) throws Exception {

        File file = new File(filename);

        //文件存在
        if (file.exists()) {
            throw new Exception();
        }

        //路径格式不对
        if (filename.endsWith(File.separator)) {
            throw new Exception();
        }

        //创建目录
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                throw new Exception();
            }
        }

        //创建文件
        if (!file.createNewFile()) {
            throw new Exception();
        }

        //写文件
        FileOutputStream fop = new FileOutputStream(file);
        fop.write(content);
        fop.flush();
        fop.close();
    }

    /**
     * @Title: getFileSuffix
     * @param header
     * @return 返回上传图片后缀名
     */
    public static String getFileSuffix(MultivaluedMap<String, String> header){
        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {

                String[] name = filename.split("=");

                String finalFileName = name[1].trim().replaceAll("\"", "");

                return finalFileName.substring(finalFileName.indexOf(".")+1).toLowerCase();
            }
        }
        return "unknown";
    }

    /**
     *  校验邮箱
     1. 必须包含一个并且只有一个符号“@”
     2. 第一个字符不得是“@”或者“.”
     3. 不允许出现“@.”或者.@
     4. 结尾不得是字符“@”或者“.”
     5. 允许“@”前的字符中出现“＋”
     6. 不允许“＋”在最前面，或者“＋@”
     * @param email
     * @return
     */
    public static boolean checkEmail(String email)
    {
        String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(email);
        return matcher.matches();
    }

    /**
     *  MD5加密
     * @param sourceStr
     * @return
     */
    public static String MD5(String sourceStr) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
//            System.out.println("MD5(" + sourceStr + ",32) = " + result);
//            System.out.println("MD5(" + sourceStr + ",16) = " + buf.toString().substring(8, 24));
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        }
        return result;
    }

    /**
     * 获取用户真实IP地址，不使用request.getRemoteAddr();的原因是有可能用户使用了代理软件方式避免真实IP地址,
     * 参考文章： http://developer.51cto.com/art/201111/305181.htm
     *
     * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？
     * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串。
     *
     * 如：X-Forwarded-For：192.168.1.110, 192.168.1.120, 192.168.1.130,
     * 192.168.1.100
     *
     * 用户真实IP为： 192.168.1.110
     *
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static void main(String []args)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(5);
        sb.append(1);
        sb.append(3);
        sb.append("movie");
        System.out.println(MD5(sb.toString()));
    }

}
