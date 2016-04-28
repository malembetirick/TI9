package com.ti.plugins;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

public class Sample_cfa implements PlugInFilter {
	
	   ImagePlus imp;	// Fen�tre contenant l'image de r�f�rence
	    int width;		// Largeur de la fen�tre
	    int height;		// Hauteur de la fen�tre

	    public int setup(String arg, ImagePlus imp) {
	    	/* � compl�ter*/
	    	this.imp = imp;
			return PlugInFilter.DOES_8G;	

}

		@Override
		public void run(ImageProcessor arg0) {
			empile(arg0);
			
		}
		
		public ImageProcessor cfa_samples(ImageProcessor ip, int k)
		{
			int pixel_value=0;
			int green =0,blue=0,red = 0;
			ImageProcessor cfa_ip=new ByteProcessor(width,height);
			
		
			 // �chantillons G
		    for (int y=0; y<height; y+=2) {
		        for (int x=0; x<width; x+=2) {
		            pixel_value = ip.getPixel(x,y);
		            if(k==1){
		            	green = (int)pixel_value;
		            }
		            cfa_ip.putPixel(x,y,green);
		        }
		    }
		    for (int y=1; y<height; y+=2) {
		        for (int x=1; x<width; x+=2) {
		            pixel_value = ip.getPixel(x,y);
		            if(k==1){
		            	green = (int)(pixel_value);
		            }
		            cfa_ip.putPixel(x,y,green);
		        }
		    }
		    
		    cfa_ip.;
		    // �chantillons R
		    for (int y=0; y<height; y+=2) {
		        for (int x=1; x<width; x+=2) {
		            pixel_value = ip.getPixel(x,y);
		            if(k==0)
		            	red = (int)(pixel_value);
		            cfa_ip.putPixel(x,y,red);
		        }
		    }
		    // �chantillons B
		    for (int y=1; y<height; y+=2) {
		        for (int x=0; x<width; x+=2) {
		            pixel_value = ip.getPixel(x,y);
		            if(k==2)
		            	blue = (int)(pixel_value);
		            cfa_ip.putPixel(x,y,blue);
		        }
		    }
		    return cfa_ip;
		}
		
		public void empile(ImageProcessor ip)
		{
			ImageStack samples_stack = imp.createEmptyStack();
			samples_stack.addSlice("Rouge",cfa_samples(ip,0));
			samples_stack.addSlice("Vert",cfa_samples(ip,1));
			samples_stack.addSlice("Bleu",cfa_samples(ip,2));
			ImagePlus cfa_samples_imp = imp.createImagePlus();
			cfa_samples_imp.setStack("echantillons couleur cfa",samples_stack);
			
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
