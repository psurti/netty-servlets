package com.streaming.servlet.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;

public class WebAppConfiguration {

	private final String contextPath;

	private Map<String,String> contextParameters;

	private Map<String,FilterConfiguration> filterCfgs;

	private Map<String,ServletConfiguration> servletCfgs;

	private Collection<ServletContextListenerConfiguration> contextListeners;

 	public WebAppConfiguration(String contextPath) {
 		this.contextPath = contextPath;
	}

	public WebAppConfiguration addContextParameters( String name, String value) {
		if (this.contextParameters == null) {
			this.contextParameters = new LinkedHashMap<>();
		}
		this.contextParameters.put(name, value);
		return this;
	}

	public WebAppConfiguration addFilterConfigurations(
			FilterConfiguration... configs) {

		if (configs == null)
			return this;

		if (this.filterCfgs == null) {
			this.filterCfgs = new LinkedHashMap<>();
		}
		for (FilterConfiguration filterCfg : configs) {
			this.filterCfgs.put(filterCfg.getFilterName(), filterCfg);
		}

		return this;
	}

	public boolean exists(Class<? extends Configuration> clazz) {
		if (clazz == ServletConfiguration.class)
			return (this.servletCfgs != null && !servletCfgs.isEmpty());
		else if (clazz == FilterConfiguration.class)
			return (this.filterCfgs != null && !filterCfgs.isEmpty());
		else if (clazz == ServletContextListenerConfiguration.class) {
			return (this.contextListeners != null && !this.contextListeners.isEmpty());
		}

		return false;
	}

	public WebAppConfiguration addServletConfigurations(
			ServletConfiguration... configs) {

		if (configs == null)
			return this;

		if (this.servletCfgs == null) {
			this.servletCfgs = new LinkedHashMap<>();
		}
		for (ServletConfiguration servletCfg : configs) {
			this.servletCfgs.put(servletCfg.getServletName(), servletCfg);
		}

		return this;
	}

	public WebAppConfiguration addServletContextListenerConfigurations(
			ServletContextListenerConfiguration... configs) {
		if (configs == null || configs.length == 0)
			return this;

		if (this.contextListeners == null) {
			this.contextListeners = new ArrayList<>();
		}

		this.contextListeners.addAll(Arrays.asList(configs));

		return this;
	}

	public String getContextPath() {
		return this.contextPath;
	}

	public static void main(String[] args) {
		WebAppConfiguration webAppConfig = new WebAppConfiguration("/testApp");
		FilterConfiguration filterConfig = new FilterConfiguration("null", (Filter)null);
		webAppConfig.addFilterConfigurations(filterConfig);
		webAppConfig.hashCode();
	}


	public FilterChain initFilterChain(String uri) {
		List<FilterConfiguration> filterCfgList = new ArrayList<>();
		ServletConfiguration servletCfg = findServlet(uri);

		if (exists(FilterConfiguration.class)) {
			for (FilterConfiguration filterCfg : this.filterCfgs.values()) {
				if (filterCfg.matchesUrlPattern(uri))
					filterCfgList.add(filterCfg);
			}
		}

		return new ServletFilterChain(servletCfg, filterCfgList);
	}

	public void initContextListeners() {
		if (exists(ServletContextListenerConfiguration.class)) {
			for (ServletContextListenerConfiguration listenerCfg : this.contextListeners) {
				listenerCfg.initComponent();
			}
		}
	}

	public void initFilters() {
		if (exists(FilterConfiguration.class)) {
			for (FilterConfiguration filterCfg : this.filterCfgs.values()) {
				filterCfg.initComponent();
			}
		}
	}

	public void initServlets() {
		if (exists(ServletConfiguration.class)) {
			for (ServletConfiguration servletCfg : this.servletCfgs.values()) {
				servletCfg.initComponent();
			}
		}

	}

	public void initServletContext() {
	}

	public void destroyContextListeners() {
		if (exists(ServletContextListenerConfiguration.class)) {
			for (ServletContextListenerConfiguration listenerCfg : this.contextListeners) {
				listenerCfg.destroyComponent();
			}
		}
	}

	public void destroyFilters() {
		if (exists(FilterConfiguration.class)) {
			for (FilterConfiguration filterCfg : this.filterCfgs.values()) {
				filterCfg.destroyComponent();
			}
		}
	}

	public void destroyServlets() {
		if (exists(ServletConfiguration.class)) {
			for (ServletConfiguration servletCfg : this.servletCfgs.values()) {
				servletCfg.destroyComponent();
			}
		}
	}


	public ServletConfiguration findServletByName(String name) {
		if (this.servletCfgs == null || this.servletCfgs.isEmpty() )
			return null;

		for (ServletConfiguration servletCfg : this.servletCfgs.values() ) {
			if (servletCfg.getServletName().equalsIgnoreCase(name))
				return servletCfg;
		}

		return null;
	}


	public ServletConfiguration findServlet(String uri) {
		if (this.servletCfgs == null || this.servletCfgs.isEmpty() )
			return null;

		for (ServletConfiguration servletCfg : this.servletCfgs.values() ) {
			if (servletCfg.matchesUrlPattern(uri))
				return servletCfg;
		}

		return null;
	}
}
