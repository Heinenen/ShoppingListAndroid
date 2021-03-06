package smg.models;

public class Item {

    private String id;
    private String name;
    private String category;
    private String amount;
    private String price;
    private boolean priority;
    private boolean check;

    public Item (String id, String name, String category, String amount, String price,int intPriority, int intCheck){
        this.id = id;
        this.name = name;
        this.category = category;
        this.amount = amount;
        this.price = price;
        this.priority = intPriority == 1;
        this.check = intCheck == 1;
    }

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPrice(){
        return price;
    }

    public void setPrice(String price){
        this.price = price;
    }

    public boolean isPriority() {
        return priority;
    }

    public void setPriority(boolean priority) {
        this.priority = priority;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String toString(){
        return System.getProperty("line.separator") + "Name: " + name + " Category: " + category + " Amount: " + amount + " Priority: " + priority + " Checked: " + check;
    }
}
