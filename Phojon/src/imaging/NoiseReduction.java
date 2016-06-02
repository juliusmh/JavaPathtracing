package imaging;

import java.awt.image.BufferedImage;

public class NoiseReduction {

	public NoiseReduction(){}
	
	public BufferedImage filter(BufferedImage b){
		try {
			int[] inPixels = new int[b.getWidth() * b.getHeight()];
			
			int n = 0;
			
			for (int x = 0; x < b.getWidth(); x++){
			
				for (int y = 0; y < b.getHeight(); y++){
					
					inPixels[n] = b.getRGB(x, y);
					n++;					
				}
				
			}
			
			NoiseReduction noise = new NoiseReduction();
			
			int[] outPixels = noise.filterPixels(b.getWidth(), b.getHeight(), inPixels);
			
			n = 0;
			
			for (int x = 0; x < b.getWidth(); x++){
				
				for (int y = 0; y < b.getHeight(); y++){
					
					b.setRGB(x, y, outPixels[n]);
					n++;					
				}
				
			}
			
			return b;
			
		} catch (Exception e) {}
		return null;
	}
	
	private int smooth(int[] v) {
		int minindex = 0, maxindex = 0, min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
		
		for (int i = 0; i < 9; i++) {
			if ( i != 4 ) {
				if (v[i] < min) {
					min = v[i];
					minindex = i;
				}
				if (v[i] > max) {
					max = v[i];
					maxindex = i;
				}
			}
		}
		if ( v[4] < min )
			return v[minindex];
		if ( v[4] > max )
			return v[maxindex];
		return v[4];
	}

	public int[] filterPixels( int width, int height, int[] inPixels) {
		int index = 0;
		int[] r = new int[9];
		int[] g = new int[9];
		int[] b = new int[9];
		int[] outPixels = new int[width * height];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int k = 0;
				int irgb = inPixels[index];
				int ir = (irgb >> 16) & 0xff;
				int ig = (irgb >> 8) & 0xff;
				int ib = irgb & 0xff;
				for (int dy = -1; dy <= 1; dy++) {
					int iy = y+dy;
					if (0 <= iy && iy < height) {
						int ioffset = iy*width;
						for (int dx = -1; dx <= 1; dx++) {
							int ix = x+dx;
							if (0 <= ix && ix < width) {
								int rgb = inPixels[ioffset+ix];
								r[k] = (rgb >> 16) & 0xff;
								g[k] = (rgb >> 8) & 0xff;
								b[k] = rgb & 0xff;
							} else {
								r[k] = ir;
								g[k] = ig;
								b[k] = ib;
							}
							k++;
						}
					} else {
						for (int dx = -1; dx <= 1; dx++) {
							r[k] = ir;
							g[k] = ig;
							b[k] = ib;
							k++;
						}
					}
				}
				outPixels[index] = (inPixels[index] & 0xff000000) | (smooth(r) << 16) | (smooth(g) << 8) | smooth(b);
				index++;
			}
		}
		return outPixels;
	}
}
