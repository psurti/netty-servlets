package com.streaming.servlet.config;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class ServletConfiguration extends AbstractConfiguration<Servlet,ServletMappingConfiguration>
implements ServletConfig {

	private int loadOnStartup;

	private boolean enabled;


	public ServletConfiguration(String name, Servlet servlet, WebAppConfiguration parent) {
		this(name, servlet, parent, DEFAULT_URL_PATTERN);
	}

	public ServletConfiguration(Servlet servlet, WebAppConfiguration parent) {
		this(null, servlet, parent, DEFAULT_URL_PATTERN);
	}

	public ServletConfiguration(Servlet servlet, WebAppConfiguration parent, String... urlPatterns ) {
		this(null, servlet, parent, urlPatterns);
	}

	public ServletConfiguration(Class<? extends Servlet> servletClass, WebAppConfiguration parent) {
		this(null, servletClass, parent, DEFAULT_URL_PATTERN);
	}

	public ServletConfiguration(String name, Class<? extends Servlet> servletClass, WebAppConfiguration parent) {
		this(name, servletClass, parent, DEFAULT_URL_PATTERN);
	}

	public ServletConfiguration(Class<? extends Servlet> servletClass, WebAppConfiguration parent, String... urlPatterns) {
		this(null, newInstance(servletClass), parent, urlPatterns);
	}

	public ServletConfiguration(String name, Class<? extends Servlet> servletClass, WebAppConfiguration parent, String... urlPatterns) {
		this(name, newInstance(servletClass), parent,  urlPatterns);
	}

	public ServletConfiguration(String name, Servlet servlet, WebAppConfiguration parent, String... urlPatterns) {
		super(name, servlet);

		if (urlPatterns == null) {
			throw new IllegalArgumentException( "no url patterns were assig ned to component");
		}

		loadOnStartup = 0;
		enabled = true;

		for (String urlPattern : urlPatterns ) {
			urlPattern = (!urlPattern.endsWith("/")) ? urlPattern + "/" : urlPattern;
			ServletMappingConfiguration type = new ServletMappingConfiguration(parent.getContextPath() + urlPattern);
			addMappingConfigurations(type);
		}

		if (parent != null)
			parent.addServletConfigurations(this);
	}

	/**
	 * @return the loadOnStartup
	 */
	public int getLoadOnStartup() {
		return loadOnStartup;
	}

	/**
	 * @param loadOnStartup the loadOnStartup to set
	 */
	public void setLoadOnStartup(int loadOnStartup) {
		this.loadOnStartup = loadOnStartup;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public ServletContext getServletContext() {
		return null;
	}

	@Override
	public String getServletName() {
		return super.getName();
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
