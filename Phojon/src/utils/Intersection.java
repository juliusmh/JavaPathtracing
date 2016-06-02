package utils;

import world.SceneObject;

public class Intersection {

	public static Intersection noIntersection = new Intersection(new Vector3(0,0,0), -1, new Vector3(0,0,0), null);
	
	private Vector3 point;
	private double distance;
	private Vector3 normal;
	private SceneObject obj;
	
	public Intersection(Vector3 point, double distance, Vector3 normal, SceneObject obj) {
		this.point = point;
		this.distance = distance;
		this.normal = normal;
		this.obj = obj;
	}
	
	public Intersection(){
		this.point = new Vector3(0,0,0);
		this.distance = -1;
		this.normal = new Vector3(0,0,0);
		this.obj = null;
	}
	
	//GETTERS AND SETTERS
	public Vector3 getPoint() {
		return point;
	}

	public void setPoint(Vector3 point) {
		this.point = point;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public Vector3 getNormal() {
		return normal;
	}

	public void setNormal(Vector3 normal) {
		this.normal = normal;
	}

	public SceneObject getObj() {
		return obj;
	}

	public void setObj(SceneObject obj) {
		this.obj = obj;
	}

	
	
}
