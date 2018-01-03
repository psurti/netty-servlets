/**
 *
 */
package com.streaming.servlet.config;

/**
 * Filter Mapping Type
 *
 * @author psurti
 *
 */
public class FilterMappingConfiguration extends AbstractMappingConfiguration {

	private DispatcherType dispatcherType;

	/**
	 * @param urlPattern
	 */
	public FilterMappingConfiguration(String urlPattern) {
		super(urlPattern);
		this.dispatcherType = DispatcherType.REQUEST;
	}


	/**
	 * @return the dispatcherType
	 */
	public DispatcherType getDispatcherType() {
		return dispatcherType;
	}

	/**
	 * @param dispatcherType the dispatcherType to set
	 */
	public void setDispatcherType(DispatcherType dispatcherType) {
		this.dispatcherType = dispatcherType;
	}
}