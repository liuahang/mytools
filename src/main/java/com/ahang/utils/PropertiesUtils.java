package com.ahang.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

public class PropertiesUtils {

	private PropertiesUtils() {
	}
	
	/**
	 * @����: ����name��ȡproperties�ļ��е�value
	 * @����: yangc
	 * @��������: 2013-11-21 ����07:01:48
	 * @param filePath properties�ļ�·��(classpath�е����·��)
	 * @param name
	 * @return
	 */
	public static String getProperty(String filePath, String name) {
		if (StringUtils.isBlank(filePath) || StringUtils.isBlank(name)) {
			throw new IllegalArgumentException("The parameters must not be null");
		}
		return getProperty(filePath, name, null);
	}
	
	/**
	 * @����: ����name��ȡproperties�ļ��е�value, ���Ϊ�շ���Ĭ��ֵ
	 * @����: yangc
	 * @��������: 2013-11-21 ����07:01:48
	 * @param filePath properties�ļ�·��(classpath�е����·��)
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public static String getProperty(String filePath, String name, String defaultValue) {
		if (StringUtils.isBlank(filePath) || StringUtils.isBlank(name)) {
			throw new IllegalArgumentException("The parameters must not be null");
		}
		Properties prop = new Properties();
		InputStream in = null;
		try {
			in = PropertiesUtils.class.getResourceAsStream(filePath);
			prop.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return prop.getProperty(name, defaultValue);
	}

	public static void main(String[] args) {
		System.out.println(PropertiesUtils.getProperty("/test.properties", "json"));
	}
}
