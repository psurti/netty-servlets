package com.streaming.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class DefaultRequestDispatcher implements RequestDispatcher {

	/*
	 * Servlet name for a named dispatcher
	 */
	private final String servletName;

	/*
	 * Servlet path for this reguest dispatcher
	 */
	private final String servletPath;

	/*
	 * Servlet
	 */
	private final Servlet servlet;


	public DefaultRequestDispatcher( String servletName,
			String servletPath, Servlet servlet) {
		this.servletName = servletName;
		this.servletPath = servletPath;
		this.servlet = servlet;
	}

	@Override
	public void forward(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
		if (servlet != null) {
			//This needs to be revisited!
			servlet.service(servletRequest, servletResponse);
		}
	}

	@Override
	public void include(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
		if (servlet != null) {
			//This needs to be revisited!
			servlet.service(servletRequest, servletResponse);
		}
	}

}
