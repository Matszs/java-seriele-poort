package wattGraphs;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.application.Application;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import wattGraphs.SerialPortControl.SerialPortControl;

public class Main extends Application {

	private Stage viewStage;

    @Override
    public void start(Stage stage) throws Exception{
        stage.setTitle("Energie gebruik");

		viewStage = stage;
		buildGraph();
		buildGraph();
    }

	private void buildGraph() {
		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("Tijdsduur");

		final LineChart<Number,Number> lineChart = new LineChart<Number,Number>(xAxis,yAxis);

		lineChart.setTitle("Energie gebruik");

		XYChart.Series series = new XYChart.Series();
		series.setName("Wattage");

		series.getData().add(new XYChart.Data(1, 23));
		series.getData().add(new XYChart.Data(2, 14));
		series.getData().add(new XYChart.Data(3, 15));
		series.getData().add(new XYChart.Data(4, 24));
		series.getData().add(new XYChart.Data(5, 34));
		series.getData().add(new XYChart.Data(6, 36));
		series.getData().add(new XYChart.Data(7, 22));
		series.getData().add(new XYChart.Data(8, 45));
		series.getData().add(new XYChart.Data(9, 43));
		series.getData().add(new XYChart.Data(10, 17));
		series.getData().add(new XYChart.Data(11, 29));
		series.getData().add(new XYChart.Data(12, 25));

		Scene scene  = new Scene(lineChart,800,600);
		lineChart.getData().add(series);

		viewStage.setScene(scene);
		viewStage.show();
	}

    public static void main(String[] args) {
        launch(args);
		//SerialPortControl portController = new SerialPortControl();
    }
}
