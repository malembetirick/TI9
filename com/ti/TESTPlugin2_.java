/**
 * Laplacien_.java 
 *
 * D�tection de points contours par approches du second ordre.
 * Fichier �tudiant � compl�ter
 *
 * @author 
 *
 */
package com.ti;

import ij.*;	// pour classes ImagePlus et IJ
import ij.gui.*;	// pour classes GenericDialog et DialogListener
import ij.plugin.filter.*;	// pour interface PlugInFilter et Convolver
import ij.process.*;	// pour classe ImageProcessor et sous-classes
import java.awt.*;		//pour classes AWTEvent, CheckBox, TextField
import java.util.Vector;	// pour classe Vector

import com.ti.plugins.TestPlugin_;


public class TESTPlugin2_ implements PlugInFilter, DialogListener {
 
	private static int filtre=0;
	private final static String[] FILTRES_LAPLACIENS3x3 = {"Laplacien1", "Laplacien2", "Laplacien3","Laplacien4","Laplacien5"};
	private final static float[][] MASQUES_LAPLACIENS3x3 = {
		{0,1,0, 1,-4,1, 0,1,0},
		{1,0,1, 0,-4,0, 1,0,1},
		{1,1,1, 1,-8,1, 1,1,1},
		{1,2,1, 2,-12,2, 1,2,1},
		{1,4,1, 4,-20,4, 1,4,1}
	};
	private static float sigma=0.0f;
	private static boolean seuillageZeroCross=false;
	private static boolean seuilZeroCrossAuto=true;
	private static float seuilZeroCross=10f;
	
	private ImagePlus imp;

	// ---------------------------------------------------------------------------------
	// M�thodes de l'interface PlugInFilter
	
