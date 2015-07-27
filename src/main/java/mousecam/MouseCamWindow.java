package mousecam;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;

@SuppressWarnings("serial")
public class MouseCamWindow extends JFrame {

	public static final boolean REVERSED = true;
	public static final int VIEW_SIZE_X = 900;
	public static final int VIEW_SIZE_Y = 900;
	public static final int CAM_SIZE = 15;

    private PixelImagePanel currentImagePanel;
    private PixelImagePanel scanImagePanel;
    @SuppressWarnings("rawtypes")
	private JComboBox portsList;
    private JButton connectButton;
    private JSlider slider;
    private JButton clearButton;
    private JButton saveButton;

    public MouseCamWindow() {
        initLayout();
    }

    public void setImageData( int x, int y, byte[] pixels ) {
        currentImagePanel.clearPixels();
        currentImagePanel.setImageData( 0, 0, pixels, CAM_SIZE, REVERSED);
        scanImagePanel.setImageData( x, y, pixels, CAM_SIZE, REVERSED );
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public void setSerialPortsList( List<String> serialPorts ) {
        portsList.setModel( new DefaultComboBoxModel( serialPorts.toArray() ) );
    }

    public String getSelectedPort() {
        return (String) portsList.getSelectedItem();
    }

    public JButton getConnectButton() {
        return connectButton;
    }

    public JSlider getSlider() {
        return slider;
    }

    public JButton getClearButton() {
        return clearButton;
    }

    public JButton getSaveButton() {
        return saveButton;
    }

    public PixelImagePanel getScanImagePanel() {
        return scanImagePanel;
    }

    @SuppressWarnings("rawtypes")
	private void initLayout() {
        setTitle( "Mouse cam" );
        setLayout( new GridBagLayout() );

        JPanel portsListPanel = new JPanel( new BorderLayout( 5, 0 ) );
        portsListPanel.setBorder( new TitledBorder( "Serial port" ) );
        portsList = new JComboBox();
        portsListPanel.add( portsList, BorderLayout.CENTER );
        connectButton = new JButton( "Connect" );
        portsListPanel.add( connectButton, BorderLayout.EAST );

        getContentPane().add( portsListPanel, new GridBagConstraints( 0, 0, 1, 1, 0, 0, GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0 ), 0, 0 ) );

        JPanel miscPanel = new JPanel();
        miscPanel.setLayout( new BoxLayout( miscPanel, BoxLayout.LINE_AXIS ) );
        miscPanel.setBorder( new TitledBorder( "Scan area" ) );
        clearButton = new JButton( "Clear" );
        miscPanel.add( clearButton );
        miscPanel.add( Box.createRigidArea( new Dimension( 5, 0 ) ) );
        saveButton = new JButton( "Save PNG" );
        miscPanel.add( saveButton );

        getContentPane().add( miscPanel, new GridBagConstraints( 0, 1, 1, 1, 0, 0, GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0 ), 0, 0 ) );

        JPanel sliderPanel = new JPanel( new BorderLayout( 5, 0 ) );
        sliderPanel.setBorder( new TitledBorder( "Frame dump width" ) );
        slider = new JSlider( 0, CAM_SIZE );
        slider.setMinorTickSpacing( 1 );
        slider.setSnapToTicks( true );
        slider.setPaintTicks( true );
        slider.setValue( 0 );
        sliderPanel.add( slider, BorderLayout.CENTER );

        getContentPane().add( sliderPanel, new GridBagConstraints( 0, 2, 1, 1, 0, 0, GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0 ), 0, 0 ) );

        currentImagePanel = new PixelImagePanel( CAM_SIZE, CAM_SIZE );
        currentImagePanel.setPreferredSize( new Dimension( 225, 225 ) );
        JPanel currentPanel = new JPanel();
        currentPanel.setBorder( new TitledBorder( "Current sensor data" ) );
        currentPanel.add( currentImagePanel );

        getContentPane().add( currentPanel, new GridBagConstraints( 0, 3, 1, 1, 0, 0, GridBagConstraints.PAGE_START, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 ) );

        scanImagePanel = new PixelImagePanel( VIEW_SIZE_X, VIEW_SIZE_Y );
        scanImagePanel.setPreferredSize( new Dimension( VIEW_SIZE_X, VIEW_SIZE_Y ) );
        JPanel scanPanel = new JPanel( new BorderLayout() );
        scanPanel.setBorder( new TitledBorder( "Scan area" ) );
        scanPanel.add( scanImagePanel );

        getContentPane().add( scanPanel, new GridBagConstraints( 1, 0, 1, 4, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
        pack();
    }

}
