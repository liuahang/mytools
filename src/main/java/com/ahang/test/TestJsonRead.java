package com.ahang.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestJsonRead {

	public static void main(String args[])  throws Exception {
		TestJsonRead test = new TestJsonRead();
		Map map = test.getJsonMap();
		System.out.println("map==" + map );
	}
	
	public Map  getJsonMap() throws Exception{
		String json = "";
		Map map = null;
		InputStream stream = this.getClass().getClassLoader().getResourceAsStream("json/policy.json");
        StringWriter writer = new StringWriter();
        IOUtils.copy(stream, writer, String.valueOf(Charset.defaultCharset()));
        json = writer.toString();
        map = new ObjectMapper().readValue(json, HashMap.class);
        return map;
	}
}
