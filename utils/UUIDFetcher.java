package me.ghost.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.UUID;

public class UUIDFetcher {

	public static UUID getUUIDOf(String playername) {
		String output = callURL("https://api.mojang.com/users/profiles/minecraft/" + playername);

		StringBuilder result = new StringBuilder();

		readData(output, result);
		if (output != null) {
			return getUUID(result.toString());
		}
		return null;
	}

	private static void readData(String toRead, StringBuilder result) {
		int i = 7;

		while (i < 200) {
			if (!String.valueOf(toRead.charAt(i)).equalsIgnoreCase("\"")) {

				result.append(String.valueOf(toRead.charAt(i)));

			} else {
				break;
			}

			i++;
		}
	}

	private static String callURL(String URL) {
		StringBuilder sb = new StringBuilder();
		URLConnection urlConn = null;
		InputStreamReader in = null;
		try {
			URL url = new URL(URL);
			urlConn = url.openConnection();

			if (urlConn != null)
				urlConn.setReadTimeout(60 * 1000);

			if (urlConn != null && urlConn.getInputStream() != null) {
				in = new InputStreamReader(urlConn.getInputStream(), Charset.defaultCharset());
				BufferedReader bufferedReader = new BufferedReader(in);

				if (bufferedReader != null) {
					int cp;

					while ((cp = bufferedReader.read()) != -1) {
						sb.append((char) cp);
					}

					bufferedReader.close();
				}
			}

			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

	public static UUID getUUID(String id) {
		return UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-"
				+ id.substring(16, 20) + "-" + id.substring(20, 32));
	}

}