package mousecam;

import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;

public class UpdateThread extends Thread {
	private final MouseCamWindow imageFromData;
	private SerialPort serialPort;

	public UpdateThread(SerialPort serialPort, MouseCamWindow imageFromData) {
		this.imageFromData = imageFromData;
		this.serialPort = serialPort;
	}

	@Override
	public void run() {
		try {
			InputStream in = serialPort.getInputStream();
			SerialReader reader = new SerialReader(in);
			int x = 1, y = 1;
//			int x = MouseCamWindow.VIEW_SIZE_X / 2,
//				y = MouseCamWindow.VIEW_SIZE_Y / 2;
			while (true) {
				String line = reader.readLine();
				// System.out.println(line);
				if (line.startsWith("DELTA:")) {
					String[] delta = line.substring(6).split(" ");
					if (delta.length == 2) {
						if (MouseCamWindow.REVERSED) {
							x -= (byte) Integer.parseInt(delta[0]);
							y += (byte) Integer.parseInt(delta[1]);
						} else {
							x += (byte) Integer.parseInt(delta[0]);
							y -= (byte) Integer.parseInt(delta[1]);
						}
						if (x < 0) {
							x = 0;
						} else if (x > MouseCamWindow.VIEW_SIZE_X
								- MouseCamWindow.CAM_SIZE) {
							x = MouseCamWindow.VIEW_SIZE_X
									- MouseCamWindow.CAM_SIZE - 1;
						}
						if (y < 0) {
							y = 0;
						} else if (y > MouseCamWindow.VIEW_SIZE_Y
								- MouseCamWindow.CAM_SIZE) {
							y = MouseCamWindow.VIEW_SIZE_Y
									- MouseCamWindow.CAM_SIZE - 1;
						}
					}
				} else if (line.startsWith("FRAME:")) {
					imageFromData.setImageData(x, y,
							parseStringData(line.substring(6)));
				} else {
					System.out.println(line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static byte[] parseStringData(String data) {
		int length = data.length() / 2;
		byte[] pix = new byte[length];
		for (int i = 0; i < length; i++) {
			try {
				pix[i] = Integer.valueOf(data.substring(i * 2, i * 2 + 2), 16)
						.byteValue();
			} catch (NumberFormatException e) {
				pix[i] = 0;
			}

		}
		return pix;
	}

	public static class SerialReader {
		InputStream in;

		public SerialReader(InputStream in) {
			this.in = in;
		}

		public String readLine() throws IOException {
			StringBuilder lineBuilder = new StringBuilder();
			// Pourquoi in.read() n'est pas bloquant comme l'indique la javadoc
			// (apparement timeout de quelques ms) ?
			int data;
			while ((data = in.read()) != '\n') {
				if (data != -1)
					lineBuilder.append((char) data);
			}
			return lineBuilder.toString().trim();
		}
	}

}
