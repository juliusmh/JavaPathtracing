package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import fileio.Logger;
import fileio.OBJImporter;
import renderer.DirectOnlyRenderer;
import renderer.PathTrace2Renderer;
import renderer.PathTraceRenderer;
import renderer.Renderer;
import utils.Vector3;
import world.Scene;
import world.Sphere;
import world.Triangle;
import world.TriangleMesh;
import world.camera.Camera;

public class Phojon {

	public static Logger logger;
	
	public static Scene BallScene(int x, int y){
		Scene s = new Scene();
		OBJImporter importer = new OBJImporter();
		
		ArrayList<TriangleMesh> m = importer.importOBJ("CornellBox-Empty-RG.obj");
		
		for (TriangleMesh mesh : m){
			//System.out.println(mesh.name + " -> " + mesh.triangles.size() + " Bounds: " + mesh.bounds);
			mesh.setBackfaceCulling(true);
			s.addMesh(mesh);
		}
		
		// b1
		Sphere ball = new Sphere(new Vector3(0.6, 0.5, -0), 0.4);
		ball.material.transparency = 0.0;
		ball.material.refractionIndex = 1.5;
		
		ball.material.reflectivity = 1.0; // Fade between diffuse and Reflectivity
		ball.material.shininess = 1.0; // Sharp Reflections?
		
		ball.material.fresnel = 1.0;
		
		ball.material.diffuseColor = new Vector3(1,0,0);
		
		s.addMesh(ball);
		
		// b2
		/*
		Sphere ball2 = new Sphere(new Vector3(-0.4, 0.5, 0.5), 0.4);
		ball2.material.transparency = 1.0;
		ball2.material.reflectivity = 1.0;
		ball2.material.refractionIndex = 1.5;
		
		ball2.material.fresnel = 0.05;

		ball2.material.diffuseColor = new Vector3(0,0,0);
		
		s.addMesh(ball2);
		*/
		
		// room
		s.addMesh(m.get(0));
		
		s.setCam(new Camera(new Vector3(0, 1 , 3), new Vector3(0, 1, -1), 2.3, x,y));
		
		return s;
	}
	
	public static Scene DemoScene(int x, int y){
		Scene s = new Scene();
		OBJImporter importer = new OBJImporter();
		
		ArrayList<TriangleMesh> m = importer.importOBJ("CornellBox-Original.obj");
		
		for(Triangle t : m.get(0).triangles){
			t.material.reflectivity = 1.0;
			t.material.fresnel = 1.0; // Only Reflection
			t.material.shininess = 0.8; // Affects the Specular BRDF below the Reflections
		}
		
		for (TriangleMesh mesh : m){
			System.out.println(mesh.name + " -> " + mesh.triangles.size() + " Bounds: " + mesh.bounds);
			mesh.setBackfaceCulling(true);
			s.addMesh(mesh);
		}
		
		s.setCam(new Camera(new Vector3(0, 1 , 3), new Vector3(0, 1, -1), 2.3, x,y));
		
		return s;
	}
	
	public static void main(String[] args){
		logger = new Logger();
		logger.logi("Phojon started");
	
		int x = 400;
		int y = 400;
	
		Scene s = BallScene(x,y);
		
		new PathTraceRenderer(x, y, s, 4, 5000).startLive();
		
		//Renderer r2 = new PathTrace2Renderer(x,y,s, 10);
		
		//try {ImageIO.write(r2.render(), "PNG", new File("render.png"));} catch (IOException e) {}
		
		//logger.logi("Phojon finished");
		
	}
	
}
