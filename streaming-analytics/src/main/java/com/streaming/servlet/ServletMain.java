package com.streaming.servlet;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.streaming.servlet.actions.ActionRequest;
import com.streaming.servlet.actions.ActionResponse;
import com.streaming.servlet.actions.ActionServlet;
import com.streaming.servlet.config.ServletConfiguration;
import com.streaming.servlet.config.WebAppConfiguration;

public class ServletMain {
	private static final Logger logger = LogManager.getLogger(ServletMain.class);


	 public static class MyActionServlet extends ActionServlet<String,String> {
		private static final long serialVersionUID = 1L;

		@Override
		public String execute(String input) {
			return "response to " + input;
		}

	}

	public static void main(String[] args) throws ServletException, IOException {

		WebAppConfiguration webAppCfg = new WebAppConfiguration("/test");
		webAppCfg.addServletConfigurations( new ServletConfiguration(TestHttpServlet.class, "/t1") );
		ServletWebApp app = new ServletWebApp();
		app.init(webAppCfg);

		app.run("/test/t1");


		app.destroy();



		MyActionServlet actionServlet = new MyActionServlet();
		logger.info( actionServlet.execute("direct-hello") );

		ActionRequest<String> a1 = new ActionRequest<>("{}");
		ActionResponse<String> a2 = new ActionResponse<>();
		actionServlet.service(a1, a2);

		logger.info( a2.getResult() );


	}
}
