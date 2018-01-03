package com.streaming.servlet.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractConfiguration<C, M extends AbstractMappingConfiguration> extends Configuration
{
	private static final Logger logger = LogManager.getLogger(AbstractConfiguration.class);

	protected static final String DEFAULT_URL_PATTERN = "/*";

	private final String name;

	private String description;

	private Map<String,String> initParameters;

	private boolean asyncSupported;

	private C component;

	private Collection<M> mappings;

	/*
	 * Constructor
	 */
	public AbstractConfiguration(String name, C component) {
		this.name = (name == null && component != null) ? component.getClass().getName() : name;
		this.component = component;
		this.asyncSupported = false;
	}

	/*
	 * Add mapping types
	 */
	@SafeVarargs
	public final void addMappingConfigurations( M... mappingTypes ) {
		for (M mappingType :  mappingTypes) {
			getMappings().add( mappingType );
		}
	}

	/*
	 * lazy initialize and return mappings
	 */
	private  Collection<M> getMappings() {
		if (mappings == null)
			mappings = new ArrayList<>();
		return mappings;
	}

	/*
	 * Add Initial Parameter
	 */
	public void addInitParameter(String name, String value) {
        if (this.initParameters == null)
        	this.initParameters = new HashMap<>();

        this.initParameters.put(name, value);
    }

	/*
	 * Get Initial Parameter
	 */
    public String getInitParameter(String name) {
        if (this.initParameters == null)
            return null;

        return this.initParameters.get(name);
    }


	public Enumeration<String> getInitParameterNames() {
		if (this.initParameters == null)
			return Collections.emptyEnumeration();

		return Collections.enumeration(this.initParameters.keySet());
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}


	/**
	 * @param descripion the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}


	/**
	 * @return the asyncSupported
	 */
	public boolean isAsyncSupported() {
		return asyncSupported;
	}


	/**
	 * @param asyncSupported the asyncSupported to set
	 */
	public void setAsyncSupported(boolean asyncSupported) {
		this.asyncSupported = asyncSupported;
	}


	/*
	 * Return the name of the type
	 */
	public String getName() {
		return name;
	}


	public C getComponent() {
		return component;
	}


	public String getMatchingUrlPattern(String uri) {
		int indx = uri.indexOf('?');
		String path = indx != -1 ? uri.substring(0, indx) : uri.substring(0);
		if (!path.endsWith("/"))
			path += "/";

		for (M m : mappings) {
			String matchingUrlPattern = m.getMatchingUrlPattern(path);
			if (matchingUrlPattern != null)
				return matchingUrlPattern;

		}

		return null;
	}

	public boolean matchesUrlPattern(String uri) {
		return getMatchingUrlPattern(uri) != null;
	}

	public void initComponent() {
		try {
			logger.debug( "Initializing component: {}", this.name );
			doInitComponent();

		} catch (ServletException e ) {
			logger.error( "Component '{0}' was not initialized!", this.getName(), e);
		}
	}

	protected abstract void doInitComponent() throws ServletException;

	public void destroyComponent() {
		 try {
			 logger.debug("Destroying http component: {}", this.component
					 .getClass());

			 this.doDestroyComponent();
		 } catch (ServletException e) {
			 logger.error("Http component '" + this.component.getClass()
			 + "' was not destroyed!", e);
		 }
	 }

	protected abstract void doDestroyComponent() throws ServletException;
}
