package wattGraphs.SerialPortControl;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import jssc.*;
import wattGraphs.Logging.Log;

import javax.swing.event.EventListenerList;

/**
 * Class for controlling the connection with the serial device
 *
 * @author Mats Otten
 * @project java-seriele-poort
 * @since 23-04-15
 */
public class SerialPortControl implements Runnable {
	protected EventListenerList listenerList = new EventListenerList();
	protected String PORT = "/dev/tty.usbmodem1411"; // Because we allow to change this port, it isn't a final

	static SerialPort serialPort;
	Thread readThread;
	int threadTimer = 0;
	static MeasuringBoard measuringBoard;
	boolean isStarted = false;
	boolean debugging = false; // enable for false measuring data
	private Timeline debugTimer = null;
	public int counter = 0;

	public SerialPortControl(String port) {
		PORT = port;
	}

	public void init() {
		try {
			measuringBoard = new MeasuringBoard();
			serialPort = new SerialPort(PORT);
			serialPort.openPort();
			serialPort.setParams(9600, 8, 1, 0);

			serialPort.addEventListener(new SerialPortEventListener() {
				@Override
				public void serialEvent(SerialPortEvent event) {
					try {
						final byte buffer[] = serialPort.readBytes(event.getEventValue());

						if (buffer.length == 840 && counter == 0) {
							measuringBoard.processData(buffer);
							fireDataReadListener(measuringBoard.getWattage());
						}

						counter++;
						if(counter == 10)
							counter = 0;

					} catch (SerialPortException ex) {
						System.out.println("ERROR 1: " + ex);
						Log.log("Error: " + ex.getMessage());
					}
				}
			});

		} catch(Exception e) {
			isStarted = false;
			fireError(e.getMessage(), true);
		}

		readThread = new Thread(this);
		readThread.start();
	}

	public boolean isStarted() {
		return isStarted;
	}

	public boolean serialIsOpen() {
		return serialPort.isOpened();
	}

	public void start() {
		try {
			if(serialIsOpen()) {
				isStarted = true;
				serialPort.writeByte((byte) 0x38); // send bytes to measuringboard to start the read process
			}
		} catch(Exception e) {
			isStarted = false;
			System.out.println("ERROR 2: " + e.getMessage());
			Log.log("Error: " + e.getMessage());
		}

		if(debugging) {
			debugTimer = new Timeline(new KeyFrame(Duration.millis(100), new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					double randomNum = 0 + (double)(Math.random() * ((80 - 0) + 1));
					fireDataReadListener(randomNum);
				}
			}));
			debugTimer.setCycleCount(1000);
			debugTimer.play();
		}
	}

	public void stop() {
		try {
			if(serialIsOpen() && isStarted())
				serialPort.writeByte((byte) 0x39); // send bytes to measuringboard to start the read process
		} catch(Exception e) {
			System.out.println("ERROR 3: " + e.getMessage());
			Log.log("Error: " + e.getMessage());
		} finally {
			isStarted = false;
		}

		if(debugging && debugTimer != null) {
			debugTimer.stop();
			debugTimer = null;
		}
	}

	public void run() {
		try {
			while(true) {
				//Thread.sleep(500);
			}
		} catch (Exception e) {System.out.println(e);}
	}

	public void addDataReadListener(DataReadListener listener) {
		listenerList.add(DataReadListener.class, listener);
	}
	public void addOnErrorListener(ErrorListener listener) {
		listenerList.add(ErrorListener.class, listener);
	}

	public void removeDataReadListener(DataReadListener listener) {
		listenerList.remove(DataReadListener.class, listener);
	}

	void fireDataReadListener(double watt) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i = i+2) {
			if (listeners[i] == DataReadListener.class) {
				((DataReadListener) listeners[i+1]).onReceivingWattage(watt);
			}
		}
	}
	void fireError(String message, boolean serialReaderStopped) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i = i+2) {
			if (listeners[i] == ErrorListener.class) {
				((ErrorListener) listeners[i+1]).onError(message, serialReaderStopped);
			}
		}
	}
}

