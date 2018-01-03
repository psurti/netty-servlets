package com.streaming.servlet.config;

import javax.servlet.Filter;
import javax.servlet.Servlet;

import org.junit.Test;

public class FilterConfigurationTest {

	@Test
	public void test() {
		FilterConfiguration type = new FilterConfiguration("Set Character Encoding", (Filter)null, "/*", "/test/*");
		type.setDescription("Set Characeter Encoding Servlet");

		ServletConfiguration stype = new ServletConfiguration("Set CE", (Servlet)null);
		stype.setEnabled(false);
	}
}
