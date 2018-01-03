/**
 *
 */
package com.streaming.servlet.config;

import java.io.InputStream;
import java.net.FileNameMap;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.streaming.servlet.DefaultRequestDispatcher;

/**
 * @author psurti
 *
 */
public class ServletContextConfiguration extends Configuration implements ServletContext {

	private static final Logger logger = LogManager.getLogger( ServletContextConfiguration.class);

	private Map<String,Object> attributes;

	private Map<String,String> initParameters;

	private String servletContextName;

	private final WebAppConfiguration config;


	public ServletContextConfiguration(WebAppConfiguration config) {
		this.config = config;
	}

	@Override
	public Object getAttribute(String name) {
        return attributes != null ? attributes.get(name) : null;
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return (attributes != null) ? Collections.enumeration( attributes.keySet() ) : Collections.emptyEnumeration();
	}

	@Override
	public ServletContext getContext(String path) {
		return this;
	}


	public void addInitParameter(String name, String value) {
		if (this.initParameters == null)
			this.initParameters = new HashMap<>();

		this.initParameters.put(name, value);
	}

	@Override
	public String getInitParameter(String name) {
		if (this.initParameters == null)
			return null;

		return this.initParameters.get(name);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return (initParameters != null) ? Collections.enumeration( initParameters.keySet() ) : Collections.emptyEnumeration();
	}

	@Override
	public int getMajorVersion() {
		return 2;
	}

	@Override
	public String getMimeType(String fileUrl) {
		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		return fileNameMap.getContentTypeFor(fileUrl);
	}

	@Override
	public int getMinorVersion() {
		return 4;
	}

	@Override
	public RequestDispatcher getNamedDispatcher(String name) {
		ServletConfiguration servletCfg = config.findServletByName(name);
		Servlet servlet = null;
		String servletName = null;

		if (servletCfg != null) {
			servlet = servletCfg.getComponent();
			servletName = servletCfg.getServletName();
		}

		return new DefaultRequestDispatcher(servletName, null, servlet);
	}

	@Override
	public String getRealPath(String arg0) {
		return null;
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String servletPath) {
		ServletConfiguration servletCfg = config.findServlet(servletPath);
		Servlet servlet = null;
		String servletName = null;

		if (servletCfg != null) {
			servlet = servletCfg.getComponent();
			servletName = servletCfg.getServletName();
		}

		return new DefaultRequestDispatcher(servletName, servletPath, servlet);
	}

	@Override
	public URL getResource(String path) throws MalformedURLException {
		return ServletContextConfiguration.class.getResource(path);
	}

	@Override
	public InputStream getResourceAsStream(String path) {
		return ServletContextConfiguration.class.getResourceAsStream(path);
	}

	@Override
	public Set<String> getResourcePaths(String arg0) {
		return Collections.emptySet();
	}

	@Override
	public String getServerInfo() {
		return null;
	}

	@Override
	public Servlet getServlet(String name) throws ServletException {
	    throw new IllegalStateException(
                "Deprecated as of Java Servlet API 2.1, with no direct replacement!");
	}

	@Override
	public String getServletContextName() {
		return this.servletContextName;

	}

	@Override
	public Enumeration<String> getServletNames() {
	       throw new IllegalStateException(
	                "Method 'getServletNames' deprecated as of Java Servlet API 2.0, with no replacement.");
	}

	@Override
	public Enumeration<Servlet> getServlets() {
	     throw new IllegalStateException(
	                "Method 'getServlets' deprecated as of Java Servlet API 2.0, with no replacement.");
	}

	@Override
	public void log(String arg0) {
		logger.info(arg0);
	}

	@Override
	public void log(Exception arg0, String arg1) {
		logger.error(arg1, arg0);
	}

	@Override
	public void log(String message, Throwable throwable) {
        logger.error(message, throwable);
	}

	@Override
	public void removeAttribute(String name) {
		if (this.attributes != null)
			this.attributes.remove(name);
	}

	@Override
	public void setAttribute(String name, Object value) {
		if (this.attributes == null)
			this.attributes = new HashMap<>();
		this.attributes.put(name, value);
	}

}
