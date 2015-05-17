package wattGraphs;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import javafx.util.Duration;
import wattGraphs.SerialPortControl.*;
import wattGraphs.SerialPortControl.ErrorListener;


public class Main extends Application {

	final static int MAX_DATA_POINTS = 20;

	private static SerialPortControl portController = null;

	private static FXMLLoader fxmlLoader;
	private static XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
	static int counter = 0;
	static double totalUsage = 0; //Double.MAX_VALUE is the maximum a double (somewhere around 1.7*10^308).

	private static int timeCounter = 0;
	private static Timeline timer = null;
	private static TextField lastUsageField = null;

	public static void main(String[] args) {
		launch(args);
	}

	public static void startMeasuring() {
		portController = new SerialPortControl(Configuration.getString("port"));
		portController.addDataReadListener(new DataReadListener() {
			@Override
			public void onReceivingWattage(final double watt) {

				// Run changes in UI thread
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						series.getData().add(new XYChart.Data(counter, (int) watt));
						totalUsage += watt;

						if (series.getData().size() > MAX_DATA_POINTS) {
							series.getData().remove(0, series.getData().size() - MAX_DATA_POINTS);
						}

						if(lastUsageField != null)
							lastUsageField.setText(String.format("%.2f", watt) + " watt");
						//averageText.setText("Gemiddelde gebruik:  " + String.format("%.2f", (totalUsage / (counter + 1))) + " watt");

						counter++;
					}
				});
			}
		});
		portController.addOnErrorListener(new ErrorListener() {
			@Override
			public void onError(String message, boolean serialReaderStopped) {

				TextField timeMinutes = (TextField)fxmlLoader.getNamespace().get("timeMinutes");
				TextField timeSeconds = (TextField)fxmlLoader.getNamespace().get("timeSeconds");

				timeMinutes.setDisable(false);
				timeSeconds.setDisable(false);

				timeMinutes.setText("0");
				timeSeconds.setText("0");

				timeCounter = 0;
				if(timer != null)
					timer.stop();

				if(portController.isStarted())
					portController.stop();
				System.out.println("STOPPED BY ERROR: " + message);

			}
		});
		portController.init();
	}

    @Override
    public void start(Stage stage) throws Exception{
		fxmlLoader = new FXMLLoader(getClass().getResource("views/energy_usage.fxml"));
		Parent root = (Parent) fxmlLoader.load();

		lastUsageField = (TextField)fxmlLoader.getNamespace().get("lastMeasuring");

		Button captureButton = (Button)fxmlLoader.getNamespace().get("startButton");
		captureButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				if(portController == null)
					startMeasuring();

				if (!portController.isStarted()) {
					final TextField timeMinutes = (TextField)fxmlLoader.getNamespace().get("timeMinutes");
					final TextField timeSeconds = (TextField)fxmlLoader.getNamespace().get("timeSeconds");

					timeMinutes.setDisable(true);
					timeSeconds.setDisable(true);

					timeCounter = (Integer.parseInt(timeMinutes.getText()) * 60) + Integer.parseInt(timeSeconds.getText());

					timer = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {

						@Override
						public void handle(ActionEvent event) {

							timeCounter--;
							if(timeCounter <= 0) {

								timeMinutes.setDisable(false);
								timeSeconds.setDisable(false);

								timeCounter = 0;
								portController.stop();
								System.out.println("STOP");

							}

							int minutes = (timeCounter / 60);
							timeMinutes.setText(String.valueOf(minutes));
							timeSeconds.setText(String.valueOf(timeCounter - (minutes * 60)));

						}

					}));

					timer.setCycleCount(timeCounter);
					timer.play();


					portController.start();
					System.out.println("START");

				} else {
					TextField timeMinutes = (TextField)fxmlLoader.getNamespace().get("timeMinutes");
					TextField timeSeconds = (TextField)fxmlLoader.getNamespace().get("timeSeconds");

					timeMinutes.setDisable(false);
					timeSeconds.setDisable(false);

					timeMinutes.setText("0");
					timeSeconds.setText("0");

					timeCounter = 0;
					timer.stop();

					portController.stop();
					System.out.println("STOP");
				}
			}
		});

		LineChart energyLineChart = (LineChart)fxmlLoader.getNamespace().get("EnergyLineChart");
		energyLineChart.getData().add(series);

		final TextField port = (TextField)fxmlLoader.getNamespace().get("settingPort");
		final TextField dbHost = (TextField)fxmlLoader.getNamespace().get("settingDbHost");
		final TextField dbUsername = (TextField)fxmlLoader.getNamespace().get("settingDbUser");
		final TextField dbPassword = (TextField)fxmlLoader.getNamespace().get("settingDbPassword");
		final TextField dbDatabase = (TextField)fxmlLoader.getNamespace().get("settingDbDatabase");

		Button saveSettingsButton = (Button)fxmlLoader.getNamespace().get("settingSave");
		saveSettingsButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				Configuration.write("port", port.getText());
				Configuration.write("db_host", dbHost.getText());
				Configuration.write("db_username", dbUsername.getText());
				Configuration.write("db_password", dbPassword.getText());
				Configuration.write("db_database", dbDatabase.getText());
			}
		});

		port.setText(Configuration.getString("port"));
		dbHost.setText(Configuration.getString("db_host"));
		dbUsername.setText(Configuration.getString("db_username"));
		dbPassword.setText(Configuration.getString("db_password"));
		dbDatabase.setText(Configuration.getString("db_database"));



		Scene scene = new Scene(root);
		stage.setTitle("Energiegebruik");
		stage.setScene(scene);

		stage.show();
    }
}