	// Initialisation du plugin
	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return PlugInFilter.DOES_8G;
	}

	// M�thode principale du plugin
	public void run(ImageProcessor ip) {
		
		// Affichage de la fen�tre de configuration
		if (!showDialog())
			return;

		// Titre et extension de l'image source
		String titre = imp.getTitle();
		String extension="";
		int index = titre.lastIndexOf('.');
		if (index>0)
			extension = titre.substring(index);
		else
			index = titre.length();
		titre = titre.substring(0,index);		

		// G�n�ration d'un masque LoG de taille impaire (exercice 3)
		/* � compl�ter */
		
		
		// Calcul et repr�sentation du Laplacien (exercice 1, puis � modifier dans le 3) 
		FloatProcessor fpLaplacian = (FloatProcessor)(ip.duplicate().convertToFloat());
		/* � compl�ter */
		fpLaplacian.convolve(MASQUES_LAPLACIENS3x3[filtre], 3, 3);
		ImagePlus newImg = new ImagePlus("Résultat du seuillage par hystérésis", fpLaplacian);
        newImg.show();
		// D�tection et affichage des passages par 0 du Laplacien par seuillage du Laplacien (exercice 2)
		if (seuillageZeroCross) {
			/* � compl�ter */
		}
	}
	
	// ---------------------------------------------------------------------------------
	/**
	 * M�thode d'affichage de la fen�tre de config et de lecture des valeurs saisies, appel�e dans run()
	 * 
	 * @return false si la fen�tre a �t� ferm�e en cliquant sur Cancel, true sinon (boolean)
	 */ 
    
    public boolean showDialog() {
    	
		// Description de la fen�tre de config
		GenericDialog gd = new GenericDialog("Laplacian parameters");
		gd.addChoice("Laplacian filter type:", FILTRES_LAPLACIENS3x3, FILTRES_LAPLACIENS3x3[filtre]);
		gd.addNumericField("Gaussian filtering scale (0 for none)", sigma, 1);
		gd.addCheckbox("Threshold Laplacian zero-crossings", seuillageZeroCross);
		gd.addNumericField("Threshold value", seuilZeroCross, 0);
		gd.getComponent(gd.getComponentCount()-1).setEnabled(seuillageZeroCross && !seuilZeroCrossAuto); // D�sactiver champ ?
		gd.addCheckbox ("Auto threshold", seuilZeroCrossAuto);
		gd.getComponent(gd.getComponentCount()-1).setEnabled(seuillageZeroCross && seuilZeroCrossAuto); // D�sactiver case ?
		gd.addDialogListener(this);     		// the DialogItemChanged method will be called on user input
		gd.showDialog();                		// display the dialog; preview runs in the background now
		if (gd.wasCanceled()) return false;

       	// Lecture des valeurs saisies
		filtre = gd.getNextChoiceIndex();
		sigma = (float) gd.getNextNumber();
		seuillageZeroCross = gd.getNextBoolean();
		seuilZeroCross = (float) gd.getNextNumber();
		seuilZeroCrossAuto = gd.getNextBoolean();

        return true;
    }	

    // ---------------------------------------------------------------------------------
	/**
	 * M�thode appel�e sur modification (par un �v�nement) de la fen�tre de config
	 * 
	 * @param gd Fen�tre de dialogue (GenericDialog)
	 * @param e �v�nement � traiter (AWTEvent) 
	 * @return true si la saisie est correcte, false sinon (boolean)
	 */
    
	public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
		
		// Acc�s aux champs de la fen�tre
		Checkbox zcCheckbox = (Checkbox)gd.getCheckboxes().get(0);
		Checkbox zcAutoCheckbox = (Checkbox)gd.getCheckboxes().get(1);
		Vector numFields = gd.getNumericFields();
		TextField sigmaField = (TextField)numFields.get(1);
		TextField zcThresholdField = (TextField)numFields.get(1);
 		if (e!=null) {	// e==null si clic sur OK
			// D�sactivation/activation du champ num�rique de seuil, selon seuillage ou pas
	        if (e.getSource() == zcCheckbox) { 
	        	zcThresholdField.setEnabled(zcCheckbox.getState()&&!zcAutoCheckbox.getState());
	        	zcAutoCheckbox.setEnabled(zcCheckbox.getState());
	        }
	        else if (e.getSource() == zcAutoCheckbox) {
	        	zcThresholdField.setEnabled(!zcAutoCheckbox.getState());
	        }
 		}
 		sigma = Float.valueOf(sigmaField.getText());
 		seuilZeroCross = Integer.valueOf(zcThresholdField.getText());
        return (!gd.invalidNumber() && sigma>=0 && seuilZeroCross>=0);
    }
	
	// ---------------------------------------------------------------------------------

	/**
	 * D�tection des passages par 0 du Laplacien (algo min<-seuil && max>seuil)
	 * 
	 * @param imLaplacien Image du Laplacien (ImageProcessor 32 bits)
	 * @param seuil Seuil sur les valeurs du Laplacien
	 * @return imZeros Carte des passages par 0 (image binaire)
	 */
	public ByteProcessor laplacienZero(ImageProcessor imLaplacien, Float seuil) {
		
		int width = imLaplacien.getWidth();
		int height = imLaplacien.getHeight();
		
		// Image binaire r�sultat des points contours apr�s seuillage
		ByteProcessor imZeros = new ByteProcessor(width,height);
		
		/* � compl�ter */	
		for (int i = 1; i < height-1; i++) {
			for (int j = 1; j < width-1; j++) {
				float min = imLaplacien.get(j,i);
				float max = imLaplacien.get(j,i);
				for (int x = -1; x < 2; x++) {
					for (int y = -1; y < 2; y++) {
						if (min>imLaplacien.get(x+j,y+i)) 
							min = imLaplacien.getf(x+j,y+i);						
						if (max<imLaplacien.get(x+j,y+i)) 
							max = imLaplacien.getf(x+j,y+i);
					}
				}
				if (min<-seuil && max>seuil) 
					imZeros.set(j, i, 255);
				else 
					imZeros.set(j, i, 0);	
			}
		}			
		return imZeros;
	}

	// ---------------------------------------------------------------------------------
	
    /**
     * G�n�ration d'un masque LoG dans un tableau float[]
     * 
     * @param tailleMasque nombre de lignes et de colonnes du masque LoG (int, impair).
     * @param sigma �cart-type de la gaussienne (float, au moins 5*sigma)
     * @return masque de l'op�rateur LoG
     */
    public static float[] masqueLoG(int tailleMasque, float sigma)
    {     
		short aperture = (short)(tailleMasque/2);
		double[][] LoG = new double[2*aperture+1][2*aperture+1];
		float[] out=new float[(2*aperture+1)*(2*aperture+1)];
		double sum=0, s2=Math.pow(sigma,2);
		int k=0;

		 // Calcul du masque LoG
		 for(int dy=-aperture;dy<=aperture;dy++)
		 {
			 for(int dx=-aperture;dx<=aperture;dx++)
			 {
				 double r2=-(dx*dx+dy*dy)/2.0/s2;
				 LoG[dy+aperture][dx+aperture]=-1/(Math.PI*s2*s2)*(1+r2)*Math.exp(r2);
				 sum+=LoG[dy+aperture][dx+aperture];
			 }
		 }
		 // Soustraction de la moyenne pour obtenir une somme nulle des coefs
		 sum=sum/(tailleMasque*tailleMasque);
		 for(int dy=-aperture;dy<=aperture;dy++)
			 for(int dx=-aperture;dx<=aperture;dx++)
				 out[k++]= (float)(LoG[dy+aperture][dx+aperture]-sum);
		 
		 return out;
    }
    public static void main(String[] args) {
		// set the plugins.dir property to make the plugin appear in the Plugins menu
		Class<?> clazz = TESTPlugin2_.class;
		String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
		String pluginsDir = url.substring(5, url.length() - clazz.getName().length() - 6);
		System.setProperty("plugins.dir", pluginsDir);

		// start ImageJ
		new ImageJ();

		// open the Clown sample
		
		ImagePlus image = IJ.openImage("./spores.png");
		ImageConverter imc = new ImageConverter(image);
		imc.convertToGray8();
		image.updateAndDraw();
		image.show();

		// run the plugin
		IJ.runPlugIn(clazz.getName(), "");
	}

}
