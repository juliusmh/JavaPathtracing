package fileio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.Vector3;
import utils.Vertex;
import world.Triangle;
import world.TriangleMesh;
import world.material.Material;

public class OBJImporter {
	
	public ArrayList<TriangleMesh> importOBJ(String s){
		
		ArrayList<TriangleMesh> meshes = new ArrayList<TriangleMesh>();
		TriangleMesh current = new TriangleMesh(null, new ArrayList<Triangle>());
		
		File f = new File(s);
		
		ArrayList<Triangle> triangles = new ArrayList<Triangle>();
		Map<String, Material> materials = new HashMap<String, Material>();
		ArrayList<Vertex> verts = new ArrayList<Vertex>(); 
		
		Material currentMaterial = new Material();
		
		try {
			
			BufferedReader buf = new BufferedReader(new FileReader(f));
			
			while (true){
				try{
					String line = buf.readLine();

					if(line == null){break;} //EOF
					
					if (line.contains("\t")){
						line = line.replaceAll("\t", " ");
					}

					String[] split = line.split(" ");
					
					if (line.length() > 0  && split.length > 0){ //SKIP BLANK LINES
							
						if (split[0].equals("g")){ //ADD A VERTEX
							
							if(current.name != null){
								if (current.bounds == null) current.bounds = current.calcBounds();
								meshes.add(current);
							}
							
							current = new TriangleMesh(split[1], new ArrayList<Triangle>());
							
						}
							
						if (split[0].equals("v")){ //ADD A VERTEX
							double[] numbers = new double[]{0,0,0};
							int k = 0;
							
							for (int i = 1; i < split.length; i++){
								String tmp = split[i];
								if (tmp.length() > 0){
									numbers[k] = Double.parseDouble(tmp);
									k++;
								}
							}
							
							verts.add(new Vertex(new Vector3(numbers[0], numbers[1], numbers[2]), null)); // Normal will be calculated later

						}
							
						if (split[0].equals("f")){ // ADD A POLYGON (WE NEED TRIANGULATED MESHES HERE) 
							
							List<Vertex> v = new ArrayList<Vertex>();
							for (int i = 1; i < split.length; i++){
								
								int pos;
								
								if (line.contains("/")){
									pos = Integer.parseInt(split[i].split("/")[0]);
								}else{
									pos  = Integer.parseInt(split[i]);
								}
								
								if (pos < 0){
									pos = verts.size() + pos;
								}else{
									pos = pos - 1;
								}
								
								v.add(verts.get(pos));
							}
							
							current.triangles.addAll(triangulate(v, currentMaterial));
						}
							
						if (split[0].equals("mtllib")){ 
							Map<String, Material> tmp = importMTL(split[1]);
							for (Material m : tmp.values()){
								materials.put(m.name, m);
							}
						}
						
						if (split[0].equals("usemtl")){
							currentMaterial = materials.get(split[1]);
						}
							
					}
					
				}catch (Exception e){
					e.printStackTrace();
				}
				
			}
			buf.close();
			
		} catch (Exception e) {}
		
		if (current.bounds == null) current.bounds = current.calcBounds();
		meshes.add(current);
		
		return meshes;
	}
	
	// Import an mtl file
	public Map<String, Material> importMTL(String s){
		Map<String, Material> mats = new HashMap<String, Material>();

		File f = new File(s);
		try {		
			BufferedReader buf = new BufferedReader(new FileReader(f));
			
			Material m = null;
			
			while (true){
				
				try{
					String line = buf.readLine();
					if(line == null){
						if (m != null && m.name != null){
							mats.put(m.name, m);
						}
						break;
					} //EOF

					line = line.trim();
					String[] split = line.split(" ");
					
					if (line.length() > 0 && split.length > 0 && !line.startsWith("#")){
						
						switch(split[0]){
							case "newmtl":
								m = new Material();
								m.name = split[1]; 
								break;
							
							case "illum" : 
								switch(Integer.parseInt(split[1])){
									case 2: m.reflectivity = 0; break;
									case 5: m.reflectivity = 1; break;
									case 7: 
										m.reflectivity = 1;
										m.transparency = 1; 
										break;
								}
								
							case "Ns": m.specularExponent = Double.parseDouble(split[1]); break;
							case "Ni": m.refractionIndex  = Double.parseDouble(split[1]); break;
							case "Ka": m.ambientColor     = new Vector3(Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3])); break;
							case "Kd": m.diffuseColor     = new Vector3(Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3])); break;
							case "Ks": m.specularColor    = new Vector3(Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3])); break;
							case "Ke": m.emissionColor    = new Vector3(Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3])); break;
						}
						
					}else{
						if (m != null && m.name != null) mats.put(m.name, m);
					}
					
				}catch (Exception e){e.printStackTrace();}
			}
			
			buf.close();
		}catch (Exception e){e.printStackTrace();}
		
		return mats;
	}
	
	public List<Triangle> triangulate(List<Vertex> verts, Material m){		
		// n-GON has exactly n-2 Triangles https://www.cs.ucsb.edu/~suri/cs235/Triangulation.pdf
		List<Triangle> triangleBuffer  = new ArrayList<Triangle>();
		
		int index = 2;
		
		Vertex p0 = verts.get(0);
		
		Vertex pHelper = verts.get(1);
		
		while (index < verts.size()){
			Vertex pTemp = verts.get(index);
			
			Triangle t = new Triangle(p0, pHelper, pTemp);
			t.material = m;
			triangleBuffer.add(t);
			pHelper = pTemp;
			index++;
		}
		
		//SORT CLOCKWISE
		return triangleBuffer;
		
	}
	
	
}
