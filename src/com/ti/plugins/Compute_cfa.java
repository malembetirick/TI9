package com.ti.plugins;

import ij.*;
import ij.plugin.filter.*;
import ij.process.*;
import ij.gui.*;

public class Compute_cfa implements PlugInFilter {

    ImagePlus imp;	// Fen�tre contenant l'image de r�f�rence
    int width;		// Largeur de la fen�tre
    int height;		// Hauteur de la fen�tre

    public int setup(String arg, ImagePlus imp) {
    	/* � compl�ter*/
    	this.imp = imp;
		return PlugInFilter.DOES_ALL;	
    }
    public void run(ImageProcessor ip) {

    // Lecture des dimensions de la fen�tre
    width = imp.getWidth();
    height = imp.getHeight();

    // Dispositions possibles pour le CFA
    String[] orders = {"R-G-R", "B-G-B", "G-R-G", "G-B-G"};

    // D�finition de l'interface
    GenericDialog dia = new GenericDialog("Generation de l'image CFA...", IJ.getInstance());
    dia.addChoice("Debut de premiere ligne :", orders, orders[2]);
    dia.showDialog();

    // Lecture de la r�ponse de l'utilisateur
    if (dia.wasCanceled()) return;
    int order = dia.getNextChoiceIndex();

    // G�n�ration de l'image CFA
    /* � compl�ter */
    ImagePlus c = new ImagePlus("cfa traitement",cfa(order));
    c.show();
    
}
  //G�n�re l'image CFA
    ImageProcessor cfa(int row_order) {

    // Image couleur de r�f�rence et ses dimensions
    ImageProcessor ip = imp.getProcessor();
    width = imp.getWidth();
    height = imp.getHeight();

    int pixel_value = 0;	// Valeur du pixel source
    ImageProcessor cfa_ip = new ByteProcessor(width,height);	// Image CFA g�n�r�e

    // �chantillons G
    for (int y=0; y<height; y+=2) {
        for (int x=0; x<width; x+=2) {
            pixel_value = ip.getPixel(x,y);
            int green = (int)(pixel_value & 0x00ff00)>>8;
            cfa_ip.putPixel(x,y,green);
        }
    }
    for (int y=1; y<height; y+=2) {
        for (int x=1; x<width; x+=2) {
            pixel_value = ip.getPixel(x,y);
            int green = (int)(pixel_value & 0x00ff00)>>8;
            cfa_ip.putPixel(x,y,green);
        }
    }
    // �chantillons R
    for (int y=0; y<height; y+=2) {
        for (int x=1; x<width; x+=2) {
            pixel_value = ip.getPixel(x,y);
            int red = (int)(pixel_value & 0xff0000)>>16;
            cfa_ip.putPixel(x,y,red);
        }
    }
    // �chantillons B
    for (int y=1; y<height; y+=2) {
        for (int x=0; x<width; x+=2) {
            pixel_value = ip.getPixel(x,y);
            int blue = (int)(pixel_value & 0x0000ff);
            cfa_ip.putPixel(x,y,blue);
        }
    }

    return cfa_ip;
}
    public static void main(String[] args) {
		// set the plugins.dir property to make the plugin appear in the Plugins menu
		Class<?> clazz = Compute_cfa.class;
		String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
		String pluginsDir = url.substring(5, url.length() - clazz.getName().length() - 6);
		System.setProperty("plugins.dir", pluginsDir);

		// start ImageJ
		new ImageJ();

		// open the Clown sample
		
		ImagePlus image = IJ.openImage("./lighthouse.png");
		//ImageConverter imc = new ImageConverter(image);
		//imc.convertToGray8();
		//image.updateAndDraw();
		image.show();

		// run the plugin
		IJ.runPlugIn(clazz.getName(), "");
	}
}