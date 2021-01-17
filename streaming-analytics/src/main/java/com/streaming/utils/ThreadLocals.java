package com.streaming.utils;

public class ThreadLocals {

	private ThreadLocals() { }

	public static final ThreadLocal<StringBuilder> stringBuilder =  new ThreadLocal<StringBuilder>() {
		@Override
		protected StringBuilder initialValue() {
			return new StringBuilder();
		}

		@Override
		public StringBuilder get() {
			StringBuilder b = super.get();
			b.setLength(0); // clear/reset the buffer
			return b;
		}

	};

}
