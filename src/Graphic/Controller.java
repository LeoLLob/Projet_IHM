package Graphic;

import java.net.URL;
import App.Requete;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.json.JSONArray;

public class Controller implements Initializable {

	@FXML
	private Pane pane3D;

	@FXML
	private RadioButton idZone;

	@FXML
	private RadioButton idNom;

	@FXML
	private TextField idRegion;

	@FXML
	private ComboBox<String> idEsp;

	@FXML
	private CheckBox idDate;

	@FXML
	private DatePicker idDateBox;

	@FXML
	private TextField idDuree;

	@FXML
	private TextField idInterv;

	@FXML
	private Slider idSlider;

	@FXML
	private Button idSearch;

	@FXML
	private TextArea idConsole;

	@FXML
	private TextField idEspece;


	private static final float TEXTURE_LAT_OFFSET = -0.2f;
	private static final float TEXTURE_LON_OFFSET = 2.8f;


	@Override
	public void initialize(URL location, ResourceBundle resources) {

		//TextFields.bindAutoCompletion(idEspece, "salut", "yo", "ça va?");

		//Create a Pane et graph scene root for the 3D content
		Group root3D = new Group();

		idConsole.setEditable(false);
		idSearch.setDisable(true);
		idDateBox.setDisable(true);
		idInterv.setDisable(true);
		idDuree.setDisable(true);

		if(idNom.isSelected()) {
			idRegion.setDisable(true);
		}

		ToggleGroup GroupeSearch = new ToggleGroup();
		idZone.setToggleGroup(GroupeSearch);
		idNom.setToggleGroup(GroupeSearch);

		idNom.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if(idNom.isSelected()){
					idRegion.setDisable(true);
					idDate.setDisable(false);
					idSlider.setDisable(false);

				}

				if(GroupeSearch!=null){
					GroupeSearch.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
						public void changed(
								ObservableValue<? extends Toggle> arg0,
								Toggle arg1, Toggle arg2) {
						}
					});
				}
			}
		});

		idDate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if(idDate.isSelected()){
					idDateBox.setDisable(false);
					idInterv.setDisable(false);
					idDuree.setDisable(false);
				}
				else {
					idDateBox.setDisable(true);
					idInterv.setDisable(true);
					idDuree.setDisable(true);
				}
			}
		});
		
		idEspece.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
					idSearch.disableProperty().bind(Bindings.createBooleanBinding(() -> 
				    idEspece.getText().trim().isEmpty()));
					if(idRegion.getText().trim().isEmpty() && idZone.isSelected()){
						idConsole.appendText("Recherche incomplète : il manque le nom de la région !\n									********************************** \n");
						idConsole.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
					}

				}
			});

		idEspece.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
				idEsp.getItems().clear();
				App.Requete requete = new Requete();
				String URL = App.Requete.getURLNom(idEspece.getText());
				JSONArray Json = App.Requete.readJsonFromUrlListeNom(URL);
				requete.listeNom(Json);
				idEsp.getItems().addAll(requete.getListeNom());
				idEsp.show();
			}
		});

		idEsp.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				idEspece.setText(idEsp.getValue());
			}
		});

		idRegion.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
					idSearch.disableProperty().bind(Bindings.createBooleanBinding(() -> 
				    idRegion.getText().trim().isEmpty()));
				}
			});
		

		idZone.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if(idZone.isSelected()){
					idDate.setDisable(true);
					idDateBox.setDisable(true);
					idDuree.setDisable(true);
					idInterv.setDisable(true);
					idSlider.setDisable(true);
					idRegion.setDisable(false);
					idDate.setSelected(false);
				}

				if(GroupeSearch!=null){
					GroupeSearch.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
						public void changed(
								ObservableValue<? extends Toggle> arg0,
								Toggle arg1, Toggle arg2) {
						}
					});
				}
			}
		});

		//Importation de la Terre
		ObjModelImporter objImporter = new ObjModelImporter();
		try {
			URL modelUrl = this.getClass().getResource("Earth/earth.obj");
			objImporter.read(modelUrl);
		} catch (ImportException e) {
			System.out.println(e.getMessage());
		}
		MeshView[] meshViews = objImporter.getImport();
		Group earth = new Group(meshViews);


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

		root3D.getChildren().add(earth);

		//Animation de la Terre
		Animation(earth, 30);

		// Create subscene
		SubScene subscene = new SubScene(root3D, 600, 600, true, SceneAntialiasing.BALANCED);
		subscene.setCamera(camera);
		subscene.setFill(Color.GREY);
		pane3D.getChildren().addAll(subscene);


		subscene.addEventHandler(MouseEvent.ANY, event -> {
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


		subscene.addEventHandler(MouseEvent.ANY, event -> {
			if (event.getEventType() == MouseEvent.MOUSE_PRESSED && event.isAltDown()) {
				PickResult pickResult = event.getPickResult();
				Point3D spaceCoord = pickResult.getIntersectedPoint();
				Double x = spaceCoord.getX();
				Double y = spaceCoord.getY();
				Double z = spaceCoord.getZ();
				double latCursor = SpaceCoordToGeoCoord(spaceCoord).getX();
				double lonCursor = SpaceCoordToGeoCoord(spaceCoord).getY();
				Double latitude = latCursor;
				Double longitude = lonCursor;
				GeoHash.Location loc = new GeoHash.Location("selectedGeoHash", latCursor, lonCursor);
				String Hash = GeoHash.GeoHashHelper.getGeohash(loc,5);
				idConsole.setStyle("-fx-text-fill: black; -fx-font-size: 13px;");
				idConsole.appendText("Coordonnée en x : " + x + "\n" + "Coordonnée en y : " + y + "\n" 
						+ "Coordonnée en z : " + z + "\n" + "Longitude : " + longitude + "\n" + "Latitude : " + latitude + "\n"
						+ "Geohash : " + Hash + "\n" + 
						"									********************************** \n");
			}
		});


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
				double t = (currentNanoTime - startNanoTime) / 10000000000.0;
				earth.setRotationAxis(new Point3D(0,1,0));
				earth.setRotate(RotationSpeed * t);
			}
		}.start();
	}
 /*
	public void majComboBox(String nom) {
		ObservableList<String> Liste = FXCollections.observableArrayList();
		// finir en utilisant la fonction déjà donnée pour les 20 premiers
		getURLNom(String chaine);
		JSONArray arrayjson = JSONArray readJsonFromUrlListeNom(String url);
		listeNom(JSONArray JsonRoot);
		idEsp.setItems(Liste);
	}


	public void update() {
		idEspece.textProperty().addListener((observable, oldvalue, newvalue) ->
		majComboBox(newvalue));}

	public void keyTyped(KeyEvent e) {
		majComboBox(getNomEsp());
	}

	public String getNomEsp() {
		return idEspece.getText();
	}

	public void setNomEsp(String nom) {
		idEspece.setText(nom);
	}
*/
	
	
}


