package com.streaming.servlet.config;

import java.util.regex.Pattern;

public  class AbstractMappingConfiguration {

	private final String urlPattern;

	private Pattern regexPattern;

	private String sanitizedUrlPattern;


	public AbstractMappingConfiguration(String urlPattern) {
		this.urlPattern = setUrlPattern(urlPattern);
	}

	/*
	 * Set URL Pattern
	 */
	private String setUrlPattern(String urlPattern) {
		if (urlPattern == null || urlPattern.isEmpty())
			throw new IllegalArgumentException( "no url patterns were assigned to component");

        String regex = urlPattern.replaceAll("\\*", ".*");
        this.regexPattern = Pattern.compile(regex);
        sanitizedUrlPattern = urlPattern.replaceAll("\\*", "");
        if (sanitizedUrlPattern.endsWith("/")) {
        	sanitizedUrlPattern = sanitizedUrlPattern.substring(0,
            		sanitizedUrlPattern.length() - 1);
        }

        return urlPattern;
	}

	/**
	 * Determine if the path matches the url pattern
	 *
	 * @param path
	 * @return null if no match
	 */
	public String getMatchingUrlPattern(String path) {

		System.out.println( path + " matches " + regexPattern + " " + regexPattern.matcher(path).matches());
		if (this.regexPattern.matcher(path).matches())
			return sanitizedUrlPattern;

		return null;
	}


	protected String getUrlPattern() {
		return this.urlPattern;
	}


}
