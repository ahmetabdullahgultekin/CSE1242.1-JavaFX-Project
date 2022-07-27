//Ahmet Abdullah GÜLTEKIN 150121025
//Ömer DELIGÖZ 150120035
//This program is a game with relocatable 4x4 tiles and demands to complete the correct pipe path with these tiles.
//This class provides screen switching, animations, function buttons and many menu specifications.

package application;

//Necessary imports
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Controller {

	//Class data field

	//Game pane
	@FXML
	private GridPane game;

	//Menu pane
	@FXML
	private AnchorPane menu;

	//Credits pane
	@FXML
	private AnchorPane creditsPane;

	//Nodes on screen
	@FXML
	private ComboBox<?> cboxLevel;
	@FXML
	private Button btPlay;
	@FXML
	private Button btCredits;
	@FXML
	private Button btMenuQuit;
	@FXML
	private Button btBacktoMenu;
	@FXML
	private Button btRestartLevel;
	@FXML
	private Ellipse ellipseBlue;
	@FXML
	private Ellipse ellipseGreen;
	@FXML
	private Ellipse ellipseRed;
	@FXML
	private Circle circle;
	@FXML
	private Circle ball;
	@FXML
	private Label labelCounter;
	@FXML
	private Label musicLabel;

	//Variables used in several line
	private Stage stage;
	private Scene scene;
	private Main main;
	private static String selectedLevel;
	private ClipboardContent content;
	private ImageView iwLast;
	private ImageView lastTouchedIW;
	private static Image[] resetImages;
	private static Image[] switchedImages;
	private static File fold;
	private static File[] allLevels;
	private static File[] levels;
	private String[] orderOfLevels;
	private short counter;
	private static File levelFile;
	private int firstsRow;
	private int firstsColumn;
	private int lastsRow;
	private int lastsColumn;
	protected static Color selectedColor;
	private static Path path;
	private static ArrayList<Integer> pathIndex = new ArrayList<Integer>();
	private static ArrayList<Integer> directions = new ArrayList<Integer>();
	private Scanner read;
	private PrintWriter pr;

	//Includes Back to Menu button functions and this method shows main menu
	@FXML
	public void backToMenu(ActionEvent event) throws IOException {

		//Use main variables
		main = new Main();

		//Menu pane
		menu = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
		Main.entranceAnimation(menu);

		//Set null or zero for every usage (Every turn)
		selectedLevel = null;
		counter = 0;

		//Combobox prompt text
		if (menu.getChildren().get(0).getClass().getName().equals("javafx.scene.control.ComboBox")) {
			((ComboBox<String>) menu.getChildren().get(0)).setPromptText("Select a Level");
		}

		//Get level file
		for (int i = 0; i < main.getAllLevels().length; i++) {
			File files = new File(main.getAllLevels()[i].getAbsolutePath());
			main.getLevels()[i] = files;
		}

		//Construct tiles
		for (int i = 0; i < main.getLevels().length; i++) {
			main.makeTiles(main.getLevels()[i]);
		}

		read = new Scanner(Main.saveLevel);
		//Fill Combobox with levels
		while (read.hasNext()) {
			String line = read.nextLine();
			if (menu.getChildren().get(0).getClass().getName().equals("javafx.scene.control.ComboBox") && Main.saveLevel.canRead()
					&& !((ComboBox<String>) menu.getChildren().get(0)).getItems().contains(line)) {
				((ComboBox<String>) menu.getChildren().get(0)).getItems().add(line);
			} else {
				continue;
			}
		}
		read.close();

		//Set scene and show stage
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(menu);

		Runnable runPause = new Runnable() {
			@Override
			public void run() {
				//Change color
				musicLabel.setStyle("-fx-text-fill: green; -fx-font-size: 13;");
				//Change text
				musicLabel.setText("Press 'M' to Start Music");
			}
		};

		Runnable runPlay = new Runnable() {
			@Override
			public void run() {
				//Change color
				musicLabel.setStyle("-fx-text-fill: red; -fx-font-size: 13;");
				//Change text
				musicLabel.setText("Press 'M' to Pause Music");
			}
		};

		//Solve default text problem
		musicLabel = ((Label)menu.getChildren().get(20));
		if (Main.mediaPlayer.getStatus().equals(MediaPlayer.Status.PAUSED)) {
			//Change color
			musicLabel.setStyle("-fx-text-fill: green; -fx-font-size: 13;");
			//Change text
			musicLabel.setText("Press 'M' to Start Music");
		}

		//Music set on/off when pressed 'M'
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode().equals(KeyCode.M)) {
					StartStopMusic(runPause,runPlay);
				}  
			}
		});

		stage.setScene(scene);
		stage.setTitle("Pipe Puzzle");
		stage.show();
	}

	//Includes Play button functions and this method shows game scene
	@FXML
	public void playButton(ActionEvent event) throws IOException {
		try {
			//Use main variables
			main = new Main();
			//Game pane
			game = FXMLLoader.load(getClass().getResource("GameScene.fxml"));
			//Set zero when game start(for steps)
			counter = 0;

			//This lines get which button used and according to this determine which level we will use
			for (int i = 0; i < findLevels().length; i++) {	

				//Check whether the thing which get clicked is a button or not
				if (event.getSource().getClass().getSimpleName().equals("Button")) {

					//Check which button was clicked
					if (((Button)event.getSource()).getText().equals("Restart Level")) {

						if (findLevels()[i].getName().endsWith(selectedLevel.substring(6) + ".txt")) {
							levelFile = findLevels()[i];
							selectedLevel = "Level " + (selectedLevel.substring(6));
							break;
						} 
					} 

					//Check which button was clicked
					else if (((Button)event.getSource()).getText().equals("Play")){
						if (findLevels()[i].getName().endsWith(selectedLevel.substring(6) + ".txt")) {
							levelFile = findLevels()[i];
							selectedLevel = "Level " + (selectedLevel.substring(6));
							break;
						} 
					}

					//If action was not on a button give information
					else {
						System.out.println("Button is irrelevant!");
					}
				}

				//If it is a path, determine the selectedLevel
				else if (event.getSource().getClass().getSimpleName().equals("PathTransition")){
					selectedLevel = "Level " + (Integer.parseInt(selectedLevel.substring(6)) + 1);
					break;
				}

				//If it is irrelevant, show alert on console
				else {
					System.out.println("Different action revealed!");
				}
			}

			//Default level tiles
			resetImages = (main.makeTiles(levelFile)).clone();

			//Changed level tiles
			switchedImages = (main.makeTiles(levelFile)).clone();

			//Draw level
			for (int i = 0; i < resetImages.length; i++) {
				//Draw tiles
				((ImageView)(game.getChildren().get(i))).setImage(resetImages[i]);

				//Set cursor according to static feature
				if (isStatic(((ImageView)(game.getChildren().get(i))).getImage())) {
					((ImageView)(game.getChildren().get(i))).setCursor(Cursor.DEFAULT);
				} else {
					((ImageView)(game.getChildren().get(i))).setCursor(Cursor.OPEN_HAND);
				}

			}

			//Action Event Handling Drag-Drop Tiles
			for (int i = 0; i < game.getChildren().size() - 5; i++) {


				//Imageview which is drag start
				((ImageView)game.getChildren().get(i)).setOnDragDetected(e -> {

					//Imageview which is in hand
					lastTouchedIW = (((ImageView) e.getSource()));

					if( !isStatic((((ImageView) e.getSource()).getImage()))){
						// drag was detected, start a drag-and-drop gesture
						// allow any transfer mode 
						Dragboard db = ((ImageView) e.getSource()).startDragAndDrop(TransferMode.MOVE);

						// Put a string on a dragboard 
						content = new ClipboardContent();
						content.putImage(((ImageView) e.getSource()).getImage());

						((ImageView) e.getSource()).setImage(new Image("Images/empty-free.jpg"));
						switchedImages[Integer.parseInt(((ImageView) e.getSource()).getId().substring(2)) - 1] = new Image("Images/empty-free.jpg");

						db.setContent(content);

						event.consume();
					}
				});


				//Detect Last Location of Mouse
				((ImageView)game.getChildren().get(i)).setOnDragEntered(e -> {
					iwLast = (ImageView)e.getTarget();
				});

				//Change cursor while dragging
				((ImageView)game.getChildren().get(i)).setOnDragOver(e -> {
					e.acceptTransferModes(TransferMode.MOVE);
					e.consume();
				});


				//Image dropped
				((ImageView)game.getChildren().get(i)).setOnDragDone(e -> { 

					//Find index of source and target image
					firstsRow = (Integer.parseInt(lastTouchedIW.getId().substring(2)) - 1) / 4;
					firstsColumn = (Integer.parseInt(lastTouchedIW.getId().substring(2)) - 1) % 4;
					lastsRow = (Integer.parseInt(iwLast.getId().substring(2)) - 1) / 4;
					lastsColumn = (Integer.parseInt(iwLast.getId().substring(2)) - 1) % 4;	

					//Check thing which was clicked is relocatable 
					if( !(isStatic((content).getImage())) 
							&& ((iwLast).getImage().getUrl().equals(new Image("Images/empty-free.jpg").getUrl()))
							&& (((Math.abs(lastsRow - firstsRow) == 1) || ((Math.abs((lastsColumn - firstsColumn)) == 1)))
									&& ((lastsRow == firstsRow) || (lastsColumn == firstsColumn))
									)) {

						((ImageView)game.getChildren().get(Integer.parseInt(iwLast.getId().substring(2)) - 1)).setImage(content.getImage());
						++counter;
						((Label)game.getChildren().get(18)).setText(((Label)game.getChildren().get(18)).getText().substring(0,9) + " " + counter);

						switchedImages[Integer.parseInt(iwLast.getId().substring(2)) - 1] = content.getImage();

						//Change cursor
						((ImageView) e.getSource()).setCursor(Cursor.DEFAULT);
						(iwLast).setCursor(Cursor.OPEN_HAND);

						//Check solution is correct or not after every movement
						try {
							checkSolution();
						} catch (IOException e1) {
							e1.printStackTrace();
						}

						event.consume();
					}

					//If thing which was clicked is not relocatable thing,put back to it's first location on
					else {
						((ImageView) e.getSource()).setImage(content.getImage());
						switchedImages[Integer.parseInt(((ImageView) e.getSource()).getId().substring(2)) - 1] = content.getImage();
					}

				});

			}

			//Find center of starter
			int indexOfstarter = 0; 
			int starterX = 0;
			int starterY = 0;

			String starterDownUrl = (new Image("Images/starter-vertical-down.jpg").getUrl());
			String starterLeftUrl = (new Image("Images/starter-horizontal-left.jpg").getUrl());

			for (int i = 0; i < resetImages.length; i++) {
				if (starterDownUrl.equals(resetImages[i].getUrl()) || starterLeftUrl.equals(resetImages[i].getUrl())) {
					indexOfstarter = i;
				}
			}

			starterX = indexOfstarter % 4 * 150;
			starterY = indexOfstarter / 4 * 150;

			//Draw ball at initial position
			ball = (Circle)game.getChildren().get(19);
			ball.setFill(selectedColor);
			ball.setStroke(selectedColor);
			ball.setTranslateX(starterX);
			ball.setTranslateY(starterY);
			ball.setVisible(true);

			//If user clicks right button, determine stage
			if (event.getSource().getClass().getSimpleName().equals("Button")) {
				stage = (Stage)((Node)event.getSource()).getScene().getWindow();
			} else {
				stage = (Stage)((PathTransition)event.getSource()).getNode().getScene().getWindow();
			}

			//Create stage and set scene
			scene = new Scene(game);

			//Runnable for Pausing music
			Runnable runPause = new Runnable() {
				@Override
				public void run() {
					//Change color
					musicLabel.setStyle("-fx-text-fill: green; -fx-font-size: 13;");
					//Change text
					musicLabel.setText("Press 'M' to Start Music");
				}
			};

			//Runnable for Playing music
			Runnable runPlay = new Runnable() {
				@Override
				public void run() {
					//Change color
					musicLabel.setStyle("-fx-text-fill: red; -fx-font-size: 13;");
					//Change text
					musicLabel.setText("Press 'M' to Pause Music");
				}
			};

			//Arrange Music
			musicLabel = ((Label)game.getChildren().get(20));
			//Solve default text problem
			if (Main.mediaPlayer.getStatus().equals(MediaPlayer.Status.PAUSED)) {
				//Change color
				musicLabel.setStyle("-fx-text-fill: green; -fx-font-size: 13;");
				//Change text
				musicLabel.setText("Press 'M' to Start Music");
			}

			//Set music on or off when pressed 'M'
			scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					if (event.getCode().equals(KeyCode.M)) {
						StartStopMusic(runPause,runPlay);
					}  
				}
			});

			stage.setScene(scene);
			stage.setTitle("Pipe Puzzle " + selectedLevel);
			stage.show();

		} 

		//Throw alert window if level not found
		catch (NullPointerException e) {
			Alert alertLevel = new Alert(AlertType.ERROR);
			alertLevel.setTitle("Level Selection");
			alertLevel.setHeaderText("Level has not been selected!");
			alertLevel.setContentText("Select a Level");
			alertLevel.showAndWait();

			e.printStackTrace();
		}

		//Throw any exception
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	//Get level from combobox
	@FXML
	public void selectLevel(ActionEvent event) throws IOException {

		try {
			menu = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
			selectedLevel = ((ComboBox<String>)event.getSource()).getValue();
		} 		

		//Throw alert window if level not found
		catch (NullPointerException e) {
			Alert alertLevel = new Alert(AlertType.ERROR);
			alertLevel.setTitle("Error");
			alertLevel.setHeaderText("Level has not been selected!");
			alertLevel.setContentText("Select a Level");
			alertLevel.showAndWait();

			e.printStackTrace();
		}
	}

	//Check whether tile is static or not 
	private boolean isStatic(Image image) {
		String url = image.getUrl();
		boolean isstatic = false;

		if (
				url.equals(new Image("Images/starter-horizontal-left.jpg").getUrl())
				|| url.equals(new Image("Images/starter-vertical-down.jpg").getUrl())
				|| url.equals(new Image("Images/pipestatic00.jpg").getUrl())
				|| url.equals(new Image("Images/pipestatic01.jpg").getUrl())
				|| url.equals(new Image("Images/pipestatic10.jpg").getUrl())
				|| url.equals(new Image("Images/pipestatic11.jpg").getUrl())
				|| url.equals(new Image("Images/pipestatic-vertical.jpg").getUrl())
				|| url.equals(new Image("Images/pipestatic-horizontal.jpg").getUrl())
				|| url.equals(new Image("Images/end-horizontal-left.jpg").getUrl())
				|| url.equals(new Image("Images/end-vertical-down.jpg").getUrl())
				|| url.equals(new Image("Images/empty-free.jpg").getUrl())) {

			isstatic = true;
		}
		return isstatic;
	}

	//Find all levels in levels folder
	private File[] findLevels() throws FileNotFoundException {
		fold = new File("src/Levels");
		allLevels = fold.listFiles((directory, filename) -> filename.endsWith(".txt"));
		levels = new File[allLevels.length];
		orderOfLevels = new String[allLevels.length];

		for (int i = 0; i < allLevels.length; i++) {
			File files = new File(allLevels[i].getAbsolutePath());
			levels[i] = files;
		}

		//Sort all levels
		findOrderOfLevels();
		File[] temp = levels.clone();

		//Reuse array with new situation
		for (int i = 0; i < levels.length; i++) {
			levels[Integer.parseInt(orderOfLevels[i]) - 1] = temp[i];
		}

		return levels;
	}

	//Put levels in order 
	private void findOrderOfLevels() throws FileNotFoundException {
		orderOfLevels = new String[allLevels.length];

		for (int i = 0; i < levels.length; i++) {

			//Get all characters from name of level file
			char[] chars = levels[i].getName().toCharArray();
			String levelName = "";

			//Find which level is
			for (int j = 0; j < chars.length; j++) {

				if ('0' <= chars[j] && chars[j] <= '9') {
					levelName += "" + chars[j];
				} 
				else {
					continue;
				}
			}
			orderOfLevels[i] = levelName;
		}
	}

	//Check solution is correct when every step occurred
	public void checkSolution() throws IOException {

		//Initialize the variables with url of images

		String starterDown = new Image("Images/starter-vertical-down.jpg").getUrl();
		String starterLeft = new Image("Images/starter-horizontal-left.jpg").getUrl();

		String endDown = new Image("Images/end-vertical-down.jpg").getUrl();
		String endLeft = new Image("Images/end-horizontal-left.jpg").getUrl();

		String pipe00 = new Image("Images/pipe00.jpg").getUrl();
		String pipe01 = new Image("Images/pipe01.jpg").getUrl();
		String pipe10 = new Image("Images/pipe10.jpg").getUrl();
		String pipe11 = new Image("Images/pipe11.jpg").getUrl();

		String pipeStatic00 = new Image("Images/pipestatic00.jpg").getUrl();
		String pipeStatic01 = new Image("Images/pipestatic01.jpg").getUrl();
		String pipeStatic10 = new Image("Images/pipestatic10.jpg").getUrl();
		String pipeStatic11 = new Image("Images/pipestatic11.jpg").getUrl();

		String pipeVertical = new Image("Images/pipe-vertical.jpg").getUrl();
		String pipeHorizontal = new Image("Images/pipe-horizontal.jpg").getUrl();

		String pipeStaticHorizontal = new Image("Images/pipestatic-horizontal.jpg").getUrl();
		String pipeStaticVertical = new Image("Images/pipestatic-vertical.jpg").getUrl();

		//Default Values
		String starterUrl = starterDown;
		int starterIndex = 0;
		String endUrl = endLeft;
		int endIndex = 0;
		int currentIndex = 0;
		int enteredFrom = 0;

		//Determine initial location of starter
		for (int i = 0; i < resetImages.length; i++) {
			if (starterDown.equals(resetImages[i].getUrl())) {
				starterUrl = starterDown;
				starterIndex = i;
			}
			else if (starterLeft.equals(resetImages[i].getUrl())) {
				starterUrl = starterLeft;
				starterIndex = i;
			}
			if (endDown.equals(resetImages[i].getUrl())) {
				endUrl = endDown;
				endIndex = i;
			}
			else if (endLeft.equals(resetImages[i].getUrl())) {
				endUrl = endLeft;
				endIndex = i;
			}
		}
		//Assign to currentIndex
		currentIndex = starterIndex;

		//Add current index to arraylist for finding order of path every tile
		pathIndex.add(currentIndex);

		//Check directions
		if (starterUrl.equals(starterLeft)) {

			directions.add(1);

			//Increase index according to pipe type
			currentIndex -= 1;
			enteredFrom = 1;
			pathIndex.add(currentIndex);

		}

		else if (starterUrl.equals(starterDown)) {

			directions.add(0);

			//Increase index according to pipe type
			currentIndex += 4;
			enteredFrom = 0;

			pathIndex.add(currentIndex);

		}

		/*
		 * We use the direction codes to verify if the created road is the correct one
		 * The following is the directions code
		 * 
		 * Up = 0
		 * Right = 1
		 * Down = 2
		 * Left = 3
		 * 
		 * enteredFrom variable is direction to previous tile
		 * 
		 * While loop acts like a direction finder until End Pipe
		 * Starter initially sets the right direction
		 * Next Pipe goes through on right path according to enteredFrom and currentIndex values
		 * Check process will be completed when index equalize to index of End Pipe
		 * As a reminder, directions {0,1,2,3}
		 * 
		 */
		while (true) {
			if (enteredFrom == 0) {
				if ((((ImageView) game.getChildren().get(currentIndex)).getImage().getUrl().equals(pipeVertical)
						|| ((ImageView) game.getChildren().get(currentIndex)).getImage().getUrl()
						.equals(pipeStaticVertical))
						&& ((currentIndex != 13) && (currentIndex != 14) && (currentIndex != 15)
								&& (currentIndex != 12) && (currentIndex != 1) && (currentIndex != 2)
								&& (currentIndex != 3) && (currentIndex != 0))) {
					
					//In this situation Index must be increase by four according to correct path
					currentIndex += 4;

					//Increase index according to pipe type
					pathIndex.add(currentIndex);
					directions.add(enteredFrom);

					/*This is inaccessible because end pipe do not look upward

						if (currentIndex == endIndex && endUrl.equals(endUp)) {
						}

					 */
					
					//Rearrange variable by direction
					enteredFrom = 0;
					continue;

				} else if ((((ImageView) game.getChildren().get(currentIndex)).getImage().getUrl().equals(pipe01)
						|| ((ImageView) game.getChildren().get(currentIndex)).getImage().getUrl().equals(pipeStatic01))
						&& ((currentIndex != 7) && (currentIndex != 11) && (currentIndex != 15) && (currentIndex != 0)
								&& (currentIndex != 1) && (currentIndex != 2) && (currentIndex != 3))) {
					currentIndex += 1;

					pathIndex.add(currentIndex);
					directions.add(enteredFrom);

					if (currentIndex == endIndex && endUrl.equals(endLeft)) {
						drawPath();
					}
					enteredFrom = 3;
					continue;

				} else if ((((ImageView) game.getChildren().get(currentIndex)).getImage().getUrl().equals(pipe00)
						|| ((ImageView) game.getChildren().get(currentIndex)).getImage().getUrl().equals(pipeStatic00))
						&& ((currentIndex != 4) && (currentIndex != 8) && (currentIndex != 12)
								&& (currentIndex != 1) && (currentIndex != 2) && (currentIndex != 3)
								&& (currentIndex != 0))) {
					currentIndex -= 1;

					pathIndex.add(currentIndex);
					directions.add(enteredFrom);

					/*This is inaccessible because end pipe do not look right
						if (currentIndex == endIndex && endUrl.equals(endRight)) {
						}
					 */

					enteredFrom = 1;
					continue;

				} else {
					try {
						directions.clear();
						pathIndex.clear();
					} catch (NullPointerException e) {
						e.printStackTrace();
					}
					break;
				}

			} else if (enteredFrom == 1) {

				if ((((ImageView) game.getChildren().get(currentIndex)).getImage().getUrl().equals(pipeHorizontal)
						|| ((ImageView) game.getChildren().get(currentIndex)).getImage().getUrl()
						.equals(pipeStaticHorizontal))
						&& ((currentIndex != 0) && (currentIndex != 4) && (currentIndex != 8)
								&& (currentIndex != 12) && (currentIndex != 3) && (currentIndex != 7)
								&& (currentIndex != 11) && (currentIndex != 15))) {
					currentIndex -= 1;

					pathIndex.add(currentIndex);
					directions.add(enteredFrom);

					/*This is inaccessible because end pipe do not look right
						if (currentIndex == endIndex && endUrl.equals(endRight)) {
						}
					 */					
					enteredFrom = 1;
					continue;

				} else if ((((ImageView) game.getChildren().get(currentIndex)).getImage().getUrl().equals(pipe01)
						|| ((ImageView) game.getChildren().get(currentIndex)).getImage().getUrl().equals(pipeStatic01))
						&& ((currentIndex != 7) && (currentIndex != 11) && (currentIndex != 15)
								&& (currentIndex != 0) && (currentIndex != 1) && (currentIndex != 2)
								&& (currentIndex != 3))) {
					currentIndex -= 4;

					pathIndex.add(currentIndex);
					directions.add(enteredFrom);

					if (currentIndex == endIndex && endUrl.equals(endDown)) {
						directions.add(2);
						drawPath();
					}
					enteredFrom = 2;
					continue;

				} else if ((((ImageView) game.getChildren().get(currentIndex)).getImage().getUrl().equals(pipe11)
						|| ((ImageView) game.getChildren().get(currentIndex)).getImage().getUrl().equals(pipeStatic11))
						&& ((currentIndex != 3) && (currentIndex != 7) && (currentIndex != 11)
								&& (currentIndex != 15) && (currentIndex != 12) && (currentIndex != 14)
								&& (currentIndex != 13))) {
					currentIndex += 4;

					pathIndex.add(currentIndex);
					directions.add(enteredFrom);

					/*This is inaccessible because end pipe do not look upward
						if (currentIndex == endIndex && endUrl.equals(endUp)) {
						}
					 */
					enteredFrom = 0;
					continue;

				} else {
					try {
						directions.clear();
						pathIndex.clear();
					} catch (NullPointerException e) {
						e.printStackTrace();
					}
					break;
				}

			} else if (enteredFrom == 2) {

				if ((((ImageView) game.getChildren().get(currentIndex)).getImage().getUrl().equals(pipeVertical)
						|| ((ImageView) game.getChildren().get(currentIndex)).getImage().getUrl()
						.equals(pipeStaticVertical))
						&& ((currentIndex != 13) && (currentIndex != 14) && (currentIndex != 15)
								&& (currentIndex != 12) && (currentIndex != 1) && (currentIndex != 2)
								&& (currentIndex != 3) && (currentIndex != 0))) {
					currentIndex -= 4;

					pathIndex.add(currentIndex);
					directions.add(enteredFrom);

					if (currentIndex == endIndex && endUrl.equals(endDown)) {
						directions.add(2);
						drawPath();
					}

					enteredFrom = 2;
					continue;

				} else if ((((ImageView) game.getChildren().get(currentIndex)).getImage().getUrl().equals(pipe10)
						|| ((ImageView) game.getChildren().get(currentIndex)).getImage().getUrl().equals(pipeStatic10))
						&& ((currentIndex != 4) && (currentIndex != 8) && (currentIndex != 12)
								&& (currentIndex != 0) && (currentIndex != 14) && (currentIndex != 15)
								&& (currentIndex != 13))) {
					currentIndex -= 1;

					pathIndex.add(currentIndex);
					directions.add(enteredFrom);

					/*This is inaccessible because end pipe do not look right
						if (currentIndex == endIndex && endUrl.equals(endRight)) {
						}
					 */

					enteredFrom = 1;
					continue;

				} else if ((((ImageView) game.getChildren().get(currentIndex)).getImage().getUrl().equals(pipe11)
						|| ((ImageView) game.getChildren().get(currentIndex)).getImage().getUrl().equals(pipeStatic11))
						&& ((currentIndex != 3) && (currentIndex != 7) && (currentIndex != 11)
								&& (currentIndex != 15) && (currentIndex != 12) && (currentIndex != 14)
								&& (currentIndex != 13))) {
					currentIndex += 1;

					pathIndex.add(currentIndex);
					directions.add(enteredFrom);

					if (currentIndex == endIndex && endUrl.equals(endLeft)) {
						directions.add(3);
						drawPath();
					}

					enteredFrom = 3;
					continue;

				} else {
					try {
						directions.clear();
						pathIndex.clear();
					} catch (NullPointerException e) {
						e.printStackTrace();
					}
					break;
				}

			} else if (enteredFrom == 3) {
				if ((((ImageView) game.getChildren().get(currentIndex)).getImage().getUrl().equals(pipeHorizontal)
						|| ((ImageView) game.getChildren().get(currentIndex)).getImage().getUrl()
						.equals(pipeStaticHorizontal))
						&& ((currentIndex != 0) && (currentIndex != 4) && (currentIndex != 8)
								&& (currentIndex != 12) && (currentIndex != 3) && (currentIndex != 7)
								&& (currentIndex != 11) && (currentIndex != 15))) {
					currentIndex += 1;

					pathIndex.add(currentIndex);
					directions.add(enteredFrom);

					if (currentIndex == endIndex && endUrl.equals(endLeft)) {
						directions.add(3);
						drawPath();
					}

					enteredFrom = 3;
					continue;

				} else if ((((ImageView) game.getChildren().get(currentIndex)).getImage().getUrl().equals(pipe10)
						|| ((ImageView) game.getChildren().get(currentIndex)).getImage().getUrl().equals(pipeStatic10))
						&& ((currentIndex != 4) && (currentIndex != 8) && (currentIndex != 12)
								&& (currentIndex != 0) && (currentIndex != 14) && (currentIndex != 15)
								&& (currentIndex != 13))) {
					currentIndex += 4;

					pathIndex.add(currentIndex);
					directions.add(enteredFrom);

					/*This is inaccessible because end pipe do not look upward
						if (currentIndex == endIndex && endUrl.equals(endUp)) {
						}
					 */

					enteredFrom = 0;
					continue;

				} else if ((((ImageView) game.getChildren().get(currentIndex)).getImage().getUrl().equals(pipe00)
						|| ((ImageView) game.getChildren().get(currentIndex)).getImage().getUrl().equals(pipeStatic00))
						&& ((currentIndex != 4) && (currentIndex != 8) && (currentIndex != 12)
								&& (currentIndex != 1) && (currentIndex != 2) && (currentIndex != 3)
								&& (currentIndex != 0))) {
					currentIndex -= 4;

					pathIndex.add(currentIndex);
					directions.add(enteredFrom);

					if (currentIndex == endIndex && endUrl.equals(endDown)) {
						directions.add(2);
						drawPath();
					}

					enteredFrom = 2;
					continue;

				} else {
					//If solution is not correct clear all collected information
					try {
						directions.clear();
						pathIndex.clear();
					} catch (NullPointerException e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
	}

	//Add path for ball
	//This method runs only if level has been completed
	private void drawPath() throws IOException {

		//Draw ball
		//The following are the properties of paths
		int startX = 0;
		int startY = 0;
		int endX = 0;
		int endY = 0;
		int radius = 75;
		int degree = 90;
		int indexOfLastPipe = 0;
		path = new Path();

		//Iteration helps us to check every path index and provides us to add path element 
		for (int i = 0; i < pathIndex.size(); i++) {

			//Get index of correct pipe way respectively
			indexOfLastPipe = pathIndex.get(i);

			if (switchedImages[indexOfLastPipe].getUrl().equals((new Image("Images/starter-horizontal-left.jpg")).getUrl())) 
			{
				//Locate center of current pipe
				startX = indexOfLastPipe % 4 * 150 + 75;
				startY = indexOfLastPipe / 4 * 150 + 75;
				endX = startX - 75;
				endY = startY;

				//Create path element
				MoveTo moveTo = new MoveTo(startX, startY);
				LineTo lineTo = new LineTo(endX, endY);
				path.getElements().addAll(moveTo, lineTo);

			} 
			else if(switchedImages[indexOfLastPipe].getUrl().equals((new Image("Images/end-horizontal-left.jpg")).getUrl())) 
			{
				//Locate center of current pipe
				startX = indexOfLastPipe % 4 * 150 + 75;
				startY = indexOfLastPipe / 4 * 150 + 75;
				endX = startX + 5;
				endY = startY;

				//Create path element
				LineTo lineTo = new LineTo(endX, endY);
				path.getElements().add(lineTo);
			}
			else if(switchedImages[indexOfLastPipe].getUrl().equals((new Image("Images/starter-vertical-down.jpg")).getUrl())) 
			{
				//Locate center of current pipe
				startX = indexOfLastPipe % 4 * 150 + 75;
				startY = indexOfLastPipe / 4 * 150 + 75;
				endX = startX;
				endY = startY + 75;

				//Create path element
				MoveTo moveTo = new MoveTo(startX, startY);
				LineTo lineTo = new LineTo(endX, endY);
				path.getElements().addAll(moveTo, lineTo);

			}
			else if(switchedImages[indexOfLastPipe].getUrl().equals((new Image("Images/end-vertical-down.jpg")).getUrl())) 
			{
				//Locate center of current pipe
				startX = indexOfLastPipe % 4 * 150 + 75;
				startY = indexOfLastPipe / 4 * 150 + 75;
				endX = startX;
				endY = startY - 5;

				//Create path element
				LineTo lineTo = new LineTo(endX, endY);
				path.getElements().add(lineTo);
			}
			else if(switchedImages[indexOfLastPipe].getUrl().equals((new Image("Images/pipestatic00.jpg")).getUrl()) 
					|| switchedImages[indexOfLastPipe].getUrl().equals((new Image("Images/pipe00.jpg")).getUrl())) 
			{
				if (directions.get(i) == 0) {
					//Locate center of current pipe
					startX = indexOfLastPipe % 4 * 150 + 75;
					startY = indexOfLastPipe / 4 * 150 + 75;
					endX = startX - 75;
					endY = startY;

					//Create path according to next pipe
					ArcTo arcTo = new ArcTo(radius, radius, degree, endX, endY, false, true);
					path.getElements().add(arcTo);
				} 
				else if(directions.get(i) == 3) {
					//Locate center of current pipe
					startX = indexOfLastPipe % 4 * 150 + 75;
					startY = indexOfLastPipe / 4 * 150 + 75;
					endX = startX;
					endY = startY - 75;

					//Create path according to next pipe
					ArcTo arcTo = new ArcTo(radius, radius, degree, endX, endY, false, false);
					path.getElements().add(arcTo);
				}
				else {
					System.out.println("pipe00 not found!");
				}

			}
			else if(switchedImages[indexOfLastPipe].getUrl().equals((new Image("Images/pipestatic01.jpg")).getUrl()) 
					|| switchedImages[indexOfLastPipe].getUrl().equals((new Image("Images/pipe01.jpg")).getUrl())) 
			{
				if (directions.get(i) == 0) {
					//Locate center of current pipe
					startX = indexOfLastPipe % 4 * 150 + 75;
					startY = indexOfLastPipe / 4 * 150 + 75;
					endX = startX + 75;
					endY = startY;

					//Create path according to next pipe
					ArcTo arcTo = new ArcTo(radius, radius, degree, endX, endY, false, false);
					path.getElements().add(arcTo);
				} 
				else if(directions.get(i) == 1) {
					//Locate center of current pipe
					startX = indexOfLastPipe % 4 * 150 + 75;
					startY = indexOfLastPipe / 4 * 150 + 75;
					endX = startX;
					endY = startY - 75;

					//Create path according to next pipe
					ArcTo arcTo = new ArcTo(radius, radius, degree, endX, endY, false, true);
					path.getElements().add(arcTo);
				}
				else {
					System.out.println("pipe01 not found!");
				}

			}
			else if(switchedImages[indexOfLastPipe].getUrl().equals((new Image("Images/pipestatic10.jpg")).getUrl()) 
					|| switchedImages[indexOfLastPipe].getUrl().equals((new Image("Images/pipe10.jpg")).getUrl())) 
			{
				if (directions.get(i) == 2) {
					//Locate center of current pipe
					startX = indexOfLastPipe % 4 * 150 + 75;
					startY = indexOfLastPipe / 4 * 150 + 75;
					endX = startX - 75;
					endY = startY;

					//Create path according to next pipe
					ArcTo arcTo = new ArcTo(radius, radius, degree, endX, endY, false, false);
					path.getElements().add(arcTo);
				} 
				else if(directions.get(i) == 3) {
					//Locate center of current pipe
					startX = indexOfLastPipe % 4 * 150 + 75;
					startY = indexOfLastPipe / 4 * 150 + 75;
					endX = startX;
					endY = startY + 75;

					//Create path according to next pipe
					ArcTo arcTo = new ArcTo(radius, radius, degree, endX, endY, false, true);
					path.getElements().add(arcTo);
				}
				else {
					System.out.println("pipe10 not found!");
				}

			}
			else if(switchedImages[indexOfLastPipe].getUrl().equals((new Image("Images/pipestatic11.jpg")).getUrl()) 
					|| switchedImages[indexOfLastPipe].getUrl().equals((new Image("Images/pipe11.jpg")).getUrl())) 
			{
				if (directions.get(i) == 1) {
					//Locate center of current pipe
					startX = indexOfLastPipe % 4 * 150 + 75;
					startY = indexOfLastPipe / 4 * 150 + 75;
					endX = startX;
					endY = startY + 75;

					//Create path according to next pipe
					ArcTo arcTo = new ArcTo(radius, radius, degree, endX, endY, false, false);
					path.getElements().add(arcTo);
				} 
				else if(directions.get(i) == 2) {
					//Locate center of current pipe
					startX = indexOfLastPipe % 4 * 150 + 75;
					startY = indexOfLastPipe / 4 * 150 + 75;
					endX = startX + 75;
					endY = startY;

					//Create path according to next pipe
					ArcTo arcTo = new ArcTo(radius, radius, degree, endX, endY, false, true);
					path.getElements().add(arcTo);
				}
				else {
					System.out.println("pipe11 not found!");
				}

			}
			else if(switchedImages[indexOfLastPipe].getUrl().equals((new Image("Images/pipe-horizontal.jpg")).getUrl())
					|| switchedImages[indexOfLastPipe].getUrl().equals((new Image("Images/pipestatic-horizontal.jpg")).getUrl())) 
			{
				if (directions.get(i) == 1) {
					//Locate center of current pipe
					startX = indexOfLastPipe % 4 * 150 + 75;
					startY = indexOfLastPipe / 4 * 150 + 75;
					endX = startX - 75;
					endY = startY;

					//Create path according to next pipe
					LineTo lineTo = new LineTo(endX, endY);
					path.getElements().add(lineTo);
				} 
				else if(directions.get(i) == 3) {
					//Locate center of current pipe
					startX = indexOfLastPipe % 4 * 150 + 75;
					startY = indexOfLastPipe / 4 * 150 + 75;
					endX = startX + 75;
					endY = startY;

					//Create path according to next pipe
					LineTo lineTo = new LineTo(endX, endY);
					path.getElements().add(lineTo);
				}
				else {
					System.out.println("pipehorizontal not found!");
				}
			}
			else if(switchedImages[indexOfLastPipe].getUrl().equals((new Image("Images/pipe-vertical.jpg")).getUrl())
					|| switchedImages[indexOfLastPipe].getUrl().equals((new Image("Images/pipestatic-vertical.jpg")).getUrl())) 
			{
				if (directions.get(i) == 0) {
					//Locate center of current pipe
					startX = indexOfLastPipe % 4 * 150 + 75;
					startY = indexOfLastPipe / 4 * 150 + 75;
					endX = startX;
					endY = startY + 75;

					//Create path according to next pipe
					LineTo lineTo = new LineTo(endX, endY);
					path.getElements().add(lineTo);
				} 
				else if(directions.get(i) == 2) {
					//Locate center of current pipe
					startX = indexOfLastPipe % 4 * 150 + 75;
					startY = indexOfLastPipe / 4 * 150 + 75;
					endX = startX;
					endY = startY - 75;

					//Create path according to next pipe
					LineTo lineTo = new LineTo(endX, endY);
					path.getElements().add(lineTo);
				}
				else {
					System.out.println("pipevertical not found!");
				}

			}
			else {
				System.out.println("Empty tile is on the path!");
			}
		}

		//Throw ball
		startAnimation();

	}

	//Ball Animation
	public void startAnimation() throws IOException {

		//Check save file
		try {
			pr = new PrintWriter(new FileWriter(Main.saveLevel, true));
			read = new Scanner(Main.saveLevel);
			ArrayList<String> lines = new ArrayList<>();

			//Read whole file
			while (read.hasNext()) {
				lines.add(read.nextLine());
			}

			//Check that is there any same level
			boolean isExist = false;
			for (int i = 0; i < lines.size(); i++) {
				if (lines.get(i).equals("Level " + (Integer.parseInt(selectedLevel.substring(6)) + 1))) {
					isExist = true;
					break;
				}
			}

			//If level is unique,add to combobox
			if (!isExist && !(Integer.parseInt(selectedLevel.substring(6)) + 1 > levels.length)) {
				pr.println("Level " + (Integer.parseInt(selectedLevel.substring(6)) + 1));
			}
			pr.close();

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		//Paint ball
		if (selectedColor == null) {
			selectedColor = Color.BLUE;
		}
		((Circle)game.getChildren().get(19)).setFill(selectedColor);
		((Circle)game.getChildren().get(19)).setStroke(selectedColor);

		//Set scene untouchable while animation runs
		game.setDisable(true);
		path.setFill(Color.BLACK);
		ball = ((Circle)game.getChildren().get(19));

		//Put ball on path and show it
		ball.setVisible(true);
		PathTransition ptBall = new PathTransition();
		ptBall.setDuration(Duration.millis(path.getElements().size() * 1000 / 3.0));
		ptBall.setNode(ball);
		ptBall.setPath(path);
		ptBall.setAutoReverse(false);
		ptBall.setCycleCount(1);
		ptBall.setInterpolator(Interpolator.LINEAR);

		//When ball reaches to finish, fade and make smaller the ball
		ptBall.setOnFinished(e -> {

			//Add fade transition
			FadeTransition fdBall = new FadeTransition();
			fdBall.setCycleCount(1);
			fdBall.setDuration(Duration.millis(750));
			fdBall.setNode(ball);
			fdBall.setByValue(1);
			fdBall.setToValue(0);
			fdBall.setInterpolator(Interpolator.LINEAR);
			fdBall.setAutoReverse(false);
			//	fdBall.setOnFinished(event -> {});
			fdBall.play();

			//Add scale transition
			ScaleTransition stBall = new ScaleTransition();
			stBall.setAutoReverse(false);
			stBall.setByX(-1);
			stBall.setByY(-1);
			stBall.setByZ(-1);
			stBall.setFromX(1);
			stBall.setFromY(1);
			stBall.setFromZ(1);
			stBall.setNode(ball);
			stBall.setCycleCount(1);
			stBall.setDuration(Duration.millis(1250));
			stBall.setInterpolator(Interpolator.LINEAR);
			stBall.setOnFinished(event1 -> {

				//Scene is touchable anymore
				game.setDisable(false);

				//But user can not change tiles
				for (int i = 0; i < game.getChildren().size() - 5; i++) {
					((ImageView)game.getChildren().get(i)).setDisable(true);
				}

				//If all levels have been completed,throw alert window for congratulation
				if (levels[levels.length - 1].equals(levelFile)) {

					Alert allLevelsCompleted = new Alert(AlertType.INFORMATION);
					allLevelsCompleted.setTitle("Superb!");
					allLevelsCompleted.setHeaderText("You completed all levels!");
					allLevelsCompleted.setContentText("Thanks for playing!");
					allLevelsCompleted.getButtonTypes().clear();
					allLevelsCompleted.getButtonTypes().add(0, ButtonType.CLOSE);
					allLevelsCompleted.setGraphic(new ImageView(new Image("Images/Emoji.png")));

					//Set enable pane if alert stage is closed
					((Button)allLevelsCompleted.getDialogPane().lookupButton(ButtonType.CLOSE)).setOnAction(event2 -> {game.setDisable(false);});
					allLevelsCompleted.setOnCloseRequest(event3 -> {game.setDisable(false);});

					allLevelsCompleted.show();

				} 

				//If level remains left, open a window for next level
				else {

					Alert levelCompletedAlert = new Alert(AlertType.CONFIRMATION);
					levelCompletedAlert.setTitle("Congratulations!");
					levelCompletedAlert.setHeaderText("You completed the level!");
					levelCompletedAlert.setContentText("Jump to next level ->");
					levelCompletedAlert.getButtonTypes().clear();
					levelCompletedAlert.getButtonTypes().add(0, ButtonType.CLOSE);
					levelCompletedAlert.setGraphic(new Button("Next Level"));

					((Button)levelCompletedAlert.getGraphic()).setOnAction(event2 -> {

						//Update Level File
						for (int j = 0; j < levels.length - 1; j++) {
							if(levels[j].equals(levelFile)) 
							{
								levelFile = levels[j + 1];
								break;
							}
						}

						//Set ball visible for next level
						ball.setVisible(true);

						try {
							playButton(e);
						} catch (IOException e1) {
							e1.printStackTrace();
						}

						levelCompletedAlert.close();
					});

					levelCompletedAlert.show();
				}
			});

			stBall.play();
		});

		ptBall.playFromStart();
	}


	private void StartStopMusic(Runnable runPause, Runnable runPlay) {

		//Actions when paused
		Main.mediaPlayer.setOnPaused(runPause);

		//Actions when started
		Main.mediaPlayer.setOnPlaying(runPlay);

		//If it has stopped, start music 
		if (Main.mediaPlayer.getStatus().equals(MediaPlayer.Status.PAUSED)) {
			Main.mediaPlayer.play();
		} 
		//If it is running, pause music
		else {
			Main.mediaPlayer.pause();
		}
	}

	//Credits scene
	public void credits() throws IOException {

		//Credits Pane
		creditsPane = FXMLLoader.load(getClass().getResource("Credits.fxml"));

		//Path transitions of balls

		PathTransition pt1 = new PathTransition();
		pt1.setDuration(Duration.millis(1000));
		pt1.setCycleCount(Transition.INDEFINITE);
		pt1.setNode(((Circle)creditsPane.getChildren().get(7)));
		pt1.setPath(((Ellipse)creditsPane.getChildren().get(0)));
		pt1.setRate(0.5);
		pt1.setInterpolator(Interpolator.LINEAR);
		pt1.play();

		PathTransition pt2 = new PathTransition();
		pt2.setDuration(Duration.millis(1000));
		pt2.setCycleCount(Transition.INDEFINITE);
		pt2.setNode(((Circle)creditsPane.getChildren().get(4)));
		pt2.setPath(((Ellipse)creditsPane.getChildren().get(1)));
		pt2.setRate(0.75);
		pt2.setInterpolator(Interpolator.LINEAR);
		pt2.play();

		PathTransition pt3 = new PathTransition();
		pt3.setDuration(Duration.millis(1000));
		pt3.setCycleCount(Transition.INDEFINITE);
		pt3.setNode(((Circle)creditsPane.getChildren().get(5)));
		pt3.setPath(((Ellipse)creditsPane.getChildren().get(2)));
		pt3.setRate(1);
		pt3.setInterpolator(Interpolator.LINEAR);
		pt3.play();

		PathTransition pt4 = new PathTransition();
		pt4.setDuration(Duration.millis(1000));
		pt4.setCycleCount(Transition.INDEFINITE);
		pt4.setNode(((Circle)creditsPane.getChildren().get(6)));
		pt4.setPath(((Ellipse)creditsPane.getChildren().get(3)));
		pt4.setRate(1.5);
		pt4.setInterpolator(Interpolator.LINEAR);
		pt4.play();

		//Set scene and show stage
		Scene creditsScene = new Scene(creditsPane);
		Stage creditsStage = new Stage();
		creditsStage.setTitle("Credits");
		creditsStage.getIcons().add(new Image("Images/CreditsIcon.png"));
		creditsStage.setScene(creditsScene);
		creditsStage.setResizable(false);
		creditsStage.show();
	}

	//Reset button function
	@FXML
	public void Reset(ActionEvent e) throws IOException {
		switchedImages = resetImages.clone();
		playButton(e);
	}

	//Quit button function
	@FXML
	public void Quit(ActionEvent e) {
		Platform.exit();
		System.exit(0);
	}

}