package Graphic;

import java.net.URL;

import App.RechercheDate;
import App.RechercheNom;
import App.Requete;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class Controller implements Initializable {

	@FXML
	private Pane pane3D;

	@FXML
	private ComboBox<String> idEsp;

	@FXML
	private CheckBox idDate;

	@FXML
	private DatePicker idDateBox;

	@FXML
	private DatePicker idDateBox1;

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
	private TextField legend8;

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

	static boolean pause = false;
	static boolean stop = false;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

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
		legend8.setEditable(false);

		idPause.setDisable(true);
		idReset.setDisable(true);
		idStart.setDisable(true);

		idConsole.setEditable(false);

		idDateBox.setDisable(true);
		idDateBox1.setDisable(true);

		list.setVisible(false);


		idDate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (idDate.isSelected()) {
					idDateBox.setDisable(false);
					idDateBox1.setDisable(false);
					idStart.setDisable(false);
				} else {
					idDateBox.setDisable(true);
					idDateBox1.setDisable(true);
					idStart.setDisable(true);
				}
			}
		});


		idEspece.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
				idEsp.getItems().clear();
				App.Requete.listeNom(idEspece.getText());

				if(!App.Requete.getListeNom().isEmpty()) {
					idEsp.getItems().addAll(App.Requete.getListeNom());

					idEsp.show();
				}

			}
		});

		idEsp.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String nom = idEsp.getSelectionModel().getSelectedItem();
				idEspece.setText(nom);
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
		idConsole.appendText("Recherche effectuée : Selachii\nPrécision : 3");


		idSearch.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {

				list.setVisible(false);
				idConsole.clear();
				earth.getChildren().remove(1, earth.getChildren().size());

				if (nomScientifiqueExiste(idEspece.getText())) {
					if (idDate.isSelected() ) {
						if(idDateBox.getValue() != null && idDateBox1.getValue() != null
								&& idDateBox.getValue().compareTo(idDateBox1.getValue()) < 0) {

							App.Requete.creerRechercheDate(idEspece.getText(), (int) idSlider.getValue(), idDateBox.getValue().toString(), idDateBox1.getValue().toString());
							majLegende();
							afficheEspDate(App.Requete.getListeRechercheDate(), earth);
							idConsole.appendText("Recherche effectuée : " + idEspece.getText() + "\nPrécision : " + (int) idSlider.getValue()
									+ "\nEntre le " + idDateBox.getValue().toString() + " et le " + idDateBox1.getValue().toString());
						}else{
							idConsole.appendText("Les dates sont incorrectes");
						}

					} else {
						App.Requete.creerRechercheNom(idEspece.getText(), (int) idSlider.getValue());
						majLegende();
						afficheEspNom(App.Requete.getListeRechercheNom(), earth);
						idConsole.appendText("Recherche effectuée : " + idEspece.getText() + "\nPrécision : " + (int) idSlider.getValue() + "\n");
					}

				} else {
					idConsole.appendText("Le nom d'espece rentré n'existe pas");
				}
			}
		});


		idStart.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (idDateBox.getValue() != null && idDateBox1.getValue() != null && idDateBox1.getValue().getYear()-idDateBox.getValue().getYear() >= 5) {

					new Thread(new Runnable() {
						@Override
						public void run() {
							idStart.setDisable(true);
							idReset.setDisable(false);
							idPause.setDisable(false);
							idEspece.setDisable(true);
							idDateBox.setDisable(true);
							idDateBox1.setDisable(true);
							idDate.setDisable(true);
							idSlider.setDisable(true);
							idSearch.setDisable(true);

							LocalDate localDate = idDateBox.getValue();
							while (localDate.getYear() <= idDateBox1.getValue().getYear()) {
								String debut = localDate.toString();
								localDate = localDate.withYear(localDate.getYear() + 5);
								String fin = localDate.toString();
								if(localDate.compareTo(idDateBox1.getValue()) > 0)
								{
									fin = idDateBox1.getValue().toString();
								}

								Requete.creerRechercheDate(idEspece.getText(), (int) idSlider.getValue(), debut, fin);
								majLegende();
								idConsole.clear();
								idConsole.appendText("Recherche effectuée : " + idEspece.getText() + "\nPrécision : " + (int) idSlider.getValue()
										+ "\nEntre le " + debut + " et le " + fin);

								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										earth.getChildren().remove(1, earth.getChildren().size());
										afficheEspDate(Requete.getListeRechercheDate(), earth);

									}
								});

								while(pause)
								{
									if(stop)
									{
										break;
									}
									try {
										Thread.sleep(100);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}

								if(stop)
								{
									break;
								}

								try {
									Thread.sleep(300);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}


							idPause.setDisable(true);
							idReset.setDisable(true);
							idStart.setDisable(false);
							idEspece.setDisable(false);
							idDateBox.setDisable(false);
							idDateBox1.setDisable(false);
							idDate.setDisable(false);
							idSlider.setDisable(false);
							idSearch.setDisable(false);

						}
					}).start();
				}
			}
		});

		idPause.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				if(!pause){
					pause = !pause;
					idPause.setText("Resume");
				}else{
					pause = !pause;
					idPause.setText("Pause");
				}
			}
		});

		idReset.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				stop = !stop;
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
				for (App.RechercheZone rechercheZone : App.Requete.getListeRechercheZone()) {
					idConsole.appendText("scientificName : " + rechercheZone.getScientificName() + " / " + "order : " + rechercheZone.getOrder() + " / " +
							"superclass : " + rechercheZone.getSuperclass() + " / " + "recordedBy : " + rechercheZone.getRecordedBy() + " / " + "species : " + rechercheZone.getSpecies()
							+ " \n");
				}
				list.setVisible(true);
				ArrayList<String> doublons = new ArrayList<String>();
				ArrayList<String> nomScientifiques = new ArrayList<String>();

				for (App.RechercheZone rechercheZone : App.Requete.getListeRechercheZone()) {
					list.getItems().add(rechercheZone.getScientificName());
				}


			}
		});

		list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				idConsole.clear();
				earth.getChildren().remove(1, earth.getChildren().size());
				String nomScientifique = list.getSelectionModel().getSelectedItem().toString();
				App.Requete.creerRechercheNom(nomScientifique, (int) idSlider.getValue());
				majLegende();
				afficheEspNom(App.Requete.getListeRechercheNom(), earth);
				idConsole.appendText("Recherche effectuée : " + nomScientifique + "\nPrécision : " + (int) idSlider.getValue() + "\n");
				idEspece.setText(nomScientifique);
				list.getItems().clear();
				list.setVisible(false);
			}
		});
	}

	public boolean nomScientifiqueExiste(String nomScientifique){
		for(String nom : Requete.getListeNom()){
			if(nomScientifique.toLowerCase().equals(nom.toLowerCase())){
				return true;
			}
		}
		return false;
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

	public void afficheEspDate (ArrayList<RechercheDate> listeRechercheDate, Group earth){


		for(App.RechercheDate rechercheDate : listeRechercheDate) {

			int occurence = rechercheDate.getOccurence();
			Color color = choixCouleur(occurence);
			PhongMaterial phongMaterial = new PhongMaterial();
			phongMaterial.setDiffuseColor(color);

			AddQuadrilateral(earth,
					geoCoordTo3dCoord((float) rechercheDate.getCoord().get(3).getY(), (float) rechercheDate.getCoord().get(3).getX()),
					geoCoordTo3dCoord((float) rechercheDate.getCoord().get(0).getY(), (float) rechercheDate.getCoord().get(0).getX()),
					geoCoordTo3dCoord((float) rechercheDate.getCoord().get(1).getY(), (float) rechercheDate.getCoord().get(1).getX()),
					geoCoordTo3dCoord((float) rechercheDate.getCoord().get(2).getY(), (float) rechercheDate.getCoord().get(2).getX()),
					phongMaterial);

		}
	}

	private void majLegende()
	{
		int ecart = 0;
		if((App.Requete.getMaxOccurence() == Integer.MIN_VALUE))
		{
			legend0.setText(Integer.toString(0));
			legend1.setText(Integer.toString(0));
			legend2.setText(Integer.toString(0));
			legend3.setText(Integer.toString(0));
			legend4.setText(Integer.toString(0));
			legend5.setText(Integer.toString(0));
			legend6.setText(Integer.toString(0));
			legend7.setText(Integer.toString(0));
			legend8.setText(Integer.toString(0));
		}
		else {
			/*
			ecart = (App.Requete.getMaxOccurence() - App.Requete.getMinOccurence()) / 8;
			legend0.setText(Integer.toString(App.Requete.getMinOccurence()));
			legend1.setText(Integer.toString(App.Requete.getMinOccurence() + 1 * ecart));
			legend2.setText(Integer.toString(App.Requete.getMinOccurence() + 2 * ecart));
			legend3.setText(Integer.toString(App.Requete.getMinOccurence() + 3 * ecart));
			legend4.setText(Integer.toString(App.Requete.getMinOccurence() + 4 * ecart));
			legend5.setText(Integer.toString(App.Requete.getMinOccurence() + 5 * ecart));
			legend6.setText(Integer.toString(App.Requete.getMinOccurence() + 6 * ecart));
			legend7.setText(Integer.toString(App.Requete.getMinOccurence() + 7 * ecart));
			legend8.setText(Integer.toString(App.Requete.getMaxOccurence()));

			 */

			legend0.setText(Integer.toString(App.Requete.getMinOccurence()));
			legend1.setText(Integer.toString((int)(0.001*App.Requete.getMaxOccurence())));
			legend2.setText(Integer.toString((int)((0.01)*App.Requete.getMaxOccurence())));
			legend3.setText(Integer.toString((int)(0.05*App.Requete.getMaxOccurence())));
			legend4.setText(Integer.toString((int)(0.15*App.Requete.getMaxOccurence())));
			legend5.setText(Integer.toString((int)(0.40*App.Requete.getMaxOccurence())));
			legend6.setText(Integer.toString((int)(0.75*App.Requete.getMaxOccurence())));
			legend7.setText(Integer.toString((int)(0.95*App.Requete.getMaxOccurence())));
			legend8.setText(Integer.toString(App.Requete.getMaxOccurence()));
		}
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


