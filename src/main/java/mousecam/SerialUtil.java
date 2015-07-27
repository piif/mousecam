package mousecam;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class SerialUtil {
    @SuppressWarnings( "unchecked" )
    public static List<String> getSerialPorts() {
        List<String> serialPorts = new ArrayList<String>();
        for ( Enumeration<CommPortIdentifier> e = CommPortIdentifier.getPortIdentifiers(); e.hasMoreElements(); ) {
            CommPortIdentifier identifier = e.nextElement();
            if ( identifier.getPortType() == CommPortIdentifier.PORT_SERIAL ) {
                serialPorts.add( identifier.getName() );
            }
        }
        return serialPorts;
    }

    public static SerialPort openSerialPort( String portName, int baudRate, int databits, int stopBit, int parity ) {
        CommPortIdentifier portIdentifier;
        try {
            portIdentifier = CommPortIdentifier.getPortIdentifier( portName );
        } catch ( NoSuchPortException e ) {
            throw new IllegalArgumentException( portName + " port not found. Check that it is corect and that the board is plugged in !", e );
        }
        if ( portIdentifier.getPortType() != CommPortIdentifier.PORT_SERIAL ) {
            throw new IllegalArgumentException( portName + " is not a serial port." );
        }
        // if ( portIdentifier.isCurrentlyOwned() ) ... try anyway
        CommPort commPort;
        try {
            commPort = portIdentifier.open( SerialUtil.class.getName(), 2000 );
        } catch ( PortInUseException e ) {
            throw new IllegalArgumentException( portName + " is already in use.", e );
        }
        SerialPort serialPort = (SerialPort) commPort;
        try {
            serialPort.setSerialPortParams( baudRate, databits, stopBit, parity );
        } catch ( UnsupportedCommOperationException e ) {
            throw new RuntimeException( "Port configuration error.", e );
        }
        return serialPort;
    }
}
