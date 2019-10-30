package org.bbottema.loremipsumobjects.typefactories.util;

import de.svenjacobs.loremipsum.LoremIpsum;

import java.util.Random;

/**
 * Utility class that contains various methods to generate dummy data for primitive types.
 */
public class LoremIpsumGenerator {

	// we're using an instance here, so we can mock this instance in our junit tests
	private static LoremIpsumGenerator instance = new LoremIpsumGenerator();

	private final Random r = new Random();
	private final LoremIpsum loremIpsum = new LoremIpsum();

	public String getRandomString() {
		StringBuilder gibberish = new StringBuilder();
		for (int i = 0; i <= r.nextInt(3); i++) {
			gibberish.append(gibberish.length() != 0 ? " " : "").append(loremIpsum.getWords(1, r.nextInt(50)));
		}
		return gibberish.toString().replaceAll("\\s", "_").replaceAll("[.,]", "");
	}

	public boolean getRandomBoolean() {
		return r.nextBoolean();
	}

	public int getRandomInt() {
		return r.nextInt();
	}

	public char getRandomChar() {
		return (char) r.nextInt();
	}

	public byte getRandomByte() {
		return (byte) r.nextInt();
	}

	public long getRandomLong() {
		return r.nextLong();
	}

	/**
	 * @return A float value between 00.00 and 100.00
	 */
	public float getRandomFloat() {
		return r.nextInt(100) + (float) Math.round(r.nextFloat() * 100) / 100;
	}

	/**
	 * @return A double value between 00.00 and 100.00
	 */
	public double getRandomDouble() {
		return r.nextInt(100) + (double) Math.round(r.nextDouble() * 100) / 100;
	}

	public short getRandomShort() {
		return (short) r.nextInt();
	}

	public int getRandomInt(final int max) {
		return r.nextInt(max);
	}

	public static void setInstance(final LoremIpsumGenerator instance) {
		LoremIpsumGenerator.instance = instance;
	}

	public static LoremIpsumGenerator getInstance() {
		return instance;
	}
}