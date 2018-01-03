/**
 *
 */
package com.streaming.servlet.config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author psurti
 *
 */
public class ServletContextListenerConfiguration extends Configuration {

	private static final Logger log = LogManager.getLogger(ServletContextListenerConfiguration.class);

	private ServletContextListener listener;

	public ServletContextListenerConfiguration(ServletContextListener listener) {
		super();
		this.listener = listener;
	}

	public ServletContextListenerConfiguration(Class<? extends ServletContextListener> clazz) {
		this(newInstance(clazz));
	}

	public ServletContextListener getListener() {
		return this.listener;
	}

	public void initComponent() {
		try {

			log.debug("Initializing listener: {}", this.listener.getClass());
			this.listener.contextInitialized(new ServletContextEvent(null));

		} catch (Exception e) {
			log.error("Listener '" + this.listener.getClass()
			+ "' was not initialized!", e);
		}
	}

	  public void destroyComponent() {
		  try {

			  log.debug("Destroying listener: {}", this.listener.getClass());
			  this.listener.contextDestroyed(new ServletContextEvent(null));

	        } catch (Exception e) {
	            log.error("Listener '" + this.listener.getClass()
	                    + "' was not destroyed!", e);
	        }
	    }
}
