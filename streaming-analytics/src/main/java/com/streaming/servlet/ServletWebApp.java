package com.streaming.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.streaming.servlet.config.ServletFilterChain;
import com.streaming.servlet.config.WebAppConfiguration;

public class ServletWebApp  {

	private WebAppConfiguration config;

	public ServletWebApp() {
		//noop
	}

	public void init(WebAppConfiguration config) {
		this.config = config;
		if (this.config == null) throw new IllegalArgumentException( "config is nil");
		this.config.initServletContext();
		this.config.initContextListeners();
		this.config.initFilters();
		this.config.initServlets();
	}


	public void destroy() {
		this.config.destroyServlets();
		this.config.destroyFilters();
		this.config.destroyContextListeners();
	}


	public boolean run(String uri, ServletRequest req, ServletResponse resp)
			throws IOException, ServletException {

		boolean ret = true;

		ServletFilterChain chain = this.config.initFilterChain(uri);

		if (chain.isValid() ) {
			handleServletRequest(req, resp, chain);
		} else {
			throw new IllegalArgumentException( "No handler for uri:" + uri);
		}

		return ret;
	}

	private void handleServletRequest(ServletRequest req, ServletResponse resp, ServletFilterChain chain)
			throws IOException, ServletException {

		chain.doFilter(req, resp);

		PrintWriter writer = resp.getWriter();
		if (writer != null)
			writer.flush();
	}

}
