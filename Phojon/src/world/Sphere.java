package world;

import java.util.Random;

import utils.Intersection;
import utils.Ray;
import utils.Vector3;
import world.material.Material;

public class Sphere extends SceneObject {
	
	private Vector3 center;
	private double radius;
	
	public Sphere(Vector3 center, double radius) {
		super();
		this.center = center;
		this.radius = radius;
		
		this.bounds = new BoundingBox(center.sub(new Vector3(radius,radius,radius)), center.add(new Vector3(radius,radius,radius)));
		
	}

	private Vector3 getNormal(Vector3 b){
		return (b.sub(this.center).normalize());
	}
	
	@Override
	public Intersection hit(Ray ray) {
		
		Vector3 origin = ray.getOrigin();
		Vector3 dir = ray.getDir();
		
		double q = Math.pow(dir.dot(origin.sub(this.center)),2) - origin.sub(this.center).dot(origin.sub(this.center)) + Math.pow(this.radius,2);
		
		if(q < 0){
			return Intersection.noIntersection;
		}else{
			double d = -1 * dir.dot(origin.sub(this.center));
			double d1 = d - Math.sqrt(q);
			double d2 = d + Math.sqrt(q);
			
			if (0 < d1 && ( d1 < d2 || d2 < 0)){
				Intersection i = new Intersection(new Vector3(0,0,0), -1, new Vector3(0,0,0), this);
				i.setPoint(origin.add(dir.mul(d1)));
				i.setDistance(d1);
				i.setNormal(this.getNormal(origin.add(dir.mul(d1))));
				return i;
				
			}else if( 0 < d2 && ( d2 < d1 || d1 < 0)){
				Intersection i = new Intersection(new Vector3(0,0,0), -1, new Vector3(0,0,0), this);
				i.setPoint(origin.add(dir.mul(d2)));
				i.setDistance(d2);
				i.setNormal(this.getNormal(origin.add(dir.mul(d2))));
				return i;
				
			}else{
				return Intersection.noIntersection;
			}
		}
	}

	@Override
	public Vector3 pointInObject(Random r) {
		return this.center;
	}
	
	@Override
	public Vector3 getCenter() {
		return this.center;
	}
	
	@Override
	public void move(Vector3 v) {
		this.center = this.center.add(v);
	}

	@Override
	public void rotate(double a, double b, double c) {
		// TODO Auto-generated method stub
	}

	@Override
	public void scale(double s) {
		this.radius = this.radius * s;
	}



}
