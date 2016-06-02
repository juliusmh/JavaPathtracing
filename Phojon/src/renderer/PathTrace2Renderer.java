package renderer;

import main.CONSTANTS;
import utils.Intersection;
import utils.Ray;
import utils.Vector3;
import world.Scene;
import world.SceneObject;
import world.material.Material;

public class PathTrace2Renderer extends Renderer {

	private int maxDepth;
	
	public PathTrace2Renderer(int width, int height, Scene scene, int maxDepth) {
		super(width, height, scene);
		this.maxDepth = maxDepth;
		
	}

	@Override
	public Vector3 renderColor(Ray ray) {
		Vector3 coeff = new Vector3(1,1,1);
		Vector3 output = new Vector3(1,0,0);
		
		for(int depth = 0; depth < this.maxDepth; depth++){
			Intersection hit = this.scene.nearestIntersection(ray);
			
			
			if(hit.getDistance() == -1) break;
			
			output = hit.getObj().material.diffuseColor;
			
			// Sampling the Light Sources
			for(int lss = 0; lss < 1; lss++){
				Vector3 lightDir = sampler.lightSample(hit.getPoint(), this.scene.getLights());
				Ray lightRay = new Ray(hit.getPoint().add(lightDir.mul(CONSTANTS.EPSILON)), lightDir);
				Intersection lightHit = this.scene.nearestIntersection(lightRay);
				
				if(lightHit.getDistance() > -1){
					output = output.add(coeff.mul(lightHit.getObj().material.emissionColor.normalize()));
				}
				
			}
			
			Vector3 newDir = sampler.diffuseSample(hit.getNormal());
			Ray newRay = new Ray(hit.getPoint().add(newDir.mul(CONSTANTS.EPSILON)), newDir.normalize());
				
			double cos_theta = newDir.dot(hit.getNormal()); 
			
			coeff = coeff.mul(cos_theta * hit.getNormal().dot(newRay.getDir()));
			
		}
		
		return output;
	}


}
