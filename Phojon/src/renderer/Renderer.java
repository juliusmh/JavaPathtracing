package renderer;

import java.awt.image.BufferedImage;
import utils.Ray;
import utils.Sampler;
import utils.Vector3;
import world.Scene;

public abstract class Renderer {

	public Scene scene;
	public int width,height;
	public Sampler sampler;
	
	//PARAMS FOR RENDERING
	public int reflectionDepth;
	public Vector3 backColor, ambientLight;
		
	// Start
	public Renderer(int width , int height, Scene scene){
		this.scene = scene;
		this.width = width; this.height = height;
		
		this.backColor = new Vector3(0.2,0.2,0.2);
		this.ambientLight = new Vector3(0.2,0.2,0.2);
		
		this.sampler = new Sampler(1);
		
		this.reflectionDepth = 10;
		
	}
	
	public abstract Vector3 renderColor(Ray ray);

	public int maxSamples = 100;
	
	public BufferedImage render(){
		BufferedImage render = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		long start = System.currentTimeMillis();
		
		for (int xx = 0; xx < width; xx++){
			for (int yy = 0; yy < height; yy++){
				Ray ray = scene.getCam().generateRay(xx, height - 1 - yy);
				render.setRGB(xx,  yy, renderColor(ray).toColor().getRGB());
			}
			
		}
		
		System.out.println("Render took " + (System.currentTimeMillis() - start) / 1000.0 + " ms");
		
		return render;
	}
	
}
