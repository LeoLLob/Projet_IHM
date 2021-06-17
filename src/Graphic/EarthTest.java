package Graphic;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import com.interactivemesh.jfx.importer.ImportException;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.*;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

public class EarthTest extends Application {

    private static final float TEXTURE_LAT_OFFSET = -0.2f;
    private static final float TEXTURE_LON_OFFSET = 2.8f;

    @Override
    public void start(Stage primaryStage) {

        //Create a Pane et graph scene root for the 3D content
        Group root3D = new Group();
        Pane pane3D = new Pane(root3D);

        // Load geometry
        	ObjModelImporter objImporter = new ObjModelImporter();
        	try {
        		URL modelUrl = this.getClass().getResource("Earth/earth.obj");
        		objImporter.read(modelUrl);
        	} catch (ImportException e) {
        		System.out.println(e.getMessage());
        	}
        	MeshView[] meshViews = objImporter.getImport();
        	Group earth = new Group(meshViews);

        // Draw a line
        	

        // Draw an helix
        	
        	Point3D bresto = geoCoordTo3dCoord(48.447911f, -4.418539f);
        	Point3D topRight = new Point3D(bresto.getZ()-1,bresto.getX()+1,bresto.getZ());
        	Point3D bottomRight = new Point3D(bresto.getZ()+1,bresto.getX()-1,bresto.getZ());
        	Point3D bottomLeft = new Point3D(bresto.getZ()-1,bresto.getX()-1,bresto.getZ());
        	Point3D topLeft = new Point3D(bresto.getZ()+1,bresto.getX()+1,bresto.getZ());
        
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(new Color(0.5,0.0,0.0,0.1));
        AddQuadrilateral(earth, topRight, bottomRight, bottomLeft, topLeft, redMaterial);
        
        // Draw city on the earth
        	displayTown(earth, "Brest", 48.447911f, -4.418539f);

        // Add a camera group
        PerspectiveCamera camera = new PerspectiveCamera(true);
        new CameraManager(camera, pane3D, root3D);


        
        // Add point light
        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateX(-180);
        light.setTranslateY(-90);
        light.setTranslateZ(-120);
        light.getScope().addAll(root3D);
        root3D.getChildren().add(light);
        
        
  

        // Add ambient light
        AmbientLight ambientLight = new AmbientLight(Color.WHITE);
        ambientLight.getScope().addAll(root3D);
        root3D.getChildren().add(ambientLight);
        
        Animation(earth, 30);
        // Create scene
        Scene scene = new Scene(pane3D, 600, 600, false);
        scene.setCamera(camera);
        scene.setFill(Color.GREY);
        root3D.getChildren().add(earth);
        
        scene.addEventHandler(MouseEvent.ANY, event -> {
      		if (event.getEventType() == MouseEvent.MOUSE_PRESSED && event.isShiftDown()) {
      			PickResult pickResult = event.getPickResult();
      			Point3D spaceCoord = pickResult.getIntersectedPoint();
      			System.out.println(spaceCoord.getX());
      			System.out.println(spaceCoord.getY());
      			System.out.println(spaceCoord.getZ());

      			Sphere sphere = new Sphere(0.009);
      			final PhongMaterial sphereMaterial = new PhongMaterial();
      			sphereMaterial.setSpecularColor(Color.WHITE);
      			sphereMaterial.setDiffuseColor(Color.WHITE);
      			sphere.setMaterial(sphereMaterial);
      			sphere.setTranslateX(spaceCoord.getX());
      			sphere.setTranslateY(spaceCoord.getY());
      			sphere.setTranslateZ(spaceCoord.getZ());
      			earth.getChildren().add(sphere);
      		}
      	});
        
        scene.addEventHandler(MouseEvent.ANY, event -> {
      		if (event.getEventType() == MouseEvent.MOUSE_PRESSED && event.isAltDown()) {
      			PickResult pickResult = event.getPickResult();
      			Point3D spaceCoord = pickResult.getIntersectedPoint();
      			System.out.println(spaceCoord.getX());
      			System.out.println(spaceCoord.getY());
      			System.out.println(spaceCoord.getZ());
      			double latCursor = SpaceCoordToGeoCoord(spaceCoord).getX();
      			double lonCursor = SpaceCoordToGeoCoord(spaceCoord).getY();
      			System.out.println(latCursor);
      			System.out.println(lonCursor);
      			GeoHash.Location loc = new GeoHash.Location("selectedGeoHash", latCursor, lonCursor);
      			System.out.println(GeoHash.GeoHashHelper.getGeohash(loc,5));

      		}
      	});

        

        //Add the scene to the stage and show it
        primaryStage.setTitle("Earth Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }


    public Cylinder createLine(Point3D origin, Point3D target) {
        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D diff = target.subtract(origin);
        double height = diff.magnitude();

        Point3D mid = target.midpoint(origin);
        Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());

        Point3D axisOfRotation = diff.crossProduct(yAxis);
        double angle = Math.acos(diff.normalize().dotProduct(yAxis));
        Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);

        Cylinder line = new Cylinder(0.01f, height);

        line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);

