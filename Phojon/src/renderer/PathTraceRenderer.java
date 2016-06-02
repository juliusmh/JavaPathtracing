package renderer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.text.html.HTMLDocument.Iterator;

import main.CONSTANTS;
import utils.Intersection;
import utils.Ray;
import utils.Sampler;
import utils.Vector3;
import world.Scene;
import world.SceneObject;
import world.material.Material;

public class PathTraceRenderer{

	private int maxDepth, spp;
	
	private int width, height;
	
	private Scene scene;
	
	private Sampler sampler;
	
	// Data holders for progressive rendering
	int[][] pixelSamples;
	Vector3[][] colors;
	
	// UI
	Graphics drawingPane;
	
	public PathTraceRenderer(int width, int height, Scene scene, int maxDepth, int spp) {
		
		this.maxDepth = maxDepth;
		this.spp = spp;
		this.sampler = new Sampler(1);
		this.width = width; this.height = height;
		this.scene = scene;
		
		colors = new Vector3[width][height];
		
		pixelSamples = new int[width][height];
		
		for (int w = 0; w < width; w++){
			for (int h = 0; h < height; h++){
				colors[w][h] = new Vector3(0,0,0);
				pixelSamples[w][h] = 0;
			}
		}
		
	}

	public void startLive(){
		JFrame frm = new JFrame("Pathtracer - Java"); 
		frm.setSize(width+15,height+35);
		
		frm.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		JPanel pan = new JPanel();
		pan.setSize(width,height);
		frm.setContentPane(pan);

		JLabel lbl = new JLabel();
		lbl.setText("0.0 Samples");
		frm.add(lbl);
		
		frm.setVisible(true);

		drawingPane = pan.getGraphics().create();
		
		// Init the Rendering
		
		ThreadGroup g = new ThreadGroup("PathTracer");
		
		int threadCount = 5;
		
		int step = Math.floorDiv(200, threadCount);
		
		for(int i = 0; i < threadCount; i++) {
			//Thread t = new Thread(g,  new RenderRunnable(250 + i * step, 250, 250 + (i+1) * step, height - 100, colors));
			Thread t = new Thread(g,  new RenderRunnable(200 + i * step, 150, 200 + (i+1) * step, height, colors));
			t.start();
		}
		
		final Timer timer = new Timer(1000, null);
		ActionListener listener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				BufferedImage render = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
				double samplesD = 0.0;
				
				for (int xx = 0; xx < width; xx++){
					for (int yy = 0; yy < height; yy++){
						render.setRGB(xx, yy, colors[xx][yy].mul(1.0 / (pixelSamples[xx][yy]+CONSTANTS.EPSILON)).toColor().getRGB());
						samplesD += pixelSamples[xx][yy];
					}
				}
				
				samplesD /= width * height;
				drawingPane.drawImage(render, 0, 0, width,height,null);
				//try {ImageIO.write(render, "PNG", new File("pt_live/render_"+samplesD+".png"));} catch (IOException e) {}
				lbl.setText(String.valueOf(samplesD) + " spp");
			}
		};
		
		timer.addActionListener(listener);
		timer.start();
		
	}
	
	private Vector3 pathTrace(Ray ray, int d, int x, int y){
		
		if (d == this.maxDepth) return Vector3.zeroVector; // Max Recursion depth
		
		Intersection intersection = scene.nearestIntersection(ray);
		
		if (intersection.getDistance() == -1) return Vector3.zeroVector; // Nothing hit
	
		Material m = intersection.getObj().material;

		// Lamps and Lights
		if (!m.emissionColor.isEmpty()) return m.emissionColor; // Lights return their emittance
		
		Vector3 offsetPoint = intersection.getPoint().add(intersection.getNormal().mul(CONSTANTS.EPSILON));
		
		// Decide what to do with that intersection
		Vector3 output = new Vector3(0,0,0);		
		
		// Sharp Optical Laws
		if(m.transparency > 0 || m.reflectivity > 0){
			double fresnelEffekt = m.fresnelEffect(ray.getDir(), intersection.getNormal());
			
			Vector3 reflectedDir = sampler.specularSample(m.reflect(ray.getDir(), intersection.getNormal()), intersection.getNormal(), m.shininess);
			Vector3 reflectedColor = pathTrace(new Ray(offsetPoint, reflectedDir), d + 1, x , y);
			reflectedColor = reflectedColor.mul(m.reflectivity);
			
			Vector3 refractedColor = new Vector3();
			if (m.transparency > 0){
				Vector3 refractedDir = m.refract(ray.getDir(), intersection.getNormal());
				refractedColor = pathTrace(new Ray(intersection.getPoint().add(refractedDir.mul(CONSTANTS.EPSILON)), refractedDir), d + 1, x , y);
				refractedColor = refractedColor.mul(m.transparency);
			}
			
			output = reflectedColor.mul(fresnelEffekt).add( refractedColor.mul((1.0 - fresnelEffekt)));

		}
		
		//Vector3 newDir = sampler.specularSample(m.reflect(ray.getDir(), intersection.getNormal()), intersection.getNormal(), m.shininess);
		
		Vector3 newDir = sampler.diffuseSample(intersection.getNormal());
		
		Ray newRay = new Ray(offsetPoint, newDir.normalize());
					
		double cos_theta = newDir.dot(intersection.getNormal()); 
		Vector3 BRDF = m.diffuseColor.mul(cos_theta).mul(2);
					   
		// Get the new color by using recursion
		Vector3 newColor = pathTrace(newRay, d + 1, x,y);
		
		output = output.add( (BRDF.mul(newColor)).mul(1.0 - m.reflectivity).mul(1.0 - m.transparency) );
		
		return output;
		
	}
	
	private class RenderRunnable implements Runnable{

		private int x,y,x2,y2;
		
		private Vector3[][] colors ;
		
		public RenderRunnable(int x, int y, int x2, int y2, Vector3[][] colors){
			this.colors = colors;
			
			this.x = x; this.y = y;
			this.x2 = x2; this.y2 = y2;
		}
		
		@Override
		public void run() {
			boolean contin = true;
			
			while(contin){
				contin = false;
				for (int xx = x; xx < x2; xx++){
					for (int yy = y; yy < y2; yy++){
						if(pixelSamples[xx][yy] < spp){
							contin = true;
							Ray ray = scene.getCam().generateRay(xx, height - 1 - yy);
							Vector3 mapData = pathTrace(ray,  0, xx, yy);
							colors[xx][yy] = colors[xx][yy].add(mapData);
							pixelSamples[xx][yy]++;
						}
					}
				}
			}
			
		}
		
	}

}
