package world;

import utils.Ray;
import utils.Vector3;

public class BoundingBox {

	private Vector3 min, max;
	
	public BoundingBox(Vector3 min, Vector3 max){
		this.min = min; this.max = max;
	}
	
	public void extend(Vector3 v){
		
		for (int i = 0; i < 3; i++){
			if ( v.xyz[i] < min.xyz[i] ) min.xyz[i] = v.xyz[i];
			if ( v.xyz[i] > max.xyz[i] ) max.xyz[i] = v.xyz[i];
		}
		
	}
	
	public boolean hit(Ray r){
		double t1 = (min.x() - r.getOrigin().x())*1.0f / r.getDir().x();
		double t2 = (max.x() - r.getOrigin().x())*1.0f / r.getDir().x();
		double t3 = (min.y() - r.getOrigin().y())*1.0f / r.getDir().y();
		double t4 = (max.y() - r.getOrigin().y())*1.0f / r.getDir().y();
		double t5 = (min.z() - r.getOrigin().z())*1.0f / r.getDir().z();
		double t6 = (max.z() - r.getOrigin().z())*1.0f / r.getDir().z();

		double tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
		double tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

		// if tmax < 0, ray (line) is intersecting AABB, but whole AABB is behing us
		if (tmax < 0) return false;

		// if tmin > tmax, ray doesn't intersect AABB
		if (tmin > tmax) return false;
		
		return true;
	}
	
	public boolean hit2(Ray r){
		
		double tmp = 0;
		
		double txmin = (min.x() - r.getOrigin().x()) / r.getDir().x(); 
		double txmax = (max.x() - r.getOrigin().x()) / r.getDir().x();
		
		if ( txmin > txmax ) {
			tmp = txmin;
			txmin = txmax;
			txmax = tmp;
		}
		
		double tymin = (min.y() - r.getOrigin().y()) / r.getDir().y(); 
		double tymax = (max.y() - r.getOrigin().y()) / r.getDir().y();
		 
		if( tymin > tymax){
			tmp = tymin;
			tymin = tymax;
			tymax = tmp;
		}
		
		if ((txmin > tymax) || (tymin > txmax)) return false; 
		
		if (tymin > txmin) txmin = tymin; 
		
		if (tymax < txmax) txmax = tymax; 
		 
		 
		double tzmin = (min.z() - r.getOrigin().z()) / r.getDir().z(); 
		double tzmax = (max.z() - r.getOrigin().z()) / r.getDir().z();
		 
		if( tzmin > tzmax){
			tmp = tzmin;
			tzmin = tzmax;
			tzmax = tmp;
		} 
		
		if ((txmin > tzmax) || (tzmin > txmax)) return false; 
		 
		if (tzmin > txmin) txmin = tzmin; 
		
		if (tzmax < txmax)  txmax = tzmax;
		
		
		return true;
	}
	
	
	
	@Override
	public String toString(){
		return "Min: " + min.toString() + " Max: " + max.toString();
	}
	
}
