/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.opensource.org/licenses/cddl1.php
 * or http://www.opensource.org/licenses/cddl1.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.opensource.org/licenses/cddl1.php.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 */
package org.dummycreator;

import java.util.Random;

import de.svenjacobs.loremipsum.LoremIpsum;

/**
 * Utility class that contains various methods to generate dummy data for primitive types.
 * 
 * @author Alexander Muthmann <amuthmann@dev-eth0.de> (original author)
 * @author Benny Bottema <b.bottema@projectnibble.org> (further developed project)
 */
public final class RandomCreator {

    private static final Random r = new Random();
    private static final LoremIpsum loremIpsum = new LoremIpsum();

    private RandomCreator() {
	// utility class, can't instantiate
    };

    public static String getRandomString() {
	String gibberish = "";
	for (int i = 0; i <= r.nextInt(3); i++) {
	    gibberish += (gibberish.length() != 0 ? " " : "") + loremIpsum.getWords(1, r.nextInt(50));
	}
	return gibberish.replaceAll("\\s", "_").replaceAll("[\\.\\,]", "");
    }

    public static boolean getRandomBoolean() {
	return r.nextBoolean();
    }

    public static int getRandomInt() {
	return r.nextInt();
    }

    public static char getRandomChar() {
	return (char) r.nextInt();
    }

    public static byte getRandomByte() {
	return (byte) r.nextInt();
    }

    public static long getRandomLong() {
	return r.nextLong();
    }

    /**
     * @return A float value between 00.00 and 100.00
     */
    public static float getRandomFloat() {
	return r.nextInt(100) + ((float) Math.round(r.nextFloat() * 100) / 100);
    }

    /**
     * @return A double value between 00.00 and 100.00
     */
    public static double getRandomDouble() {
	return r.nextInt(100) + ((double) Math.round(r.nextDouble() * 100) / 100);
    }

    public static short getRandomShort() {
	return (short) r.nextInt();
    }

    public static int getRandomInt(int max) {
	return r.nextInt(max);
    }
}