package com.streaming.servlet.actions;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class ActionServlet<T, K> extends GenericServlet implements Actionable<T, K>
{
	/**
	 * Serializable UID
	 */
	private static final long serialVersionUID = 1786301991510127421L;

	private static final Logger logger = LogManager.getLogger(ActionServlet.class);

	/*
	 * belongs at some common place
	 */
	public static final ObjectMapper mapper = new ObjectMapper();

	/*
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() throws ServletException {

	}


	@SuppressWarnings("unchecked")
	@Override
	public void service(ServletRequest arg0, ServletResponse arg1) throws ServletException, IOException {

		service( (ActionRequest<T>) arg0, (ActionResponse<K>) arg1 );
	}

	@Override
	public void service(ActionRequest<T> req, ActionResponse<K> resp) throws ServletException, IOException {

		//-- pending: convert body to object
		String data = mapper.readValue(req.getInputStream(), String.class);
		logger.info( "data: {0}", data);
		//-- pending: convert object to body

		K result = execute(req.getInput());

		if ( resp != null ) {
			resp.setResult(result);
		}
	}

	@Override
	public void destroy() {

	}

}
