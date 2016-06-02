package world;

import java.util.Random;

import utils.Intersection;
import utils.Ray;
import utils.Vector3;
import world.material.Material;

public abstract class SceneObject {

	//PUBLIC VARIABLES
	public Material material;
	public BoundingBox bounds;
	
	//ABSTRACT FUNCTIONS
	public abstract Intersection hit(Ray ray);
	
	public abstract Vector3 pointInObject(Random r);
	public abstract Vector3 getCenter();
	
	public abstract void move(Vector3 v);
	public abstract void rotate(double a, double b, double c);
	public abstract void scale(double s);
	
	//CONSTRUCTORS
	public SceneObject(Material material){
		this.material = material;
	}
	
	public SceneObject(){
		//(double diffuse, double specular, double reflection, Vector3 color) {
		this.material = new Material();
	}
	
	public boolean hitBounds(Ray ray) {
		return this.bounds.hit(ray);
	}
	
}
