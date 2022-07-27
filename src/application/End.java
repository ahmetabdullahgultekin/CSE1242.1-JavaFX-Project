//Ahmet Abdullah GÜLTEKIN 150121025
//Ömer DELIGÖZ 150120035
//This program is a game with relocatable 4x4 tiles and demands to complete the correct pipe path with these tiles.
//This class provides features of end pipe.

package application;

//Necessary imports
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;

public class End {

	//Class data field
	private int id = 0;
	private String type = "end";
	private String property = "vertical";
	private Image image;
	
	//No-arg Constructor
	public End() {

	}

	//Constructor has pipe's id, type and direction.
	public End(int id, String type, String property) {
		this.id = id;
		this.type = type;
		this.property = property;
		
		//Check vertical or horizontal
		if (property.equalsIgnoreCase("Vertical")) {
			//Create new image for imageview
			image = new Image("Images/end-vertical-down.jpg");

		} 
		else if (property.equalsIgnoreCase("Horizontal")){
			//Create new image for imageview
			image = new Image("Images/end-horizontal-left.jpg");

		}
		//If direction do not match throw a alert window
		else {
			Alert alertLevel = new Alert(AlertType.ERROR);
			alertLevel.setTitle("Input Error");
			alertLevel.setHeaderText("Direction not Matched!");
			alertLevel.setContentText("Find correct input.");
			alertLevel.showAndWait();
		}
		
	}

	//Getters and Setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDirect() {
		return property;
	}

	public void setDirect(String property) {
		this.property = property;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

}
