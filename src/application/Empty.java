//Ahmet Abdullah GÜLTEKIN 150121025
//Ömer DELIGÖZ 150120035
//This program is a game with relocatable 4x4 tiles and demands to complete the correct pipe path with these tiles.
//This class provides features of empty tiles.

package application;

//Necessary import
import javafx.scene.image.Image;

public class Empty {

	//Class data field
	private int id = 0;
	private String type = "Empty";
	private String property = "none";
	private Image image;
	
	//No-arg Constructor
	public Empty() {
		image = new Image("Images/empty-free.jpg");
	}

	//Constructor has pipe's id, type and direction.
	public Empty(int id, String type, String property) {
		this.id = id;
		this.type = type;
		this.property = property;
		
		//Create an image according to tile type
		image = (property.equalsIgnoreCase("Free")) ? (new Image("Images/empty-free.jpg")) : (new Image("Images/empty-none.jpg"));
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
