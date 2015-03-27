package serial.port.controller;

import jssc.*;

import java.io.*;
import java.util.*;


public class SerialPortControl implements Runnable {
	final String PORT = "/dev/tty.usbmodem1411";


	static SerialPort serialPort;
	Thread readThread;
	int[] byteData = {0x31, 0x32, 0x33};
	int currentDataType = 0;

	public static void main(String[] args) {
		SerialPortControl portController = new SerialPortControl();
	}

	public SerialPortControl() {
		try {
			serialPort = new SerialPort(PORT);
			serialPort.openPort();
			serialPort.setParams(9600, 8, 1, 0);

			serialPort.addEventListener(new SerialPortEventListener() {
				@Override
				public void serialEvent(SerialPortEvent event) {

					if (event.isRXCHAR()) {
						try {
							/*final byte buffer[] = serialPort.readBytes(event.getEventValue());
							final String readed = new String(buffer);


							System.out.println("length: " + buffer.length);

							for(int i = 0; i < buffer.length; i++) {
								System.out.println(Integer.toHexString(buffer[i]));
							}


							System.out.println(readed + "(" + event.getEventValue() + ")");
							System.out.println("----------------------------------");*/

							final byte buffer[] = serialPort.readBytes(event.getEventValue());
							final String data = new String(buffer);

							String formattedData = data.substring(0, data.length() - 2);
							char type = formattedData.charAt(formattedData.length() - 1);
							double tempvar;


							// fake constants

							double currentref = 3.3;
							double currentampresolution = 4096; //12-bit
							double currentampgain = 50;
							double shuntresistor = 0.002;
							double Vin_R1 = 99200;
							double Vin_R2 = 9900;
							double vref = 3.3 / 1.6;
							double adcresolution = 4096;
							double therm_r = 6490;
							double therm_rnom = 10000;
							double therm_beta = 3428;

							switch (type) {
								case 'C': // current


									tempvar = Double.parseDouble(formattedData.substring(0, formattedData.length() - 1));
									tempvar = currentref * (tempvar / currentampresolution) / (currentampgain * shuntresistor); //v = i*r, i=v/r

									System.out.println(tempvar + " A");


									break;
								case 'V': // voltage

									tempvar = Double.parseDouble(formattedData.substring(0, formattedData.length() - 1));
									if (tempvar >= 61440) {
										tempvar -= 65535;
									}

									tempvar *= 2;

									tempvar = vref * (tempvar / adcresolution) * ((Vin_R1 + Vin_R2) / Vin_R2);
									System.out.println(tempvar + " V");

									break;
								case 'T': // temperature

									tempvar = Double.parseDouble(formattedData.substring(0, formattedData.length() - 1));
									if (tempvar >= 61440) {
										tempvar -= 65535;
									}

									tempvar *= 2;

									tempvar = tempvar * vref / adcresolution; //voltage on pin

									double Rtherm = tempvar * therm_r / (currentref - tempvar); //current resistance of thermistor
									double Ttherm = 1 / ((1 / 298.15) - Math.log(therm_rnom / Rtherm) / therm_beta);
									Ttherm -= 273.15;

									System.out.println(tempvar + " deg C");

									break;
							}


						} catch (Exception ex) {
							System.out.println(ex);
						}
					}

				}
			});

			//serialPort.writeByte((byte)0x31); // current
			//serialPort.writeByte((byte)0x32); // voltage
			//serialPort.writeByte((byte)0x33); // temperature

			//serialPort.writeByte((byte)0x38); // start
			//serialPort.writeByte((byte)0x39); // end



		} catch(Exception e) {
			System.out.println(e);
		}

		readThread = new Thread(this);
		readThread.start();
	}

	private void sendBytes() {
		try {
			if(serialPort.isOpened()) {
				serialPort.writeByte((byte) byteData[currentDataType]);
				currentDataType = (((currentDataType + 1) < byteData.length) ? (currentDataType + 1) : 0);
			}
		} catch(Exception e) {
			System.out.println(e);
		}
	}

	public void run() {
		try {
			while(true) {

				sendBytes();

				Thread.sleep(3000);
			}
		} catch (Exception e) {System.out.println(e);}
	}
}
