package mousecam;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;
import java.util.Arrays;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class PixelImagePanel extends JPanel {
	private int width = 16;
	private int height = 16;
	private Image image;
	private byte[] pixels;
	private ColorModel colorModel;

	public PixelImagePanel( int width, int height ) {
		this.width = width;
		this.height = height;
		this.pixels = new byte[width * height];
		colorModel = buildPalette();
	}

	static final int paletteDepth = 7;
	static final int paletteSize = 1 << paletteDepth;

	private ColorModel buildPalette() {
		byte[] grey = new byte[paletteSize];
		for ( int i = 0; i < paletteSize; i++ ) {
			grey[i] = (byte) (i * 256 / paletteSize);
		}
		return new IndexColorModel( paletteDepth, paletteSize, grey, grey, grey );
	}

	@Override
	protected void paintComponent( Graphics g ) {
		g.drawImage( getImage(), 0, 0, getWidth(), getHeight(), 0, 0, width, height, this );
	}

	public synchronized Image getImage() {
		if ( image == null ) {
			MemoryImageSource imageSource =
					new MemoryImageSource( width, height, colorModel, pixels, 0, width );
			imageSource.setAnimated( true );
			image = Toolkit.getDefaultToolkit().createImage( imageSource );
			image.setAccelerationPriority( 1.0f );
		}
		return image;
	}

	public void setImageData( int x0, int y0, byte[] newPixels, int newPixelsWidth, boolean reversed ) {
		// first received byte is bottom right pixel
		// second one is on top of first, coords maxX, maxY - 1 
		// => must invert and transpose data
		if ( newPixels != null ) {
			// excepted if mouse as component mounted reversed !
			// => transpose, but don't invert
			if (reversed) {
				int x = 0, y = 0;
				int dst = (y0 + y) * width + (x0 + x);
				for (int src = 0; src < newPixels.length; src++) {
					pixels[dst] = (byte) ((newPixels[src] & 0xFF) / (256 / paletteSize));
					y++;
					if (y == newPixelsWidth) {
						y = 0;
						x++;
					}
		   			dst = (y0 + y) * width + (x0 + x);
				}
			} else {
				int x = newPixelsWidth - 1, y = newPixelsWidth - 1;
				int dst = (y0 + y) * width + (x0 + x);
				for (int src = 0; src < newPixels.length; src++) {
					pixels[dst] = (byte) ((newPixels[src] & 0xFF) / (256 / paletteSize));
					y--;
					if (y < 0) {
						y = newPixelsWidth - 1;
						x--;
					}
		   			dst = (y0 + y) * width + (x0 + x);
				}
			}
	   }
		image = null;
		repaint();
	}
	
	public void clearPixels() {
		image = null;
		Arrays.fill( pixels, (byte) 0 );
	}
}
