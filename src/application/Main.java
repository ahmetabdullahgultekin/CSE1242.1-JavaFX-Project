//Ahmet Abdullah GÜLTEKIN 150121025
//Ömer DELIGÖZ 150120035
//This program is a game with relocatable 4x4 tiles and demands to complete the correct pipe path with these tiles.
//This class provides main method and first screen of game.

package application;

//Necessary imports
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


public class Main extends Application {

	//Class data field
	private Scanner input;
	private Image icon;

	//Initial pane of scene
	private AnchorPane pane;

	//This will scan all "text" level files
	private File fold;
	private static File[] allLevels;
	private static File[] levels;

	//Levels files in order
	private String[] orderOfLevels;
	protected static int starterIndex;
	public static File saveLevel;
	private Scanner scan;
	private PrintWriter pr;
	private Media music;
	protected static MediaPlayer mediaPlayer;
	private File fileMusic;
	private File files[];
	private String directory;

	//Animation circles
	@FXML
	private Circle circlePink;
	@FXML
	private Circle circleTurquoise;
	@FXML
	private Circle circleWhite;
	@FXML
	private Circle circleOrange;
	@FXML
	private Label musicLabel;

	//Start method for application
	@Override
	public void start(Stage primaryStage) {
		try {

			try {
				//Initialize variable
				fold = new File("src/Levels");
				allLevels = fold.listFiles();   //allLevels = fold.listFiles((directory, filename) -> filename.endsWith(".txt"));
				orderOfLevels = new String[allLevels.length];
				levels = new File[allLevels.length];
			} catch (Exception e) {
				e.printStackTrace();
			}

			//Get menu pane which created in SceneBuilder
			pane = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));

			//Run Entrance Animation
			entranceAnimation(pane);


			//Arrange Music
			fileMusic = new File("src/Media");
			files = fileMusic.listFiles();
			if(files != null) {
				
				for (File file : files) {
					if(file.getName().contains("Music")) {
						try {
							directory = file.toURI().toString();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			}

			music = new Media(directory);
			
			mediaPlayer = new MediaPlayer(music);
			mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
			mediaPlayer.setAutoPlay(true);
			musicLabel = ((Label)pane.getChildren().get(20));


			//Create text file for completed levels
			try {
				saveLevel = new File("Save.txt");
				pr = new PrintWriter(new FileWriter(saveLevel, true));
				scan = new Scanner(saveLevel);

				//If file found,use it
				if (!scan.hasNext()) {
					saveLevel.createNewFile();
					pr.println("Level " + 1);
					System.out.println("File created: " + saveLevel.getName());

					//If save file not found, create new one
				} else {
					System.out.println("File already exists.");
				}
				pr.close();

			} catch (IOException e) {
				//This must give an error window since missing save file or troubled new one
				System.out.println("An error occurred.");
				Alert alertFile = new Alert(AlertType.ERROR);
				alertFile.setTitle("Error");
				alertFile.setHeaderText("Save file not Found!");
				alertFile.showAndWait();

				e.printStackTrace();
			}

			try {
				//Scan level files and determine game icon
				findLevels();
				icon = new Image("Images/end-horizontal-left.jpg");
			} catch (NullPointerException e) {
				Alert alertLevel = new Alert(AlertType.ERROR);
				alertLevel.setTitle("Error");
				alertLevel.setHeaderText("File is missing!");
				alertLevel.setContentText("Check level files or app icon image.");
				alertLevel.showAndWait();

				e.printStackTrace();
			}

			//Initial Scene
			Scene scene = new Scene(pane);
			primaryStage.setScene(scene);

			//Create Runnable for to pause music
			Runnable runPause = new Runnable() {
				@Override
				public void run() {
					//Change color
					musicLabel.setStyle("-fx-text-fill: green; -fx-font-size: 13;");
					//Change text
					musicLabel.setText("Press 'M' to Start Music");
				}
			};

			//Create Runnable for to Play music
			Runnable runPlay = new Runnable() {
				@Override
				public void run() {
					//Change color
					musicLabel.setStyle("-fx-text-fill: red; -fx-font-size: 13;");
					//Change text
					musicLabel.setText("Press 'M' to Pause Music");
				}
			};

			//Set on Action for Music with Key 'M'
			scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					if (event.getCode().equals(KeyCode.M)) {
						//Run method every time 'M' key is pressed
						StartStopMusic(runPause,runPlay);
					}  
				}
			});

			//Set game icon
			primaryStage.getIcons().add(icon);

			//Set stage non-resizable and launch as window
			primaryStage.setResizable(false);
			primaryStage.setFullScreen(false);
			primaryStage.setTitle("Pipe Puzzle");
			primaryStage.centerOnScreen();
			primaryStage.show();

			//Close the program totally
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

