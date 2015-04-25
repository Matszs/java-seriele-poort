package wattGraphs.SerialPortControl;

import jssc.*;

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

	public SerialPortControl(String port) {
		PORT = port;
		init();
	}

	public SerialPortControl() {
		init();
	}

	private void init() {
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

						if (buffer.length == 840) {
							measuringBoard.processData(buffer);
							fireMyEvent(measuringBoard.getWattage());
						}

					} catch (SerialPortException ex) {
						System.out.println("ERROR 1: " + ex);
					}
				}
			});

		} catch(Exception e) {
			isStarted = false;
			System.out.println(e);
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
			isStarted = true;
			serialPort.writeByte((byte) 0x38); // send bytes to measuringboard to start the read process
		} catch(Exception e) {
			isStarted = false;
			System.out.println("ERROR 2: " + e.getMessage());
		}
	}

	public void stop() {
		try {
			serialPort.writeByte((byte) 0x39); // send bytes to measuringboard to start the read process
		} catch(Exception e) {
			System.out.println("ERROR 3: " + e.getMessage());
		} finally {
			isStarted = false;
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

	public void removeDataReadListener(DataReadListener listener) {
		listenerList.remove(DataReadListener.class, listener);
	}

	void fireMyEvent(double watt) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i = i+2) {
			if (listeners[i] == DataReadListener.class) {
				((DataReadListener) listeners[i+1]).onReceivingWattage(watt);
			}
		}
	}
}

