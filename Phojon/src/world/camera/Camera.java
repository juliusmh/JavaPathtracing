package world.camera;

import utils.Ray;
import utils.Vector3;

public class Camera {

	public Vector3 eyePoint; //
	private Vector3 viewPlaneBottomLeftPoint ; //
	private Vector3 xIncVector;//
	private Vector3 yIncVector;//
	
	/* 
	 * Create Camera Rays as described at
	 * https://steveharveynz.wordpress.com/2012/12/20/ray-tracer-part-two-creating-the-camera/
	 * using an UV camera model (https://steveharveynz.files.wordpress.com/2012/12/camera2.jpg)
	*/
	
	public Camera(Vector3 eyePoint, Vector3 lookAtPoint, double fov, double xResolution, double yResolution){
		this.eyePoint = eyePoint;
		
		Vector3 viewDirection = lookAtPoint.sub(eyePoint);
		Vector3 u = viewDirection.cross(new Vector3(0,1,0)).normalize(); //pointing left on the image
		Vector3 v = u.cross(viewDirection).normalize(); //pointing up on the image
		
		double viewPlaneHalfWidth = Math.tan(fov / 2);
		double aspectRatio = yResolution / xResolution;
		double viewPlaneHalfHeight = aspectRatio * viewPlaneHalfWidth;
		
		viewPlaneBottomLeftPoint = lookAtPoint.sub(v.mul(viewPlaneHalfHeight)).sub(u.mul(viewPlaneHalfWidth));
		
		xIncVector = u.mul(2).mul(viewPlaneHalfWidth).mul(1 / xResolution);//(U*2*halfWidth)/xResolution;
		yIncVector = v.mul(2).mul(viewPlaneHalfHeight).mul(1 / yResolution);//(U*2*halfWidth)/xResolution;
		
	}
	
	public Ray generateRay(double x, double y){
		Vector3 viewPlanePoint = viewPlaneBottomLeftPoint.add(xIncVector.mul(x)).add(yIncVector.mul(y));
		return new Ray(eyePoint, viewPlanePoint.sub(eyePoint).normalize());
	}
	
}
