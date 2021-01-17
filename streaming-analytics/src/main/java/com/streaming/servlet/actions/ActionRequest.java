/**
 *
 */
package com.streaming.servlet.actions;

import java.io.IOException;
import java.io.StringReader;

import javax.servlet.ServletInputStream;

import com.streaming.servlet.GenericServletRequest;

/**
 * @author psurti
 *
 */
public class ActionRequest<T> extends GenericServletRequest
{
	/*
	 * input object
	 */
	private final T input;

	/**
	 * Constructor
	 *
	 * @param input
	 */
	public ActionRequest( T input ) {
		this.input = input;
	}

	/**
	 * Returns Input
	 *
	 * @return
	 */
	public T getInput() {
		return this.input;
	}

	/* (non-Javadoc)
	 * @see com.streaming.servlet.GenericServletRequest#getInputStream()
	 */
	@Override
	public ServletInputStream getInputStream() throws IOException {

		final StringReader strReader = new StringReader( input.toString() );
		return new ServletInputStream() {

			@Override
			public int read() throws IOException {

				int val = strReader.read();
				return val;
			}
		};

	}
}