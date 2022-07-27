//Ahmet Abdullah GÜLTEKIN 150121025
//Ömer DELIGÖZ 150120035
//This program is a game with relocatable 4x4 tiles and demands to complete the correct pipe path with these tiles.
//This class provides features of static pipe.

package application;

//Necessary import
import javafx.scene.image.Image;

public class PipeStatic {

	//Class data field
	private int id = 0;
	private String type = "starter";
	private String property = "vertical";
	private Image image;
	
	//No-arg Constructor
	public PipeStatic() {

	}

	//Constructor has pipe's id, type and direction.
	public PipeStatic(int id, String type, String property) {
		this.id = id;
		this.type = type;
		this.property = property;
		
		//Check vertical or horizontal
		image = (property.equalsIgnoreCase("Vertical")) ? (new Image("Images/pipestatic-vertical.jpg")) : (new Image("Images/pipestatic-horizontal.jpg"));
		
		//Check vertical or horizontal
		//If it is curved it's image will define in this switch case
		switch (property) {
		case "00": image = new Image("Images/pipestatic00.jpg"); break;
		case "01": image = new Image("Images/pipestatic01.jpg");  break;
		case "10": image = new Image("Images/pipestatic10.jpg");  break;
		case "11": image = new Image("Images/pipestatic11.jpg");  break;
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
