// TODO : add RGB LED buttons (sliders ?) + protocol to arduino (L prefix + 3*2 hex digits for r,g,b)

package mousecam;

import gnu.io.SerialPort;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MouseCam {
    private static SerialPort serialPort;

    static char toHex(byte value) {
    	return (char)(value > 9 ? 'a' - 10 + value : value + '0');
    }

    public static class LedPilot {
    	void set(short r, short g, short b) throws IOException {
    		OutputStream out = serialPort.getOutputStream();
    		out.write('L');
        	out.write(toHex((byte)(r >> 4)));
        	out.write(toHex((byte)(r & 0x0f)));
        	out.write(toHex((byte)(g >> 4)));
        	out.write(toHex((byte)(g & 0x0f)));
        	out.write(toHex((byte)(b >> 4)));
        	out.write(toHex((byte)(b & 0x0f)));
    	}
    }

    public static void main( String[] args ) throws Exception {
    	LedPilot ledPilot = new LedPilot();

        final MouseCamWindow frame = new MouseCamWindow(ledPilot);

        List<String> ports = SerialUtil.getSerialPorts();
        for (String a : args) {
        	ports.add(0, a);
        }
        frame.setSerialPortsList( ports );

        frame.getConnectButton().addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    MouseCam.createUpdateThread( frame, frame.getSelectedPort() );
                    frame.getConnectButton().setEnabled( false ); // Can't disconnect yet ... ;)
                } catch ( IllegalArgumentException ex ) {
                    JOptionPane.showMessageDialog( frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
                }
            }
        } );

        frame.getClearButton().addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                frame.getScanImagePanel().clearPixels();
                frame.getScanImagePanel().repaint();
            }
        } );

        frame.getSaveButton().addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                JFileChooser fc = new JFileChooser();
                int returnVal = fc.showSaveDialog( frame );
                if ( returnVal == JFileChooser.APPROVE_OPTION ) {
                    Image image = frame.getScanImagePanel().getImage();
                    BufferedImage bufferedImage = new BufferedImage( image.getWidth( null ), image.getHeight( null ), BufferedImage.TYPE_INT_RGB );
                    Graphics2D g2d = bufferedImage.createGraphics();
                    g2d.drawImage( image, 0, 0, null );
                    g2d.dispose();
                    try {
                        ImageIO.write( bufferedImage, "png", fc.getSelectedFile() );
                    } catch ( IOException ex ) {
                        JOptionPane.showMessageDialog( frame, "Error while saving image : " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
                    }
                }
            }
        } );

        frame.getSlider().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                JSlider source = (JSlider) e.getSource();
                if ( !source.getValueIsAdjusting() ) {
                    if ( serialPort != null ) {
                        byte value = (byte)source.getValue();
                        try {
                        	// TODO : prefix by W
                        	serialPort.getOutputStream().write('W');
                            serialPort.getOutputStream().write(toHex(value));
                        } catch ( IOException e1 ) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        } );

        frame.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing( WindowEvent e ) {
                System.exit( 0 );
            }
        } );

        frame.setVisible( true );

    }

    static void createUpdateThread( MouseCamWindow frame, String selectedPort ) {
        serialPort = SerialUtil.openSerialPort( selectedPort, 115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE );
        final UpdateThread updateThread = new UpdateThread( serialPort, frame );
        updateThread.start();
    }

}
