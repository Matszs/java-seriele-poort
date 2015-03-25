package serial.port.controller;

import java.io.*;
import java.util.*;
import javax.comm.*;

public class SerialPortControl implements Runnable {
	private static final String PORT_ID = "COM3"; // Linux: /dev/term/a

	static CommPortIdentifier portId;
	static Enumeration portList;

	InputStream inputStream;
	OutputStream outputStream;
	SerialPort serialPort;
	Thread readThread;


    public static void main(String[] args) {
		System.out.println("Starting application...");
		portList = CommPortIdentifier.getPortIdentifiers();

		while (portList.hasMoreElements()) {
			portId = (CommPortIdentifier) portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (portId.getName().equals(PORT_ID)) {
					SerialPortControl reader = new SerialPortControl();
				}
			}
		}
    }

	public SerialPortControl() {
		try {
			serialPort = (SerialPort) portId.open("SerialPortControlApp", 2000);
		}
		catch (PortInUseException e) {
			System.out.println(e);
		}


		try {
			inputStream = serialPort.getInputStream();
		}
		catch (IOException e) {
			System.out.println(e);
		}

		try {
			outputStream = serialPort.getOutputStream();
		}
		catch (IOException e) {
			System.out.println(e);
		}

		try {
			outputStream.write(0x31);
		}
		catch (IOException e) {
			System.out.println(e);
		}

		try {
			serialPort.addEventListener(new SerialPortEventListener() {
				public void serialEvent(SerialPortEvent event) {
					if(event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {

						byte[] readBuffer = new byte[20];

						try {
							while (inputStream.available() > 0) {
								int numBytes = inputStream.read(readBuffer);
							}
							System.out.print(new String(readBuffer));

							// Todo: Handle response


						}
						catch (IOException e) {
							System.out.println(e);
						}

					}
				}
			});
		} catch (TooManyListenersException e) {
			System.out.println(e);
		}


		serialPort.notifyOnDataAvailable(true);


		try {
			serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		}
		catch (UnsupportedCommOperationException e) {
			System.out.println(e);
		}


		readThread = new Thread(this);
		readThread.start();
	}

	public void run() {
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {System.out.println(e);}
	}





}
