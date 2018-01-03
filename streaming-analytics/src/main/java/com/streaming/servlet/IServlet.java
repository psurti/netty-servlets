/**
 * 
 */
package com.streaming.servlet;

/**
 * @author psurti
 *
 */
public interface IServlet<T,R> {
	
	public void service(T req, R resp);

}
