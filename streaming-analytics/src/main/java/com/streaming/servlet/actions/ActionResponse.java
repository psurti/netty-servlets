/**
 *
 */
package com.streaming.servlet.actions;

import com.streaming.servlet.GenericServletResponse;

/**
 * @author psurti
 *
 */
public class ActionResponse<T> extends GenericServletResponse {

	/*
	 * output object
	 */
	private T result;

	/**
	 * Constructor
	 *
	 * @param input
	 */
	public ActionResponse() {
		// do nothing
	}

	/**
	 * Returns Input
	 *
	 * @return
	 */
	public T getResult() {
		return this.result;
	}

	/**
	 * Set Result
	 *
	 * @param output
	 */
	public ActionResponse<T> setResult(T result) {
		this.result = result;
		return this;
	}
}
