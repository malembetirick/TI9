/**
 * 
 */
package com.ti.plugins;

import java.util.ArrayList;
import java.util.List;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;

/**
 * @author malembetirick
 *
 */
public class TestPlugin_ implements PlugInFilter {
	
	private int seuilBas=17;
    private int seuilHaut=46;

    public int setup(String arg, ImagePlus imp) {
        return PlugInFilter.DOES_8G;
    }

    public void run(ImageProcessor ip) {
        ByteProcessor newbp = hystIter(ip, this.seuilBas, this.seuilHaut);
        ImagePlus newImg = new ImagePlus("Résultat du seuillage par hystérésis", newbp);
        newImg.show();
    }

    public ByteProcessor hystIter(ImageProcessor imNormeG, int seuilBas, int seuilHaut) {
        int width = imNormeG.getWidth();
        int height = imNormeG.getHeight();

        ByteProcessor maxLoc = new ByteProcessor(width,height);
        List<int[]> highpixels = new ArrayList<int[]>();

        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {

                int g = imNormeG.getPixel(x, y)&0xFF;
                if (g<seuilBas) continue;

                if (g>seuilHaut) {
                    maxLoc.set(x,y,255);
                    highpixels.add(new int[]{x,y});
                    continue;
                }

                maxLoc.set(x,y,128);
            }
        }

        int[] dx8 = new int[] {-1, 0, 1,-1, 1,-1, 0, 1};
        int[] dy8 = new int[] {-1,-1,-1, 0, 0, 1, 1, 1};

        while(!highpixels.isEmpty()) {

            List<int[]> newhighpixels = new ArrayList<int[]>();

            for(int[] pixel : highpixels) {
                int x=pixel[0], y=pixel[1];

                for(int k=0;k<8;k++) {
                    int xk=x+dx8[k], yk=y+dy8[k];
                    if (xk<0 || xk>=width) continue;
                    if (yk<0 || yk>=height) continue;
                    if (maxLoc.get(xk, yk)==128) {
                        maxLoc.set(xk, yk, 255);
                        newhighpixels.add(new int[]{xk, yk});
                    }
                }
            }

            highpixels = newhighpixels;
        }

        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                if (maxLoc.get(x, y)!=255) maxLoc.set(x,y,0);
            }
        }
        return maxLoc;
    }
    public static void main(String[] args) {
		// set the plugins.dir property to make the plugin appear in the Plugins menu
		Class<?> clazz = TestPlugin_.class;
		String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
		String pluginsDir = url.substring(5, url.length() - clazz.getName().length() - 6);
		System.setProperty("plugins.dir", pluginsDir);

		// start ImageJ
		new ImageJ();

		// open the Clown sample
		
		ImagePlus image = IJ.openImage("./normelighthouse_8bits.png");
		ImageConverter imc = new ImageConverter(image);
		imc.convertToGray8();
		image.updateAndDraw();
		image.show();

		// run the plugin
		IJ.runPlugIn(clazz.getName(), "");
	}
}
