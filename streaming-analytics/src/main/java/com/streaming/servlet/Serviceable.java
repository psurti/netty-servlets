/**
 * 
 */
package com.streaming.servlet;

/**
 * @author psurti
 *
 */
public interface Serviceable<T, S> {
	
	/*
	 * Generic Serviceable 
	 */
	public S service( T req, S resp );
}
