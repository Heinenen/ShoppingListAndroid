package smg.models;

public class ShoppingList {

    private String slID;
    private String name;
    private int color;

    public ShoppingList(String slID, String name, int color){
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
