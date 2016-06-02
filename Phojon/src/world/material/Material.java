package world.material;

import main.CONSTANTS;
import utils.Ray;
import utils.Vector3;

public class Material {
	
	// Material Definition from https://github.com/scoffey/raytracer/blob/master/src/objects/Material.java
	// Description from http://forum.runtimedna.com/showthread.php?24827-Whats-the-difference-in-diffuse-ambient-and-specular-colors
	
	// The name of the material
	public String name = null;
	
	// The color of the mesh when it's not illuminated.
	public Vector3 ambientColor = new Vector3(0, 0, 0);
	
	// The color of the mesh when it is illuminated
	public Vector3 diffuseColor = new Vector3(0.8, 0.8, 0.8);
	
	// The index of the Light when reflected diffusely
	public double diffuseIndex = 1;

	// The fresnel Color
	public double fresnel = 0.0;
	
	// The light emmission of the Light
	public Vector3 emissionColor = new Vector3(0, 0, 0);
	
	// The color of specular Highlights
	public Vector3 specularColor = new Vector3(1, 1, 1);
	
	// The size of the specular Highlight
	public double specularExponent = 1;
	
	// The shininess of the Material
	public double shininess = 1.0;
	
	// Reflection of the index
	public double reflectivity = 0;
	
	// The transparency of the Material
	public double transparency = 0;

	// Refraction index
	public double refractionIndex = 0;
	
	// Construcotr
	public Material(){}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	
	double max = 0;
	
	public double mix(double a, double b, double mix){
		return b*mix + a * (1.0 - mix);
	}
	
	public double fresnelEffect(Vector3 inDir, Vector3 normal){
		
		if (inDir.dot(normal) > 0.0)normal = normal.mul(-1.0);
		
		double facingRatio = - inDir.dot(normal);
		
		double fresneleffect = mix(Math.pow(1.0 - facingRatio, 3.0), 1.0, fresnel); 
		
		return fresneleffect;
		
	}
	
	//----------------------------------------------------------------------------------
	// Algorithm from: 
	// http://asawicki.info/news_1301_reflect_and_refract_functions.html and
	// https://github.com/aparajit-pratap/RayTracer/blob/master/raytracer.cpp
	//----------------------------------------------------------------------------------
		
	
	public Vector3 reflect(Vector3 inDir, Vector3 normal){
		return new Vector3(inDir).sub(normal.mul(2.0 * inDir.dot(normal))).normalize(); 
	}
	
	public Vector3 refract(Vector3 inDir, Vector3 normal){
		Vector3 out, v1,v2,v3;
		
		double ratio, dot, factor;
		
		if (inDir.dot(normal) < 0.0){
			ratio = 1.0/refractionIndex;
		}else{
			ratio = refractionIndex;
			normal = normal.mul(-1.0);
		}
		
		dot = inDir.dot(normal);
		
		factor = Math.pow((1 - (ratio*ratio* (1 - dot*dot))), 0.5);
		
		v1 = normal.mul(dot * -1).add(inDir);
		v2 = v1.mul(ratio);
		
		if( (1.0 - (ratio*ratio* (1.0 - dot*dot)) ) > 0.0){
			v3 = normal.mul(factor);
			out = v2.sub(v3).normalize();
		}
		
		v3 = normal.mul(factor);
		out = v2.sub(v3).normalize();
		
		return out;
		
	}
	

	
}
