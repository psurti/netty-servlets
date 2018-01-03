package com.streaming.servlet;

import javax.servlet.FilterChain;

import com.streaming.servlet.config.WebAppConfiguration;

public class ServletWebApp {

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


	public void run(String uri) {
		FilterChain chain = this.config.initFilterChain(uri);
		if (chain != null ) {
			//handleServletRequest(request, chain)
		}
	}
}
