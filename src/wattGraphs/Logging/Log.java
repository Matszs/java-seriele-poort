package wattGraphs.Logging;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import wattGraphs.Main;

/**
 * Place program description here
 *
 * @author Mats Otten
 * @project java-seriele-poort
 * @since 02-06-15
 */
public class Log {
	public static void log(final String message) {

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if(Main.fxmlLoader != null) {
					TextArea logField = (TextArea) Main.fxmlLoader.getNamespace().get("logField");

					logField.appendText(message + "\n");

				}
			}
		});

	}
}