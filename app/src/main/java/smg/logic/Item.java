package smg.logic;

public class Item {

    private String id;
    private String name;
    private String category;
    private String amount;
    private String priority;
    private String price;
    private boolean check;

    public Item (String id, String name, String category, String amount, String priority, String price, int intCheck){
        this.id = id;
        this.name = name;
        this.category = category;
        this.amount = amount;
        this.priority = priority;
        this.price = price;
        if(intCheck == 1){
            this.check = true;
        } else {
            this.check = false;
        }
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

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getPrice(){
        return price;
    }

    public void setPrice(String price){
        this.price = price;
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
