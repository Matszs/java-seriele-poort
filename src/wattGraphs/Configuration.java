package wattGraphs;

import java.io.*;
import java.util.Scanner;

/**
 * Place program description here
 *
 * @author Mats Otten
 * @project java-seriele-poort
 * @since 15-05-15
 */
public class Configuration {

	public static String getString(String key) {
		try {
			File file = new File("config.properties");
			file.createNewFile();
			Scanner in = new Scanner(new FileReader(file));

			while (in.hasNext()) {
				String rule = in.next();
				if(rule.length() == 0)
					continue;

				String[] keyValue = rule.split("=", 2);
				if(keyValue[0].equals(key))
					return keyValue[1];
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static void write(String key, String value) {
		try {
			File file = new File("config.properties");
			file.createNewFile();
			Scanner in = new Scanner(new FileReader(file));
			boolean keyFound = false;
			String settings = "";

			while (in.hasNext()) {
				String rule = in.next();
				if(rule.length() == 0)
					continue;

				String[] keyValue = rule.split("=", 2);
				if(keyValue[0].equals(key)) {
					settings += key + "=" + (value != null ? value : "") + "\n";
					keyFound = true;
				} else {
					settings += rule + "\n";
				}
			}

			if(!keyFound)
				settings += key + "=" + (value != null ? value : "") + "\n";

			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.write(settings);
			output.close();

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
