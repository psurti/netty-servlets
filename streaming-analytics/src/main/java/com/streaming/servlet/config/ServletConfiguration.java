package com.streaming.servlet.config;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class ServletConfiguration extends AbstractConfiguration<Servlet,ServletMappingConfiguration>
implements ServletConfig {

	private int loadOnStartup;

	private boolean enabled;


	public ServletConfiguration(String name, Servlet servlet) {
		this(name, servlet, DEFAULT_URL_PATTERN);
	}

	public ServletConfiguration(Servlet servlet) {
		this(null, servlet, DEFAULT_URL_PATTERN);
	}

	public ServletConfiguration(Servlet servlet, String... urlPatterns ) {
		this(null, servlet, urlPatterns);
	}

	public ServletConfiguration(Class<? extends Servlet> servletClass) {
		this(null, servletClass, DEFAULT_URL_PATTERN);
	}

	public ServletConfiguration(String name, Class<? extends Servlet> servletClass) {
		this(name, servletClass, DEFAULT_URL_PATTERN);
	}

	public ServletConfiguration(Class<? extends Servlet> servletClass, String... urlPatterns) {
		this(null, newInstance(servletClass), urlPatterns);
	}

	public ServletConfiguration(String name, Class<? extends Servlet> servletClass, String... urlPatterns) {
		this(name, newInstance(servletClass), urlPatterns);
	}

	public ServletConfiguration(String name, Servlet servlet, String... urlPatterns) {
		super(name, servlet);

		if (urlPatterns == null) {
			throw new IllegalArgumentException( "no url patterns were assig ned to component");
		}

		loadOnStartup = 0;
		enabled = true;

		for (String urlPattern : urlPatterns ) {
			ServletMappingConfiguration type = new ServletMappingConfiguration(urlPattern);
			addMappingConfigurations(type);
		}
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
