/**
 *
 */
package com.streaming.servlet.actions;

import java.io.IOException;

import javax.servlet.ServletException;

/**
 * @author psurti
 *
 */
public interface Actionable<T, S> {


	/*
	 * Generic Serviceable
	 */
	public S execute( T input );

	/*
	 * Request/Response
	 */
	public void service(ActionRequest<T> req, ActionResponse<S> resp ) throws ServletException, IOException;
}
