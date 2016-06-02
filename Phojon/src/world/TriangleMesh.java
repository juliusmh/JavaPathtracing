package world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import main.CONSTANTS;
import utils.Intersection;
import utils.Ray;
import utils.Vector3;
import utils.Vertex;

public class TriangleMesh extends SceneObject{
	
	public int polygonCount;
	
	public boolean backfaceCulling;
	
	public String name;
	public ArrayList<Triangle> triangles;
	
	// Construcotr
	public TriangleMesh(String name, ArrayList<Triangle> triangles ){
		super();
		this.name = name;
		this.triangles = triangles;
		this.backfaceCulling = false;
	}
	
	public void setSmoothing(boolean state){
		if(state){
			calcNormals();
		}
		
		for (Triangle tri : this.triangles){
			tri.smoothing = state;
		}
	}
	
	public void setBackfaceCulling(boolean state){
		backfaceCulling = state;
	}
	
	public void calcNormals(){
		List<Vertex> verts = new ArrayList<Vertex>();
		for(Triangle tri: triangles){
			verts.add(tri.verts[0]);
			verts.add(tri.verts[1]);
			verts.add(tri.verts[2]);
		}
		
		for(Vertex v : verts){
			if(v.normal == null){
				Vector3 tmp = new Vector3(0,0,0);
				
				for(Triangle p : triangles){
					for (int i = 0; i < 3; i++){
						if(p.verts[i].equals(v)){
							tmp = tmp.add(p.normal);
						}
					}	
				}
				
				v.normal = tmp.normalize();
			}
		}
	}
	
	public BoundingBox calcBounds(){
		
		BoundingBox b = new BoundingBox(new Vector3(0,0,0), new Vector3(0,0,0));
		
		for(Triangle t : this.triangles){
			
			for (int i = 0; i < 3; i++) b.extend(t.verts[i].location);
			
		}
		
		return b;
		
	}
	
	// TODO Improve this function as it takes 75% of all calculation time
	
	@Override
	public Intersection hit(Ray ray) {
		
		double lastDistance = CONSTANTS.MAX_DISTANCE;
		Intersection ri = new Intersection();
		
		for(Triangle t : this.triangles){
			
			
			
			if( !backfaceCulling || (backfaceCulling && ray.getDir().dot(t.normal) <= 0)){
				Intersection i = t.hit(ray);
				double distance = i.getDistance();
					
				if(distance != -1 && distance < lastDistance){
					lastDistance = distance;
					ri = i;
				}
			}
			
			
		}
		
		return ri;
		
	}

	@Override
	public Vector3 pointInObject(Random r) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Vector3 getCenter() {
		
		Vector3 center = new Vector3();
		
		for (Triangle t : triangles){
			for (Vertex v : t.verts){
				center = center.add(v.location);
			}
		}
		
		return center.mul(1.0 / triangles.size());
	}

	@Override
	public void move(Vector3 mover) {
		for (Triangle t : triangles){
			for (Vertex v : t.verts){
				v.location = v.location.add(mover);
			}
		}
	}

	@Override
	public void rotate(double a, double b, double c) {}

	
	@Override
	public void scale(double s) { 
		// From http://stackoverflow.com/questions/6684851/scaling-3d-models-finding-the-origin
	}

	
}