				@Override
				public void handle(WindowEvent wEvent) {

					Platform.exit();
					System.exit(0);
				}
			});

		} 

		catch(Exception e) {
			e.printStackTrace();
		}
	}

	//Entrance animation for color choice
	static void entranceAnimation(AnchorPane pane) throws IOException {

		//Curved pipe images for ball
		((ImageView)pane.getChildren().get(11)).setImage(new Image("Images/pipe11.jpg"));
		((ImageView)pane.getChildren().get(12)).setImage(new Image("Images/pipe10.jpg"));
		((ImageView)pane.getChildren().get(13)).setImage(new Image("Images/pipe01.jpg"));
		((ImageView)pane.getChildren().get(14)).setImage(new Image("Images/pipe00.jpg"));

		//Path of ball
		PathTransition pt = new PathTransition();
		pt.setDuration(Duration.millis(1000));

		//Determine node and path
		//Acceleration is zero
		pt.setInterpolator(Interpolator.LINEAR);
		pt.setRate(0.2);
		pt.setCycleCount(Transition.INDEFINITE);
		pt.setPath(((Circle)pane.getChildren().get(16)));
		pt.setNode(((Circle)pane.getChildren().get(15)));
		pt.play();

		//Default ball color
		((ColorPicker)pane.getChildren().get(17)).setValue(Color.BLUE);
		((Circle)pt.getNode()).setFill(Color.BLUE);
		Controller.selectedColor = Color.BLUE;

		//Actions when colorpicker get hidden
		((ColorPicker)pane.getChildren().get(17)).setOnHidden(e -> {

			//Paint the ball according to colorpicker
			((Circle)pt.getNode()).setFill(((ColorPicker)pane.getChildren().get(17)).getValue());
			((Circle)pt.getNode()).setStroke(((ColorPicker)pane.getChildren().get(17)).getValue());
			Controller.selectedColor = ((ColorPicker)pane.getChildren().get(17)).getValue();
		});

		//Actions when orange ball get clicked
		((Circle)pane.getChildren().get(7)).setOnMouseClicked(e -> {

			//Paint ball
			((Circle)pt.getNode()).setFill(Color.ORANGE);
			((Circle)pt.getNode()).setStroke(Color.ORANGE);
			((ColorPicker)pane.getChildren().get(17)).setValue(Color.ORANGE);
			Controller.selectedColor = ((ColorPicker)pane.getChildren().get(17)).getValue();
		});

		//Actions when white ball get clicked
		((Circle)pane.getChildren().get(8)).setOnMouseClicked(e -> {

			//Paint ball
			((Circle)pt.getNode()).setFill(Color.WHITE);
			((Circle)pt.getNode()).setStroke(Color.WHITE);
			((ColorPicker)pane.getChildren().get(17)).setValue(Color.WHITE);
			Controller.selectedColor = ((ColorPicker)pane.getChildren().get(17)).getValue();
		});

		//Actions when turquoise ball get clicked
		((Circle)pane.getChildren().get(9)).setOnMouseClicked(e -> {

			//Paint ball
			((Circle)pt.getNode()).setFill(Color.TURQUOISE);
			((Circle)pt.getNode()).setStroke(Color.TURQUOISE);
			((ColorPicker)pane.getChildren().get(17)).setValue(Color.TURQUOISE);
			Controller.selectedColor = ((ColorPicker)pane.getChildren().get(17)).getValue();
		});

		//Actions when pink ball get clicked
		((Circle)pane.getChildren().get(10)).setOnMouseClicked(e -> {

			//Paint ball
			((Circle)pt.getNode()).setFill(Color.PINK);
			((Circle)pt.getNode()).setStroke(Color.PINK);
			((ColorPicker)pane.getChildren().get(17)).setValue(Color.PINK);
			Controller.selectedColor = ((ColorPicker)pane.getChildren().get(17)).getValue();
		});

	}

	//Level-finder method
	private void findLevels() throws FileNotFoundException {

		scan = new Scanner(Main.saveLevel);
		
		//Collect level files
		for (int i = 0; i < allLevels.length; i++) {
			File files = new File(allLevels[i].getAbsolutePath());
			levels[i] = files;
		}

		//Put in order
		findOrderOfLevels();

		//Set correct order for levels
		for (int i = 0; i < levels.length; i++) {
			File[] temp = levels.clone();
			levels[Integer.parseInt(orderOfLevels[i]) - 1] = temp[i];
		}

		//Create each tile
		for (int i = 0; i < levels.length; i++) {
			makeTiles(levels[i]);
		}

		//Add a prompt text for combobox
		((ComboBox<String>) pane.getChildren().get(0)).setPromptText("Select a Level");

		//Add each level to combobox respectively
		scan = new Scanner(saveLevel);
		while (scan.hasNext()) {
			if (pane.getChildren().get(0).getClass().getName().equals("javafx.scene.control.ComboBox") && Main.saveLevel.canRead()) {
				((ComboBox<String>) pane.getChildren().get(0)).getItems().add(scan.nextLine());
			} else {
				continue;
			}
		}

	}

	//Put in order level files in array
	private void findOrderOfLevels() {
		for (int i = 0; i < levels.length; i++) {

			char[] chars = levels[i].getName().toCharArray();
			String levelName = "";

			for (int j = 0; j < chars.length; j++) {

				//Find out what level it is
				if ('0' <= chars[j] && chars[j] <= '9') {
					levelName += "" + chars[j];
				} 
				else {
					continue;
				}
			}
			this.orderOfLevels[i] = levelName;
		}
	}

	//This method creates each tile
	public Image[] makeTiles(File levels) throws FileNotFoundException {
		//All images on screen will be in array respectively
		Image[] images = new Image[16]; 

		try {
			input = new Scanner(levels);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		//Create tiles according to given informations about tiles
		String line = "";
		while(input.hasNextLine()){
			line = input.nextLine();

			if(line.isEmpty()) {
				continue;
			}

			//Get features of tiles
			String squares[] = line.split(",");

			//According to features, create tiles
			switch(squares[1]) {
			case "Starter": Starter starter = new Starter(Integer.parseInt(squares[0]),squares[1],squares[2]); 
			images[Integer.parseInt(squares[0]) - 1] = starter.getImage();
			starterIndex = Integer.parseInt(squares[0]) - 1;
			break;
			case "End": End end = new End(Integer.parseInt(squares[0]),squares[1],squares[2]); 
			images[Integer.parseInt(squares[0]) - 1] = end.getImage();
			break;
			case "Empty": Empty empty = new Empty(Integer.parseInt(squares[0]),squares[1],squares[2]); 
			images[Integer.parseInt(squares[0]) - 1] = empty.getImage();
			break;
			case "PipeStatic": PipeStatic pipeStatic = new PipeStatic(Integer.parseInt(squares[0]),squares[1],squares[2]); 
			images[Integer.parseInt(squares[0]) - 1] = pipeStatic.getImage();
			break;
			case "Pipe": Pipe pipe = new Pipe(Integer.parseInt(squares[0]),squares[1],squares[2]); 
			images[Integer.parseInt(squares[0]) - 1] = pipe.getImage();
			break;
			}
		}

		return images;
	}

	//Music on/off
	private void StartStopMusic(Runnable runPause, Runnable runPlay) {

		//Actions when paused
		mediaPlayer.setOnPaused(runPause);

		//Actions when started
		mediaPlayer.setOnPlaying(runPlay);

		//If it has stopped, start music 
		if (mediaPlayer.getStatus().equals(MediaPlayer.Status.PAUSED)) {
			mediaPlayer.play();
		} 
		//If it is running, pause music
		else {
			mediaPlayer.pause();
		}
	}

	//Getters and Setters
	public Scanner getInput() {
		return input;
	}

	public void setInput(Scanner input) {
		this.input = input;
	}

	public Image getIcon() {
		return icon;
	}

	public void setIcon(Image icon) {
		this.icon = icon;
	}

	public AnchorPane getPane() {
		return pane;
	}

	public void setPane(AnchorPane pane) {
		this.pane = pane;
	}

	public File getFold() {
		return fold;
	}

	public void setFold(File fold) {
		this.fold = fold;
	}

	public File[] getAllLevels() {
		return allLevels;
	}

	public void setAllLevels(File[] allLevels) {
		this.allLevels = allLevels;
	}

	public File[] getLevels() {
		return levels;
	}

	public void setLevels(File[] levels) {
		this.levels = levels;
	}

	public String[] getOrderOfLevels() {
		return orderOfLevels;
	}

	public void setOrderOfLevels(String[] orderOfLevels) {
		this.orderOfLevels = orderOfLevels;
	}

	public int getStarterIndex() {
		return starterIndex;
	}

	public void setStarterIndex(int starterIndex) {
		Main.starterIndex = starterIndex;
	}

	public Circle getCirclePink() {
		return circlePink;
	}

	public void setCirclePink(Circle circlePink) {
		this.circlePink = circlePink;
	}

	public Circle getCircleTurquoise() {
		return circleTurquoise;
	}

	public void setCircleTurquoise(Circle circleTurquoise) {
		this.circleTurquoise = circleTurquoise;
	}

	public Circle getCircleWhite() {
		return circleWhite;
	}

	public void setCircleWhite(Circle circleWhite) {
		this.circleWhite = circleWhite;
	}

	public Circle getCircleOrange() {
		return circleOrange;
	}

	public void setCircleOrange(Circle circleOrange) {
		this.circleOrange = circleOrange;
	}

	public static File getSaveLevel() {
		return saveLevel;
	}

	public static void setSaveLevel(File saveLevel) {
		Main.saveLevel = saveLevel;
	}

	public Scanner getScan() {
		return scan;
	}

	public void setScan(Scanner scan) {
		this.scan = scan;
	}

	public PrintWriter getPr() {
		return pr;
	}

	public void setPr(PrintWriter pr) {
		this.pr = pr;
	}

	public Media getMusic() {
		return music;
	}

	public void setMusic(Media music) {
		this.music = music;
	}

	public Label getMusicLabel() {
		return musicLabel;
	}

	public void setMusicLabel(Label musicLabel) {
		this.musicLabel = musicLabel;
	}

	//Main method
	public static void main(String[] args) {
		launch(args);
	}

}