        return line;
    }
    
    public static Point3D geoCoordTo3dCoord(float lat, float lon) {
        float lat_cor = lat + TEXTURE_LAT_OFFSET;
        float lon_cor = lon + TEXTURE_LON_OFFSET;
        return new Point3D(
                -Math.sin(Math.toRadians(lon_cor))
                        * Math.cos(Math.toRadians(lat_cor)),
                -Math.sin(Math.toRadians(lat_cor)),
                Math.cos(Math.toRadians(lon_cor))
                        * Math.cos(Math.toRadians(lat_cor)));
    }
    
	public void displayTown(Group parent, String name, float latitude, float longitude)
	{
		Group ville = new Group();
		ville.setId(name);
		Sphere sphere = new Sphere(0.005);
		final PhongMaterial sphereMaterial = new PhongMaterial();
		sphereMaterial.setSpecularColor(Color.RED);
		sphereMaterial.setDiffuseColor(Color.YELLOW);
		sphere.setMaterial(sphereMaterial);
		Point3D coord = geoCoordTo3dCoord(latitude, longitude);
		ville.getChildren().add(sphere);
		ville.setTranslateX(coord.getX());
		ville.setTranslateY(coord.getY());
		ville.setTranslateZ(coord.getZ());
		parent.getChildren().add(ville);
	}
	
	private void AddQuadrilateral(Group parent, Point3D topRight, Point3D bottomRight, Point3D bottomLeft, Point3D topLeft, PhongMaterial material) {
		final TriangleMesh triangleMesh = new TriangleMesh();
		final float[] points = { 
				(float)topRight.getX(), (float)topRight.getY(), (float)topRight.getZ(),
				(float)topLeft.getX(), (float)topLeft.getY(), (float)topLeft.getZ(),
				(float)bottomLeft.getX(), (float)bottomLeft.getY(), (float)bottomLeft.getZ(),
				(float)bottomRight.getX(), (float)bottomRight.getY(), (float)bottomRight.getZ()};
		final float[] texCoords = 
			{
					1,1,
					1,0,
					0,1,
					0,0
			};

		final int[] faces = {
				0, 1, 1, 0, 2, 2,
				0, 1, 2, 2, 3, 3
		};
		
		triangleMesh.getPoints().setAll(points);
		triangleMesh.getTexCoords().setAll(texCoords);
		triangleMesh.getFaces().setAll(faces);
		
		final MeshView meshView = new MeshView(triangleMesh);
		meshView.setMaterial(material);
		parent.getChildren().addAll(meshView);
	}
	
	
	public static Point2D SpaceCoordToGeoCoord(Point3D p) {
		float lat = (float)(Math.asin(-p.getY()/1.01f)*(180/Math.PI) - TEXTURE_LAT_OFFSET);
		float lon;
		
		if (p.getZ()<0) {
			lon = 180 - (float)(Math.asin(-p.getX()/(1.01f*Math.cos((Math.PI/180)
					*(lat+TEXTURE_LAT_OFFSET))))*180/Math.PI + TEXTURE_LON_OFFSET);
			}
		else {
			lon = (float) (Math.asin(-p.getX()/(1.01f*Math.cos((Math.PI/180)
				*(lat+TEXTURE_LAT_OFFSET))))*180/Math.PI - TEXTURE_LON_OFFSET);
	}
	return new Point2D(lat,lon);
	}
	
	public void Animation(Group earth, double RotationSpeed) {
    	final long startNanoTime = System.nanoTime();
    	new AnimationTimer() {
    		public void handle(long currentNanoTime) {
    			double t = (currentNanoTime - startNanoTime) / 1000000000.0;
    			earth.setRotationAxis(new Point3D(0,1,0));
    			earth.setRotate(RotationSpeed * t);
    		}
    	}.start();
    	
	}
	
	
	

}
