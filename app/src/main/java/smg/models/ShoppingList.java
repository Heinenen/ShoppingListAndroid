package smg.models;

public class ShoppingList {

    private String slID;
    private String name;
    private String color;

    public ShoppingList(String slID, String name, String color){
        this.name = name;
        this.slID = slID;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlID() {
        return slID;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
