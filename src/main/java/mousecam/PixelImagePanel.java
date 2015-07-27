package mousecam;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;
import java.util.Arrays;

import javax.swing.JPanel;

public class PixelImagePanel extends JPanel {
    private int width = 16;
    private int height = 16;
    private Image image;
    private MemoryImageSource imageSource;
    private byte[] pixels;
    private ColorModel colorModel;

    public PixelImagePanel( int width, int height ) {
        this.width = width;
        this.height = height;
        colorModel = buildPalette();
    }

    private ColorModel buildPalette() {
        byte[] grey = new byte[64];
        for ( int i = 0; i < 64; i++ ) {
            grey[i] = (byte) (i * 4);
        }
        return new IndexColorModel( 6, 64, grey, grey, grey );
    }

    @Override
    protected void paintComponent( Graphics g ) {
        g.drawImage( getImage(), 0, 0, getWidth(), getHeight(), 0, 0, width, height, this );
    }

    public synchronized Image getImage() {
        if ( image == null ) {
            image = Toolkit.getDefaultToolkit().createImage( getImageSource() );
            image.setAccelerationPriority( 1.0f );
        }
        return image;
    }

    private MemoryImageSource getImageSource() {
        imageSource = new MemoryImageSource( width, height, colorModel, getPixels(), 0, width );
        imageSource.setAnimated( true );
        return imageSource;
    }

    private byte[] getPixels() {
        if ( pixels == null ) {
            pixels = new byte[width * height];
            Arrays.fill( pixels, (byte) 0 );
        }
        return pixels;
    }

    public void setImageData( int x, int y, byte[] newPixels, int newPixelsWidth ) {
        if ( newPixels != null ) {
            for ( int i = 0; i < newPixels.length; i++ ) {
                int trans = 0xFF-((i&0x0F)<<4)-((i&0xF0)>>4); // Do a little transposition to go from chip pixel order to standard pixel order.
                int destIndex = x + trans % newPixelsWidth + (y + trans / newPixelsWidth) * width;
                if ( destIndex < getPixels().length  )
                getPixels()[destIndex] = newPixels[i];
            }
        }
        image = null;
        getImageSource().newPixels();
        repaint();
    }
    
    public void clearPixels() {
        image = null;
        Arrays.fill( pixels, (byte) 0 );
    }
}
