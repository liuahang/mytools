package com.ahang.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import javax.mail.internet.MimeUtility;
public class AttachmentUtils {

	private AttachmentUtils() {
	}

	/**
	 * @����: ���ݲ�ͬ�������ֹ���صĸ�����������
	 * @����: yangc
	 * @��������: 2016��4��26�� ����10:13:15
	 * @param request
	 * @param response
	 * @param fileName
	 */
	public static void convertAttachmentFileName(HttpServletRequest request, HttpServletResponse response, String fileName) {
		if (StringUtils.isNotBlank(fileName)) {
			try {
				fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
				String userAgent = request.getHeader("User-Agent");
				if (StringUtils.isNotBlank(userAgent)) {
					userAgent = userAgent.toLowerCase();
					// IE�����ֻ�ܲ���URLEncoder����
					if (userAgent.indexOf("msie") != -1) {
						fileName = "filename=\"" + fileName + "\"";
					}
					// Opera�����ֻ�ܲ���filename*
					else if (userAgent.indexOf("opera") != -1) {
						fileName = "filename*=UTF-8''" + fileName;
					}
					// Chrome�����ֻ�ܲ���MimeUtility�����ISO������������
					else if (userAgent.indexOf("chrome") != -1) {
						fileName = "filename=\"" + MimeUtility.encodeText(fileName, "UTF-8", "B") + "\"";
					}
					// Safari�����ֻ�ܲ���ISO������������
					else if (userAgent.indexOf("safari") != -1) {
						fileName = "filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + "\"";
					}
					// FireFox���������ʹ��MimeUtility��filename*��ISO������������
					else if (userAgent.indexOf("mozilla") != -1) {
						fileName = "filename*=UTF-8''" + fileName;
					}
				}
				response.setHeader("Content-Disposition", "attachment; " + fileName);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}
}
