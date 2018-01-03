package com.streaming.servlet.config;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class FilterConfiguration extends AbstractConfiguration<Filter,FilterMappingConfiguration>
implements FilterConfig {

	public FilterConfiguration(String name, Filter filter) {
		this(name, filter, DEFAULT_URL_PATTERN);
	}

	public FilterConfiguration(Filter filter) {
		this(null, filter, DEFAULT_URL_PATTERN);
	}

	public FilterConfiguration(Filter filter, String... urlPatterns ) {
		this(null, filter, urlPatterns);
	}

	public FilterConfiguration(Class<? extends Filter> filterClass) {
		this(null, filterClass, DEFAULT_URL_PATTERN);
	}

	public FilterConfiguration(String name, Class<? extends Filter> filterClass) {
		this(name, filterClass, DEFAULT_URL_PATTERN);
	}

	public FilterConfiguration(Class<? extends Filter> filterClass, String... urlPatterns) {
		this(null, newInstance(filterClass), urlPatterns);
	}

	public FilterConfiguration(String name, Class<? extends Filter> filterClass, String... urlPatterns) {
		this(name, newInstance(filterClass), urlPatterns);
	}

	public FilterConfiguration(String name, Filter filter, String... urlPatterns) {
		super(name, filter);

		if (urlPatterns == null) {
			throw new IllegalArgumentException( "no url patterns were assig ned to component");
		}

		for (String urlPattern : urlPatterns ) {
			FilterMappingConfiguration type = new FilterMappingConfiguration(urlPattern);
			addMappingConfigurations(type);
		}
	}

	@Override
	public String getFilterName() {
		return getName();
	}

	@Override
	public ServletContext getServletContext() {
		return null;
	}

	@Override
	protected void doInitComponent() throws ServletException {
		getComponent().init(this);
	}

	@Override
	protected void doDestroyComponent() throws ServletException {
		getComponent().destroy();
	}

}
