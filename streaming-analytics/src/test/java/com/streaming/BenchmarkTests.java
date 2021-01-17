package com.streaming;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.Throughput)
@Fork(1)
@State(Scope.Thread)
@Warmup(iterations = 10, time = 1, batchSize = 1000)
@Measurement(iterations = 10, time = 1, batchSize = 1000)
public class BenchmarkTests {

	@Param({"1000","100","10","5", "1"})
	private int size;
	private int[] original;

	@Setup
	public void setup() {
		original = new int[size];
		for (int i = 0; i < size; i++) {
			original[i] = i;
		}
	}

	@Benchmark
	public int[] SystemArrayCopy() {
		final int length = size;
		int[] destination = new int[length];
		System.arraycopy(original, 0, destination, 0, length);
		return destination;
	}


	@Benchmark
	public int[] arrayClone() {
		return original.clone();
	}

	public static void main(String[] args) throws Exception {
		BenchmarkTests tests = new BenchmarkTests();
		tests.size = 5_000_000;
		tests.setup();
		for (int k = 0; k < 100; k++) {
			long start = 0;
			start = System.currentTimeMillis();
			for (int i = 0; i < 100; i++) {
				tests.arrayClone();
			}
			System.out.println( "clone=" + (System.currentTimeMillis()-start));

			start = System.currentTimeMillis();
			for (int i = 0; i < 100; i++) {
				tests.SystemArrayCopy();
			}
			System.out.println( "array=" + (System.currentTimeMillis()-start));
			System.out.flush();
		}
	}
}