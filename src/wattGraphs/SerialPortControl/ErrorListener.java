package wattGraphs.SerialPortControl;

import java.util.EventListener;

/**
 * Place program description here
 *
 * @author Mats Otten
 * @project java-seriele-poort
 * @since 17-05-15
 */
public interface ErrorListener extends EventListener {
	void onError(String message, boolean serialReaderStopped);
}
