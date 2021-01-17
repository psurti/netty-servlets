package com.streaming.servlet.config;

public class Configuration {

	protected Configuration() {}

	protected static final <T> T newInstance(Class<T> clazz)  {
        try {
            return clazz.newInstance();
        } catch (InstantiationException|IllegalAccessException e) {
            throw new IllegalStateException(
                    "Error instantiating class: " + clazz, e);
        }
    }
}