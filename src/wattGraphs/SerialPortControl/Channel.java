package wattGraphs.SerialPortControl;

import java.util.ArrayList;
import java.util.List;

/**
 * Place program description here
 *
 * @author Mats Otten
 * @project java-seriele-poort
 * @since 23-04-15
 */
public class Channel {
	private String name;
	private double value;
	private double multiplier;

	private List<Double> measurements = new ArrayList<Double>();

	public Channel(String name, double value, double multiplier) {
		this.name = name;
		this.value = value;
		this.multiplier = multiplier;
	}

	public String getName() {
		return name;
	}

	public double getValue() {
		return value;
	}

	public double calculateAmperage(int adc) {
		return (adc * multiplier);
	}

	public double getAverageWattage() {
		double totalWatt = 0;
		for(double amperage : measurements)
			totalWatt += (amperage * value);

		return (totalWatt / measurements.size());
	}

	void resetMeasurements() {
		measurements.clear();
	}

	void addMeasurement(double measurement) {
		measurements.add(measurement);
	}
}
