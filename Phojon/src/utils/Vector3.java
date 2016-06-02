package utils;

import java.awt.Color;
import java.util.Arrays;
import java.util.Random;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.IntToDoubleFunction;

public class Vector3 {

	public static Vector3 zeroVector = new Vector3(0,0,0);
	public static Vector3 oneVector = new Vector3(0,0,0);
	
	public double[] xyz = new double[3];
	
	// Constructors
	public Vector3(){}
	
	public Vector3(double x, double y, double z){
		xyz = new double[]{x,y,z};
	}
	
	public Vector3(Vector3 p){
		for (int i = 0; i < 3; i++) this.xyz[i] = p.xyz[i];
	}
	
	public Vector3 clone(){
		return new Vector3(this);
	}
	
	
	
	// Getters
	public double x(){
		return this.xyz[0];
	}
	public double y(){
		return this.xyz[1];
	}
	public double z(){
		return this.xyz[2];
	}
	
	
	
	// Setters
	public void setX(double x) {
		this.xyz[0] = x;
	}

	public void setY(double y) {
		this.xyz[1] = y;
	}

	public void setZ(double z) {
		this.xyz[2] = z;
	}
	
	
	
	// Mathematical Operators
	public Vector3 mul(double d){
		Vector3 tmp = new Vector3();
		for (int i = 0; i < 3; i++) tmp.xyz[i] = this.xyz[i] * d;
		return tmp;
	}
	
	public Vector3 add(Vector3 v){
		Vector3 tmp = new Vector3();
		for (int i = 0; i < 3; i++) tmp.xyz[i] = this.xyz[i] + v.xyz[i];
		return tmp;
	}
	
	public Vector3 sub(Vector3 v){
		Vector3 tmp = new Vector3();
		for (int i = 0; i < 3; i++) tmp.xyz[i] = this.xyz[i] - v.xyz[i];
		return tmp;
	}
	
	public Vector3 mul(Vector3 v){
		Vector3 tmp = new Vector3();
		for (int i = 0; i < 3; i++) tmp.xyz[i] = this.xyz[i] * v.xyz[i];
		return tmp;
	}
	
	public double dot(Vector3 v){
		double sum = 0;
		for (int i = 0; i < 3; i++) sum += this.xyz[i] * v.xyz[i];
		return sum;
	}
	
	public Vector3 cross(Vector3 v){
		return new Vector3(this.y() * v.z() - this.z() * v.y(),
						   this.z() * v.x() - this.x() * v.z(),
				           this.x() * v.y() - this.y() * v.x());
	}
	
	public Vector3 normalize(){
		double m = this.magnitude();
		
		for (int i = 0; i < 3; i++) this.xyz[i] = this.xyz[i] / m;
		
		return this;
	}
	
	public double magnitude(){
		return  Math.sqrt(this.x()*this.x() + this.y()*this.y() + this.z()*this.z());
	}
	
	public boolean isEmpty(){
		if(this.x()*this.x() + this.y()*this.y() + this.z()*this.z() > 0) return false;
		return true;
	}
	
	//FUNCTIONS MISC
	public Vector3 clampColor(){
		return new Vector3(Math.max(0.0, Math.min(this.x(), 1.0)),
						   Math.max(0.0, Math.min(this.y(), 1.0)),
				           Math.max(0.0, Math.min(this.z(), 1.0)));
	}
	
	public Color toColor(){
		Vector3 res = clampColor();
		for (int i = 0; i < 3; i++) res.xyz[i] *= 255;
		return new Color((int)res.x(), (int)res.y(), (int)res.z());
	}
	
	//-----------------------------------------------
	// MISC FUNCTIONS 
	//-----------------------------------------------
	
	@Override
	public String toString(){
		return "("+this.x()+","+this.y()+","+this.z()+")";
	}
	
}

