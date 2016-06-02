package renderer;

import main.CONSTANTS;
import utils.Intersection;
import utils.Ray;
import utils.Vector3;
import world.Scene;
import world.SceneObject;
import world.material.Material;

public class DirectOnlyRenderer extends Renderer{

	private int shadowSamples;
	
	public DirectOnlyRenderer(int width, int height, Scene scene, int shadowSamples) {
		super(width, height, scene);
		this.shadowSamples = shadowSamples;
	}

	@Override
	public Vector3 renderColor(Ray ray) {
		return renderColor2(ray, 0);
	}

	public Vector3 renderColor2(Ray ray, int depth) {
		
		Intersection intersection = this.scene.nearestIntersection(ray);
		
		Vector3 mapData = new Vector3(0,0,0);
		
		// If we got an Intersection
		if (intersection.getDistance() != -1.0){
			
			Material m = intersection.getObj().material;
			
			mapData = m.ambientColor.mul(this.ambientLight);
			
			// Only shade objects that are no lights
			if (m.emissionColor.isEmpty()){

				double shade = 0.8;
				
				// Iterate thorguh all lights
				for (SceneObject light : scene.getLights()){
					
					Vector3 dir;
					
					if (this.shadowSamples > 0){
						for (int sampleCount = 0; sampleCount < this.shadowSamples; sampleCount++){
							
							dir = (light.pointInObject(sampler.rand).sub(intersection.getPoint())).normalize();
								
							Ray rr = new Ray(intersection.getPoint().add(dir.mul(CONSTANTS.EPSILON)), dir);
									
							Intersection shadowIntersection = this.scene.nearestIntersection(rr);
									
							if (shadowIntersection.getDistance() != -1.0){
								if (shadowIntersection.getObj().material.emissionColor.magnitude() == 0){
									shade -=  (1.0 / (this.shadowSamples * scene.getLights().size())); 
								}
							}
								
						}
					}else{
						dir = (light.getCenter().sub(intersection.getPoint())).normalize();
						Ray rr = new Ray(intersection.getPoint().add(dir.mul(CONSTANTS.EPSILON)), dir);
						
						Intersection shadowIntersection = this.scene.nearestIntersection(rr);
								
						if (shadowIntersection.getDistance() != -1.0){
							if (shadowIntersection.getObj().material.emissionColor.magnitude() == 0){
								shade -=  0.2;
							}
						}
					}
					
					
					
					// Diffuse Lambertian shading ("KD")
					dir = light.getCenter().sub(intersection.getPoint()).normalize();
					
					if(!m.diffuseColor.isEmpty()){
						
						double dot = intersection.getNormal().dot(dir);
						if(dot > 0){
							mapData = mapData.add(m.diffuseColor.mul(dot));
						}
						
					}
					
					// Specular Phong Shading
					if(m.shininess > 0){
						// intensity = diffuse * (L.N) + specular * (V.R)n 
						// http://www.flipcode.com/archives/Raytracing_Topics_Techniques-Part_2_Phong_Mirrors_and_Shadows.shtml
						Vector3 N = intersection.getNormal();
						Vector3 R = dir.sub(N.mul(2.0 * dir.dot(N)));
						double dot = ray.getDir().dot(R);

						if(dot > 0){
							double specular = Math.pow(dot, m.specularExponent) * m.shininess;
							mapData = mapData.add(m.specularColor.mul(specular));
						}
					}
					
				}
				
				shade = Math.max(0.05, shade);
				mapData = mapData.mul(shade);
				
				//-----RENDER REFLECTIONS-----
				if(m.reflectivity > 0){
					if (depth <= this.reflectionDepth){
						Vector3 newDirection = m.reflect(ray.getDir(), intersection.getNormal());
						Vector3 tmp = renderColor2(new Ray(intersection.getPoint().add(intersection.getNormal().mul(CONSTANTS.EPSILON)), newDirection), depth + 1);
						mapData = mapData.add(tmp.mul(m.reflectivity));
					}
					
				}
				
				//-----RENDER REFRACTIONS-----
				if(m.transparency > 0){			
					if (depth <= this.reflectionDepth){
						Vector3 t = m.refract(ray.getDir(), intersection.getNormal());
						mapData = mapData.add(renderColor2(new Ray(intersection.getPoint().add(t.mul(CONSTANTS.EPSILON)), t) , depth + 1).mul(m.transparency));
					}
					
				}
					
			}else{mapData = m.emissionColor;}
			
		}else{mapData = backColor;}
		
		return mapData;
	}

}
