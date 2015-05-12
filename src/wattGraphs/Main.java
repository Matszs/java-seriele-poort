package wattGraphs;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import javafx.util.Duration;
import wattGraphs.SerialPortControl.DataReadListener;
import wattGraphs.SerialPortControl.SerialPortControl;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main extends Application {

	final static int MAX_DATA_POINTS = 20;

	private static SerialPortControl portController;
	private static XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
	static int counter = 0;
	static double totalUsage = 0; //Double.MAX_VALUE is the maximum a double (somewhere around 1.7*10^308).

	private static Text usageText = new Text("-");
	private static Text averageText = new Text("-");

	private static int timeCounter = 0;
	private static Timeline timer;





	public static void main(String[] args) {
		portController = new SerialPortControl("/dev/tty.usbmodem1411");
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

						usageText.setText("Laatste meting: " + String.format("%.2f", watt) + " watt          ");
						averageText.setText("Gemiddelde gebruik:  " + String.format("%.2f", (totalUsage / (counter + 1))) + " watt");

						counter++;
					}
				});
			}
		});
		launch(args);
	}

    @Override
    public void start(Stage stage) throws Exception{


		//Parent root = FXMLLoader.load(getClass().getResource("views/energy_usage.fxml"));

		final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("views/energy_usage.fxml"));
		Parent root = (Parent) fxmlLoader.load();



		Button captureButton = (Button)fxmlLoader.getNamespace().get("startButton");
		captureButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
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








		Scene scene = new Scene(root);
		stage.setTitle("Energiegebruik");
		stage.setScene(scene);

		stage.show();








		/*stage.setTitle("Energiegebruik");

		GridPane root = new GridPane();
		root.setPadding(new Insets(25));


		//Text on top
		Text scenetitle = new Text("Seflab :: Energiegebruik (SEF10)");
		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		scenetitle.setFill(Color.LIGHTGRAY);
		root.add(scenetitle, 0, 0, 2, 1);

		//Start/stop button
		final Button btn = new Button();
		btn.setText("Start meting");
		btn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (!portController.isStarted()) {
					portController.start();
					System.out.println("START");
					btn.setText("Stop");

				} else {
					portController.stop();
					System.out.println("STOP");
					btn.setText("Start");
				}
			}
		});
		root.add(btn, 2, 0);

		//Line chart
		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis(0, MAX_WATT, 10);
		yAxis.setLabel("Watt");
		yAxis.setForceZeroInRange(false);

		xAxis.setTickLabelsVisible(false);
		xAxis.setForceZeroInRange(false);

		final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);

		series.setName("Wattage");
		root.add(lineChart, 0, 2);
		root.setColumnSpan(lineChart, 2);
		lineChart.getData().add(series);

		//Text for last measure point
		root.add(usageText, 0, 3);

		//Text for average wattage usage
		root.add(averageText, 1, 3);

		//Set as content and display
		stage.setScene(new Scene(root, 800, 800));
		stage.show();*/
    }
}
