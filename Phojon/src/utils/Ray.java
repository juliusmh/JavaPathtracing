package utils;

public class Ray {

	private Vector3 origin; //The origin of the Ray
	private Vector3 dir; //The direction of the Ray
	
	public Ray(Vector3 origin, Vector3 dir) {
		this.origin = origin;
		this.dir = dir;
	}

	//GETTERS AND SETTERS
	public Vector3 getOrigin() {
		return origin;
	}

	public void setOrigin(Vector3 origin) {
		this.origin = origin;
	}

	public Vector3 getDir() {
		return dir;
	}

	public void setDir(Vector3 dir) {
		this.dir = dir;
	}
	
	
	
}
