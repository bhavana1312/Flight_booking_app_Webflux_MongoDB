package com.flightapp.util;

import java.util.UUID;

public class PnrGenerator {
	private PnrGenerator() {
	}

	public static String generate() {
		return UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
	}
}
