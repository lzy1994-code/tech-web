package com.wd.tech.web;

import com.wd.tech.web.util.PropertiesUtil;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.Properties;

@SpringBootApplication
public class TechWebApplication {

	public static void main(String[] args) {
		Properties log = null;
		try {
			log = PropertiesUtil.getProperties("properties/log4j.properties","utf-8");
			if (null == log) {
				System.out.println("没有找到日志文件");
			}
			PropertyConfigurator.configure(log);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		SpringApplication.run(TechWebApplication.class, args);
	}
}
