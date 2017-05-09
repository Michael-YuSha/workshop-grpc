package io.grpc.examples.translate;

import java.util.HashMap;
import java.util.Map;

public class Repo {

	private static Map<String, byte[]> dataMap = new HashMap<>();
			
	private Repo() {}

	public static void save(final String key, final byte[] data) {
		dataMap.put(key, data);
	}
	
	public static byte[] retrieve(final String key) {
		return dataMap.get(key);
	}
}
