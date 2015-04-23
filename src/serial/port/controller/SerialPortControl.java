package serial.port.controller;

import jssc.*;


public class SerialPortControl implements Runnable {
	final String PORT = "/dev/tty.usbmodem1411";


	static SerialPort serialPort;
	Thread readThread;
	int threadTimer = 0;
	static MeasuringBoard measuringBoard;

	public static void main(String[] args) {
		SerialPortControl portController = new SerialPortControl();
	}

	public SerialPortControl() {
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

					if(buffer.length == 840)
						measuringBoard.processData(buffer);

				} catch (Exception ex) {
					System.out.println(ex);
				}
				}
			});

		} catch(Exception e) {
			System.out.println(e);
		}

		readThread = new Thread(this);
		readThread.start();
	}

	private void sendBytes() {
		try {
			if(serialPort.isOpened()) {
				if(threadTimer == 0) {
					serialPort.writeByte((byte)0x38);
				} else if(threadTimer == 50) {
					serialPort.writeByte((byte) 0x39);
				}

			}
		} catch(Exception e) {
			System.out.println(e);
		}
	}

	public void run() {
		try {
			while(true) {

				sendBytes();

				threadTimer++;
				Thread.sleep(500);
			}
		} catch (Exception e) {System.out.println(e);}
	}
}
