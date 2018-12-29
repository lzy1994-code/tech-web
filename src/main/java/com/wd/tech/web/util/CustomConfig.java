package com.wd.tech.web.util;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @program: tech-web
 * @description: 自定义配置
 * @author: Lzy
 * @create: 2018-09-15 09:16
 **/
@Component
@ConfigurationProperties(prefix = "tech.pic")
public class CustomConfig {

    private String communityPath;

    private String communityVisit;

    private String headPath;

    private String headVisit;

    private String groupPath;

    private String groupVisit;


    public String getCommunityPath() {
        return communityPath;
    }

    public void setCommunityPath(String communityPath) {
        this.communityPath = communityPath;
    }

    public String getCommunityVisit() {
        return communityVisit;
    }

    public void setCommunityVisit(String communityVisit) {
        this.communityVisit = communityVisit;
    }

    public String getHeadPath() {
        return headPath;
    }

    public void setHeadPath(String headPath) {
        this.headPath = headPath;
    }

    public String getHeadVisit() {
        return headVisit;
}

    public void setHeadVisit(String headVisit) {
        this.headVisit = headVisit;
    }

    public String getGroupPath() {
        return groupPath;
    }

    public void setGroupPath(String groupPath) {
        this.groupPath = groupPath;
    }

    public String getGroupVisit() {
        return groupVisit;
    }

    public void setGroupVisit(String groupVisit) {
        this.groupVisit = groupVisit;
    }

    @Override
    public String toString() {
        return "CustomConfig{" +
                "communityPath='" + communityPath + '\'' +
                ", communityVisit='" + communityVisit + '\'' +
                ", headPath='" + headPath + '\'' +
                ", headVisit='" + headVisit + '\'' +
                ", groupPath='" + groupPath + '\'' +
                ", groupVisit='" + groupVisit + '\'' +
                '}';
    }
}
