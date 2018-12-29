package com.wd.tech.web.util;

import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by xyj on 2018/9/28.
 */
public class UserInfoVo {

    private String nickName;
    private int sex;
    private String signature;
    private String birthday;
    private String email;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Date getBirthday() {
        if (birthday == null)
        {
            return null;
        }
        DateTime time = new DateTime(birthday);
        return time.toDate();
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "UserInfoVo{" +
                ", nickName='" + nickName + '\'' +
                ", sex=" + sex +
                ", signature='" + signature + '\'' +
                ", birthday='" + birthday + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
