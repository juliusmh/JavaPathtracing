package utils;

import java.util.ArrayList;
import java.util.List;

import world.SceneObject;

public class Sampler {

	public java.util.Random rand;
	
	private double Xi1,Xi2,theta,phi,xs,ys,zs;
	private Vector3 y,h,x,z, dir;
	
	public Sampler(int seed){
		this.rand = new java.util.Random(seed);	
	}
		
	// cosWeightedRandomHemisphere Sampling
	public Vector3 diffuseSample(Vector3 n){
	    Xi1 = rand.nextDouble();
	    Xi2 = rand.nextDouble();

	    theta = Math.acos(Math.sqrt(1.0-Xi1));
	    phi = 2.0 * Math.PI * Xi2;

	    xs = Math.sin(theta) * Math.cos(phi);
	    ys = Math.cos(theta);
	    zs = Math.sin(theta) * Math.sin(phi);

	    y = new Vector3(n);
	    h = y.clone();
	    
	    if (Math.abs(h.x()) <= Math.abs(h.y()) && Math.abs(h.x()) <= Math.abs(h.z())){
	        h.setX(1.0);
	    }else if (Math.abs(h.y()) <= Math.abs(h.x()) && Math.abs(h.y()) <= Math.abs(h.z())){
	        h.setY(1.0);
		} else{
	        h.setZ(1.0);
	    }

	    x = new Vector3(h.cross(y)).normalize();
	    z = new Vector3(x.cross(y)).normalize();

	    dir = x.mul(xs).add(y.mul(ys)).add(z.mul(zs)); //xs * x + ys * y + zs * z;
	    return dir.normalize();
	}

	public Vector3 specularSample(Vector3 in, Vector3 normal, double shininess){
		if(shininess == 0.0) return diffuseSample(normal);
		if(shininess == 1.0) return in;
		
		return in.mul(shininess).add(diffuseSample(normal).mul(1.0 - shininess)).normalize();
	}
	
	public Vector3 lightSample(Vector3 origin, List<SceneObject> lights){
		int light = rand.nextInt(lights.size());
		return lights.get(light).pointInObject(rand).sub(origin).normalize();
	}
	
}

	
	
	
