package world;

import java.util.ArrayList;
import java.util.List;

import main.CONSTANTS;
import utils.Intersection;
import utils.Ray;
import world.camera.Camera;


public class Scene {

	private Camera cam;
	private List<SceneObject> objects;
	private List<SceneObject> lights;
	
	
	public Scene(){
		this.objects = new ArrayList<SceneObject>();
		this.lights = new ArrayList<SceneObject>();
	}
	
	public Intersection nearestIntersection(Ray ray){
		
		
		double lastDistance = CONSTANTS.MAX_DISTANCE;
		Intersection lastIntersection = new Intersection();
		
		for (SceneObject obj : this.objects){
			if(obj.hitBounds(ray)){
				Intersection i = obj.hit(ray);
				
				double distance = i.getDistance();
				
				if(distance != -1 && distance < lastDistance){
					lastDistance = distance;
					lastIntersection = i;
				}
			}
		}
		
		
		return lastIntersection;
	}
	
	public void addMesh(SceneObject m){
		this.objects.add(m);
		
		if(m instanceof TriangleMesh){
			
			for(Triangle t : ((TriangleMesh) m).triangles ){
				if ( !t.material.emissionColor.isEmpty()) this.lights.add(t);
			}
			
		}else{
			if ( !m.material.emissionColor.isEmpty()) this.lights.add(m);
		}
		
		
	}
	
	public Camera getCam() {
		return cam;
	}
	
	public List<SceneObject> getLights(){
		return lights;
	}
	
	public void setCam(Camera cam) {
		this.cam = cam;
	}
	
	public void setObjects(ArrayList<SceneObject> meshes) {
		this.objects = meshes;
	}
	
	
}
