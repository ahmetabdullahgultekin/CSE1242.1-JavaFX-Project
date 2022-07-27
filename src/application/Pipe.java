//Ahmet Abdullah GÜLTEKIN 150121025
//Ömer DELIGÖZ 150120035
//This program is a game with relocatable 4x4 tiles and demands to complete the correct pipe path with these tiles.
//This class provides features of relocatable pipe.

package application;

//Necessary import
import javafx.scene.image.Image;

public class Pipe {

	//Class data field
	private int id = 0;
	private String type = "Starter";
	private String property = "Vertical";
	private Image image;
	
	//No-arg Constructor
	public Pipe() {

	}

	//Constructor has pipe's id, type and direction.
	public Pipe(int id, String type, String property) {
		this.id = id;
		this.type = type;
		this.property = property;
		
		//Check vertical or horizontal
		image = (property.equalsIgnoreCase("Vertical")) ? (new Image("Images/pipe-vertical.jpg")) : (new Image("Images/pipe-horizontal.jpg"));
		
		//Check vertical or horizontal
	    //If it is curved it's image will define in this switch case
		switch (property) {
		case "00": image = new Image("Images/pipe00.jpg");  break;
		case "01": image = new Image("Images/pipe01.jpg");  break;
		case "10": image = new Image("Images/pipe10.jpg");  break;
		case "11": image = new Image("Images/pipe11.jpg");  break;
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
