package Graphic;

import java.net.URL;

import App.RechercheNom;
import App.Requete;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import com.interactivemesh.jfx.importer.ImportException;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.*;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

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

	@FXML
	private TextField legend0;

	@FXML
	private TextField legend1;

	@FXML
	private TextField legend2;

	@FXML
	private TextField legend3;

	@FXML
	private TextField legend4;

	@FXML
	private TextField legend5;

	@FXML
	private TextField legend6;

	@FXML
	private TextField legend7;

	@FXML
	private Rectangle rec0;

	@FXML
	private Rectangle rec1;

	@FXML
	private Rectangle rec2;

	@FXML
	private Rectangle rec3;

	@FXML
	private Rectangle rec4;

	@FXML
	private Rectangle rec5;

	@FXML
	private Rectangle rec6;

	@FXML
	private Rectangle rec7;

	@FXML
	private Button idStart;

	@FXML
	private Button idPause;

	@FXML
	private Button idReset;

	@FXML
	private ListView list;


	private static final float TEXTURE_LAT_OFFSET = -0.2f;
	private static final float TEXTURE_LON_OFFSET = 2.8f;


	@Override
	public void initialize(URL location, ResourceBundle resources) {

		//TextFields.bindAutoCompletion(idEspece, "salut", "yo", "ça va?");

		//Create a Pane et graph scene root for the 3D content
		Group root3D = new Group();

		legend0.setEditable(false);
		legend1.setEditable(false);
		legend2.setEditable(false);
		legend3.setEditable(false);
		legend4.setEditable(false);
		legend5.setEditable(false);
		legend6.setEditable(false);
		legend7.setEditable(false);

		idConsole.setEditable(false);

		idDateBox.setDisable(true);
		idInterv.setDisable(true);
		idDuree.setDisable(true);

		list.setVisible(false);

		if (idNom.isSelected()) {
			idRegion.setDisable(true);
		}

		ToggleGroup GroupeSearch = new ToggleGroup();
		idZone.setToggleGroup(GroupeSearch);
		idNom.setToggleGroup(GroupeSearch);


		idNom.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (idNom.isSelected()) {
					idRegion.setDisable(true);
					idDate.setDisable(false);
					idSlider.setDisable(false);

				}

				if (GroupeSearch != null) {
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
				if (idDate.isSelected()) {
					idDateBox.setDisable(false);
					idInterv.setDisable(false);
					idDuree.setDisable(false);
				} else {
					idDateBox.setDisable(true);
					idInterv.setDisable(true);
					idDuree.setDisable(true);
				}
			}
		});

		idZone.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (idZone.isSelected()) {
					idDate.setDisable(true);
					idDateBox.setDisable(true);
					idDuree.setDisable(true);
					idInterv.setDisable(true);
					idSlider.setDisable(true);
					idRegion.setDisable(false);
					idDate.setSelected(false);
				}

				if (GroupeSearch != null) {
					GroupeSearch.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
						public void changed(
								ObservableValue<? extends Toggle> arg0,
								Toggle arg1, Toggle arg2) {
						}
					});
				}
			}
		});

		/*idEspece.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				idSearch.disableProperty().bind(Bindings.createBooleanBinding(() ->
						idEspece.getText().isEmpty()));
				if (idRegion.getText().trim().isEmpty() && idZone.isSelected()) {
					idConsole.appendText("Recherche incomplète : il manque le nom de la région !\n									********************************** \n");
					idConsole.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
				}

			}
		});

		 */

		idEspece.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
				idEsp.getItems().clear();
				App.Requete.listeNom(idEspece.getText());
				idEsp.getItems().addAll(App.Requete.getListeNom());
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

		// Add a camera group
		PerspectiveCamera camera = new PerspectiveCamera(true);
		new CameraManager(camera, pane3D, root3D);

		/*// Add point light
		PointLight light = new PointLight(Color.WHITE);
		light.setTranslateX(-180);
		light.setTranslateY(-90);
		light.setTranslateZ(-120);
		light.getScope().addAll(root3D);
		root3D.getChildren().add(light);

		 */

		// Add ambient light
		AmbientLight ambientLight = new AmbientLight(Color.WHITE);
		ambientLight.getScope().addAll(root3D);
		root3D.getChildren().add(ambientLight);

		root3D.getChildren().add(earth);

		//Animation de la Terre
		//Animation(earth, 30);

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
				String Hash = GeoHash.GeoHashHelper.getGeohash(loc, 5);
				idConsole.setStyle("-fx-text-fill: black; -fx-font-size: 13px;");
				idConsole.appendText("Coordonnée en x : " + x + "\n" + "Coordonnée en y : " + y + "\n"
						+ "Coordonnée en z : " + z + "\n" + "Longitude : " + longitude + "\n" + "Latitude : " + latitude + "\n"
						+ "Geohash : " + Hash + "\n" +
						"									********************************** \n");
			}
		});

		App.Requete.startApp();
		majLegende();
		afficheEspNom(App.Requete.getListeRechercheNom(), earth);


		idSearch.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {

				list.setVisible(false);
				earth.getChildren().remove(1, earth.getChildren().size());

				if (idNom.isSelected()) {
					if (idDate.isSelected()) {

					} else {
						App.Requete.creerRechercheNom(idEspece.getText(), (int) idSlider.getValue());
						majLegende();
						afficheEspNom(App.Requete.getListeRechercheNom(), earth);


					}
				} else {
					App.Requete.creerRechercheZone(idEspece.getText(), idRegion.getText());
					majLegende();

				}
			}
		});


		subscene.addEventHandler(MouseEvent.ANY, event -> {
			if (event.getEventType() == MouseEvent.MOUSE_PRESSED && event.isControlDown()) {
				PickResult pickResult = event.getPickResult();
				Point3D spaceCoord = pickResult.getIntersectedPoint();
				Double x = spaceCoord.getX();
				Double y = spaceCoord.getY();
				Double z = spaceCoord.getZ();
				double latCursor = SpaceCoordToGeoCoord(spaceCoord).getX();
				double lonCursor = SpaceCoordToGeoCoord(spaceCoord).getY();
				GeoHash.Location loc = new GeoHash.Location("selectedGeoHash", latCursor, lonCursor);
				String Hash = GeoHash.GeoHashHelper.getGeohash(loc, 3);
				App.Requete.creerRechercheZone("", Hash);
				idConsole.setStyle("-fx-text-fill: black; -fx-font-size: 13px;");
				for (App.RechercheZone rechercheZone : App.Requete.getListeRechercheZone()) {
					idConsole.appendText("scientificName : " + rechercheZone.getScientificName() + " / " + "order : " + rechercheZone.getOrder() + " / " +
							"superclass : " + rechercheZone.getSuperclass() + " / " + "recordedBy : " + rechercheZone.getRecordedBy() + " / " + "species : " + rechercheZone.getSpecies()
							+ " \n");
				}
				list.setVisible(true);
				ObservableList<String> items = FXCollections.observableArrayList("");
				for (App.RechercheZone rechercheZone : App.Requete.getListeRechercheZone()) {
					list.getItems().add(rechercheZone.getScientificName());
				}
				list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
						idEspece.setText(list.getSelectionModel().getSelectedItem().toString());
					}
				});
			}
		});
	}

	public void afficheEspNom (ArrayList< RechercheNom > listeRechercheNom, Group earth){


		for(App.RechercheNom rechercheNom : listeRechercheNom) {

			int occurence = rechercheNom.getOccurence();
			Color color = choixCouleur(occurence);
			PhongMaterial phongMaterial = new PhongMaterial();
			phongMaterial.setDiffuseColor(color);

			AddQuadrilateral(earth,
					geoCoordTo3dCoord((float) rechercheNom.getCoord().get(3).getY(), (float) rechercheNom.getCoord().get(3).getX()),
					geoCoordTo3dCoord((float) rechercheNom.getCoord().get(0).getY(), (float) rechercheNom.getCoord().get(0).getX()),
					geoCoordTo3dCoord((float) rechercheNom.getCoord().get(1).getY(), (float) rechercheNom.getCoord().get(1).getX()),
					geoCoordTo3dCoord((float) rechercheNom.getCoord().get(2).getY(), (float) rechercheNom.getCoord().get(2).getX()),
					phongMaterial);

		}
	}

	private void majLegende()
	{
		int ecart = (App.Requete.getMaxOccurence() - App.Requete.getMinOccurence()) / 8;
		legend0.setText(Integer.toString(App.Requete.getMinOccurence()));
		legend1.setText(Integer.toString(App.Requete.getMinOccurence() + 1 * ecart));
		legend2.setText(Integer.toString(App.Requete.getMinOccurence() + 2 * ecart));
		legend3.setText(Integer.toString(App.Requete.getMinOccurence() + 3 * ecart));
		legend4.setText(Integer.toString(App.Requete.getMinOccurence() + 4 * ecart));
		legend5.setText(Integer.toString(App.Requete.getMinOccurence() + 5 * ecart));
		legend6.setText(Integer.toString(App.Requete.getMinOccurence() + 6 * ecart));
		legend7.setText(Integer.toString(App.Requete.getMinOccurence() + 7 * ecart));
	}

	private Color choixCouleur(int occurence)
	{
		if (occurence >= Integer.parseInt(legend0.getText())  && occurence < Integer.parseInt(legend1.getText())) return (Color) rec0.getFill();
		else if (occurence >= Integer.parseInt(legend1.getText()) && occurence < Integer.parseInt(legend2.getText())) return (Color) rec1.getFill();
		else if (occurence >= Integer.parseInt(legend2.getText()) && occurence < Integer.parseInt(legend3.getText())) return (Color) rec2.getFill();
		else if (occurence >= Integer.parseInt(legend3.getText()) && occurence < Integer.parseInt(legend4.getText())) return (Color) rec3.getFill();
		else if (occurence >= Integer.parseInt(legend4.getText()) && occurence < Integer.parseInt(legend5.getText())) return (Color) rec4.getFill();
		else if (occurence >= Integer.parseInt(legend5.getText()) && occurence < Integer.parseInt(legend6.getText())) return (Color) rec5.getFill();
		else if (occurence >= Integer.parseInt(legend6.getText()) && occurence < Integer.parseInt(legend7.getText())) return (Color) rec6.getFill();
		else   return (Color) rec7.getFill();

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
						* Math.cos(Math.toRadians(lat_cor))*1.01,
				-Math.sin(Math.toRadians(lat_cor))*1.01,
				Math.cos(Math.toRadians(lon_cor))
						* Math.cos(Math.toRadians(lat_cor))*1.01);
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
				(float)bottomLeft.getX(), (float)bottomLeft.getY(), (float)bottomLeft.getZ(),
				(float)topLeft.getX(), (float)topLeft.getY(), (float)topLeft.getZ(),
				(float)topRight.getX(), (float)topRight.getY(), (float)topRight.getZ(),
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

/*
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

 */
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


