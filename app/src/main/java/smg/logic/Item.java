package smg.logic;

public class Item {
    private String name;
    private String category;
    private String amount;
    private boolean priority;
    private boolean check;

    public Item (String name, String category, String amount, boolean priority){
        this.name = name;
        this.category = category;
        this.amount = amount;
        this.priority = priority;
        this.check = false;
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
        return "Name: " + name + "; Category: " + category + "Amount: " + amount + "; Priority: " + priority + "; Checked: " + check;
    }
}
