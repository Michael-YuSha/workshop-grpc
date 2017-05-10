package io.grpc.examples.translate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

public class Utils {

	private Utils() {
	}

	public static int askAction() {
		System.out.println("0 : Quit");
		System.out.println("1 : Simple blocking 1 line translate request");
		System.out.println("2 : Multi line translate request");
		System.out.println("3 : Retrieve translated multi lines");
		System.out.println("4 : Async translate chat");
		String s = ask("Welke actie wil je uitvoeren ? : ");
		return new Integer(s).intValue();
	}

	public static Language askLanguage() {
		return toLanguage(ask("Welke taal [UK,FR,DE,IT,ES] : "));
	}

	public static String askLineToTranslate() {
		return ask("line (''=quit) : ");
	}

	public static String translate(final String lang, final String line) {
		
		
		return lang + " " + line + " " + lang;
	}
	
	public static String uuid() {
		return UUID.randomUUID().toString();
	}
	
	private static String ask(final String question) {
		System.out.println(question);
		try {
			BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
			return bufferRead.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}	
	}
	
	public static Language toLanguage(final String lang) {
		return Language.valueOf(lang.toUpperCase());
	}
}
