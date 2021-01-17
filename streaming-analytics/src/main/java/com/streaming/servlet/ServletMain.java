package com.streaming.servlet;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.streaming.servlet.actions.ActionRequest;
import com.streaming.servlet.actions.ActionResponse;
import com.streaming.servlet.actions.ActionServlet;
import com.streaming.servlet.config.ServletConfiguration;
import com.streaming.servlet.config.WebAppConfiguration;
import com.streaming.utils.ThreadLocals;

public class ServletMain {
	private static final Logger logger = LogManager.getLogger(ServletMain.class);


	 public static class MyActionServlet extends ActionServlet<Map<?,?>,String> {
		private static final long serialVersionUID = 1L;

		@Override
		public String execute(Map<?,?> input) {
			return "response to " + input;
		}

	}

	 private static String digits = "0123456789abcdef";

	 public static char[] toHex(byte[] data, int length){
		 length = (length > 0) ? length : data.length;

		 StringBuilder buf = ThreadLocals.stringBuilder.get();
	     for (int i = 0; i != length; i++)	{
	         int v = data[i] & 0xff;
	         buf.append(digits.charAt(v >> 4));
	         buf.append(digits.charAt(v & 0xf));
	     }
	     return buf.toString().toCharArray();
	 }

	 public static boolean isJavaSerializable(byte[] data) {
		 char[] ret = toHex(data, 2);
		 if (ret == null || ret.length < 4) return false;
		 return (ret[0] == 'a' && ret[1] == 'c' && ret[2] == 'e' && ret[3] == 'd');
	 }

	public static void main(String[] args) throws ServletException, IOException {

		WebAppConfiguration webAppCfg = new WebAppConfiguration("/test");
		new ServletConfiguration(TestHttpServlet.class, webAppCfg, "/t1");
		ServletWebApp app = new ServletWebApp();

		Serializable[] test = new Serializable[] { Double.valueOf(123), Integer.valueOf(123), (Serializable)Collections.emptyList() };
		byte[] t0 = "{ \"test\":4300 }".getBytes();
		ObjectMapper mapper  = new ObjectMapper();
		String t0Str = mapper.convertValue(t0, String.class);
		System.out.println( t0 + "->" + t0Str);

		for ( int j = 0; j < test.length; j++) {
			int i = 0;
			byte[] sBytes = org.apache.commons.lang3.SerializationUtils.serialize( test[j] );
			new FileOutputStream("out.bin").write(sBytes);

			char[] shex = toHex(sBytes, 2);
			for (char sh : shex) {
				System.out.print(sh);
			}
			System.out.println( "IsJavaSerializable:" + isJavaSerializable(sBytes) );
			System.out.println("");
		}

		app.init(webAppCfg);

		app.run("/test/t1", null, null);


		app.destroy();


		Map<String,String> input = new HashMap<>();
		input.put("test", "direct");
		MyActionServlet actionServlet = new MyActionServlet();
		logger.info( actionServlet.execute(input) );

		String jsonString = "{\"test\":{} }";
		//service call
		ActionRequest<String> a1 = new ActionRequest<>(jsonString);
		ActionResponse<String> a2 = new ActionResponse<>();
		actionServlet.service(a1, a2);
		logger.info( a2.getResult() );


	}
}
