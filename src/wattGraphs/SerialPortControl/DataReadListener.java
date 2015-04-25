package wattGraphs.SerialPortControl;

/**
 * Created by Mats on 25-4-2015.
 */
import java.util.EventListener;
import java.util.EventObject;

public interface DataReadListener extends EventListener {
	void onReceivingWattage(double watt);
}
