package com.streaming.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestHttpServlet extends javax.servlet.http.HttpServlet {

	private static final Logger logger = LogManager.getLogger(TestHttpServlet.class);

	@Override
	protected void service(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		logger.info( "HELLO:" + getClass());
		logger.info(arg0.getClass());
	}

	/**
	 * serial uid
	 */
	private static final long serialVersionUID = -4151878009124577095L;
}
