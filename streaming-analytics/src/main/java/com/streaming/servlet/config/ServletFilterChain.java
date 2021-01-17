package com.streaming.servlet.config;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Implementation of the Servlet FilterChain Specification
 *
 * @author psurti
 */
public class ServletFilterChain implements FilterChain {

	private final ServletConfiguration servletCfg;

	private final LinkedList<FilterConfiguration> filterCfgs;

	public ServletFilterChain(ServletConfiguration servletCfg, List<FilterConfiguration> filterCfgs) {
		this.servletCfg = servletCfg;
		this.filterCfgs= new LinkedList<>(filterCfgs);
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp) throws IOException, ServletException {

		FilterConfiguration config = filterCfgs != null ? filterCfgs.poll() : null;

		if (config != null) {
			config.getComponent().doFilter(req, resp, this);
		}
		else if (this.servletCfg != null) {
			servletCfg.getComponent().service(req, resp);
		}
	}

	public boolean isValid() {
		return this.servletCfg != null
				|| (this.filterCfgs != null && !this.filterCfgs.isEmpty());
	}
}
