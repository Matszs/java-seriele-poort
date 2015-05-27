package serial.port.controller;

/**
 * Place program description here
 *
 * @author Mats Otten
 * @project java-seriele-poort
 * @since 22-04-15
 *
 *
 *
 *

//serialPort.writeByte((byte)0x31); // current
//serialPort.writeByte((byte)0x32); // voltage
//serialPort.writeByte((byte)0x33); // temperature

//serialPort.writeByte((byte)0x38); // start
//serialPort.writeByte((byte)0x39); // end
 *
 *
 */
public class MeasuringBoard {

	//  constant to calculate 5v and 3.3v currents (2 5mohm resistors)
	final static double multiplier1 = 0.00322;
	//  constant to calculate other currents (3x 2 mohm resistors)
	final static double multiplier2 = 0.00806;

	Channel[] channels = {
		null,
		null,
		null,
		new Channel("ATX12", 12, multiplier2),
		new Channel("3.3V", 3.3, multiplier1),
		new Channel("5V", 5, multiplier1),
		new Channel("5Vsb", 5, multiplier1),
		new Channel("12V  ", 12, multiplier2),
	};


	public MeasuringBoard() {

	}

	public void processData(byte[] buffer) {


		/*
		// Debugging data
		for (int i = 0; i < 840; i++) {
			System.out.print(String.format("%02X", (0xFF & buffer[i])) + " ");
		}*/



		double totalWattage = 0;

		for (int channelIndex = 3; channelIndex <= 7; channelIndex++) {
			Channel channel = channels[channelIndex];

			/*if(channel.getName() == "ATX12" || channel.getName() == "12V  ")
				System.out.print("Channel (" + channel.getName() + "): ");*/
			double total = 0;
			for (int i = 0; i < 50; i++) { // first 50 samples, each 8 16-bit words=50*8*2=800 bytes (400 16-bit words)
				int bix = 2 * (i * 8 + channelIndex); // buffer byte index
				int adc = bytesToWord(buffer, bix);

				total += channel.calculateAmperage(adc);

				channel.addMeasurement(channel.calculateAmperage(adc));
			}

			//System.out.println((total / 50));

			double watt = channel.getAverageWattage();
			channel.resetMeasurements();

			/*if(channel.getName() == "ATX12" || channel.getName() == "12V  ")
				System.out.println("Wattage: " + watt);*/
			totalWattage += watt;
		}


		System.out.println("Total wattage: " + totalWattage);
	}

	public double getVoltage() {
		return 0.0;
	}

	public double getAmperage() {
		return 0.0;
	}

	public double getWattage() {
		return  0.0;
	}

	/*
		Method for converting bytes to 'words' from Jan Derriks
	 */
	private int bytesToWord(byte[] buffer, int bix) {
		return (((buffer[bix]) << 8) | buffer[bix + 1] & 0xFF) & 0xffff; // bigendian ABCD= AB CD
		//return (((buffer[bix + 1]) << 8) | buffer[bix] & 0xFF) & 0xffff; // little endian ABCD= CD AB
	}
}
