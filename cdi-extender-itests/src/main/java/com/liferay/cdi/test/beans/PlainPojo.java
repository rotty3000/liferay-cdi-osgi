package com.liferay.cdi.test.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlainPojo {

	static Logger logger = LoggerFactory.getLogger(PlainPojo.class);

	static {
		logger.info(
			"=============== TEST =================\n" +
			"=============== TEST =================\n" +
			"=============== TEST =================\n" +
			"=============== TEST =================\n" +
			"=============== TEST =================\n" +
			"=============== TEST =================\n"
		);
	}

}