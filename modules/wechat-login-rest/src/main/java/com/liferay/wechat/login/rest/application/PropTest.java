package com.liferay.wechat.login.rest.application;

//import com.liferay.portal.kernel.log.Log;
//import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author SaraLiu
 *
 */
public class PropTest {
//	Log logger = LogFactoryUtil.getLog(PropTest.class);

	private static final String PROP_FILE_NAME = "config.properties";
	private static final String key = "foo";
	private final String value;

	{
		Properties prop = new Properties();
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PROP_FILE_NAME);

		if (inputStream != null) {
			try {
				prop.load(inputStream);
			} catch (IOException e) {
				System.out.println(e);
			}
		} else {
			FileNotFoundException e = new FileNotFoundException(
					"property file '" + PROP_FILE_NAME + "' not found in the classpath");
			System.out.println(e);
		}

		value = prop.getProperty(key);
		System.out.println(key + ": " + value);
		assert value == "barf";
	}

	public static void main(String[] args) {
		new PropTest();
	}

}
