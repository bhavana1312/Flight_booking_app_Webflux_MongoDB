package com.flightapp.util;

import java.security.SecureRandom;

public class PnrGenerator {
	private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private static final SecureRandom rnd = new SecureRandom();

	public static String gen() {
		StringBuilder sb = new StringBuilder(6);
		for (int i = 0; i < 6; i++)
			sb.append(CHARS.charAt(rnd.nextInt(CHARS.length())));
		return sb.toString();
	}
}
