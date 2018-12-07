package smg.models;

public class ShoppingList {

    private String slID;
    private String name;

    public ShoppingList(String slID, String name){
        this.name = name;
        this.slID = slID;
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

    public void setSlID(String slId) {
        this.slID = slId;
    }
}
