package world;

import java.util.Random;

import main.CONSTANTS;
import utils.Intersection;
import utils.Ray;
import utils.Vector3;
import utils.Vertex;

public class Triangle extends SceneObject {

	public Vertex[] verts; //FACE DEFINED BY 3 POINTS

	private Vector3 edge1, edge2;
	public Vector3  normal, center;	
	
	public boolean smoothing = false;
	
	// TEMP Variables
	
	public Triangle(Vertex a, Vertex b, Vertex c){
		super();
		this.verts = new Vertex[3];
		
		this.verts[0] = a;
		this.verts[1] = b;
		this.verts[2] = c;
		
		normal = calcNormal();
		center = new Vector3(); center = calcCenter();
		
	}
	
	private Vector3 calcNormal(){
		edge1 = verts[1].location.sub(verts[0].location);
		edge2 = verts[2].location.sub(verts[0].location);
		normal = edge1.cross(edge2).normalize();
		return normal;
	}
	
	
	@Override
	public Intersection hit(Ray ray) {
		
		/*
		 * Möller-Trumbore algorithm from 
		 * http://www.scratchapixel.com/old/lessons/3d-basic-lessons/lesson-10-polygonal-objects/ray-tracing-polygon-meshes/
		*/
		
		double det, invDet;
		double v, t;
		
		Vector3 origin = ray.getOrigin();
		Vector3 dir = ray.getDir();
			 
		Vector3 pvec = dir.cross(edge2);
		det = edge1.dot(pvec);
			
		if (Math.abs(det) < CONSTANTS.EPSILON) return Intersection.noIntersection;
			
		invDet = 1.0 / det;
	        
	    Vector3 tvec = origin.sub(verts[0].location);
	    double u = tvec.dot(pvec) * invDet;
	        
	    if (u < 0.0 || u > 1.0) return Intersection.noIntersection;
	        
	    Vector3 qvec = tvec.cross(edge1);
	    v = dir.dot(qvec) * invDet;
	       
	    if (v < 0 || u + v > 1) return Intersection.noIntersection;
	        
	    t = edge2.dot(qvec) * invDet;
			
	    if (t > CONSTANTS.EPSILON){
	    	Vector3 point = verts[0].location.mul(1-u-v).add(verts[1].location.mul(u)).add(verts[2].location.mul(v));
		   
	    	Intersection i = new Intersection(new Vector3(), -1, new Vector3(), this);
	        	
		    i.setDistance(point.sub(origin).magnitude());
		    i.setPoint(point);
		    
		    if(smoothing){
		    	Vector3 N1 = verts[0].normal;
		    	Vector3 N2 = verts[1].normal;
		    	Vector3 N3 = verts[2].normal;
		    	Vector3 normalInter = N1.add(N2.sub(N1).mul(u).add(N3.sub(N1).mul(v))).normalize();
		    	i.setNormal(normalInter);
		    }else{
		    	i.setNormal(normal);
		    }
		    
		        
		    return i;
	    }
	       
	    return Intersection.noIntersection;
	}	
	
	@Override
	public Vector3 pointInObject(Random r) {
		
		// From http://math.stackexchange.com/questions/538458/triangle-point-picking-in-3d
		
		double a = r.nextDouble();
		double b = r.nextDouble();
		
		if (a+b >= 1){
			a = 1-a;
			b = 1-b;
		}
		
		Vector3 v1 = verts[0].location;
		Vector3 v2 = verts[1].location;
		Vector3 v3 = verts[2].location;
		
		Vector3 point = v1.add(v2.sub(v1).mul(a)).add(v3.sub(v1).mul(b));
		
		return point;
	}
	
	// Misc
	public Vector3 calcCenter(){
		center = center.mul(0.0);
		for (int i = 0; i < 3; i++) center = center.add(verts[i].location);
		center = center.mul(1.0 / 3.0);
		return center;
	}
	
	@Override
	public Vector3 getCenter() {
		return center;
	}
	
	@Override 
	public String toString(){
		String s = "";
		for (int i = 0; i < 3; i++) s += verts[i].location.toString() + "\n";
		return s;
	}
	
	@Override
	public void move(Vector3 v) {
		center = calcCenter();
		normal = calcNormal();
	}

	@Override
	public void rotate(double a, double b, double c) {
		// TODO Rotate around center
		center = calcCenter();
		normal = calcNormal();
	}

	@Override
	public void scale(double s) {
		// TODO Scale around center
		center = calcCenter();
		normal = calcNormal();
	}
	
}
