package mousecam;

import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;

public class UpdateThread extends Thread {
    private final MouseCamWindow imageFromData;
    private SerialPort serialPort;

    public UpdateThread( SerialPort serialPort, MouseCamWindow imageFromData ) {
        this.imageFromData = imageFromData;
        this.serialPort = serialPort;
    }

    @Override
    public void run() {
        try {
            InputStream in = serialPort.getInputStream();
            SerialReader reader = new SerialReader( in );
            int x = 0, y = 0;
            while ( true ) {
                String line = reader.readLine();
                if ( line.startsWith( "DELTA:" ) ) {
                    String[] delta = line.substring( 6 ).split( " " );
                    if ( delta.length == 2 ) {
                        x += Integer.parseInt( delta[0] );
                        y -= Integer.parseInt( delta[1] );
                        if ( x < 0 )
                            x = 0;
                        if ( y < 0 )
                            y = 0;
                    }
                }
                if ( line.startsWith( "FRAME:" ) ) {
                    imageFromData.setImageData( x, y, parseStringData( line.substring( 6 ) ) );
                }
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }

    }

    private static byte[] parseStringData( String data ) {
        int length = data.length() / 2;
        byte[] pix = new byte[length];
        for ( int i = 0; i < length; i++ ) {
            try {
                pix[i] = Integer.valueOf( data.substring( i * 2, i * 2 + 2 ), 16 ).byteValue();
            } catch ( NumberFormatException e ) {
                pix[i] = 0;
            }

        }
        return pix;
    }

    public static class SerialReader {
        InputStream in;

        public SerialReader( InputStream in ) {
            this.in = in;
        }

        public String readLine() throws IOException {
            StringBuilder lineBuilder = new StringBuilder();
            // Pourquoi in.read() n'est pas bloquant comme l'indique la javadoc (apparement timeout de quelques ms) ?
            int data;
            while ( (data = in.read()) != '\n' ) {
                if ( data != -1 )
                    lineBuilder.append( (char) data );
            }
            return lineBuilder.toString().trim();
        }
    }

}
