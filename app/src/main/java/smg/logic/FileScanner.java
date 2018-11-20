package smg.logic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author caspar
 */
public class FileScanner {
    
    private BufferedReader reader;
    private List<String> items;
    private List<String> categories;
    /**
     *
     */
    public void openShoppingList() {
        
        items = new ArrayList<>();
        try{
            reader = new BufferedReader(new FileReader("src/saves/List1.txt"));
        } catch(FileNotFoundException e){
            System.out.println(e);
        }
        
        smg.logic.ShoppingList List1 = new smg.logic.ShoppingList("List1");
        Category category = new Category("category");
        
        // StringBuilder sb = new StringBuilder();
        String letter;
        StringBuilder sb = new StringBuilder();
        
        try{
            while((letter = reader.readLine()) != null){
                String temp = "";
                int i = 0;
                int p = 0;
                System.out.println(letter);
                Item item = new Item(null, null, 0, null);
                List1.addItemFS(item);
                while(i < 4 && p >= 0){
                    //System.out.print(letter.charAt(p));
                    if(letter.charAt(p) == ';' && letter.charAt(p+1) == ';' && letter.charAt(p+2) == ';'){
                        p = p +3;
                        switch (i) {
                            case 0:
                                item.setName(temp);
                                break;
                            case 1:
                                item.setCategory(category);
                                break;
                            case 2:
                                item.setPriority(Integer.parseInt(temp));
                                break;
                            case 3:
                                item.setAmount(temp);
                                break;
                            case 4:
                                if("t".equals(temp)){
                                    item.setCheck(true);
                                } else {
                                    item.setCheck(false);
                                }   break;
                            default:
                                break;
                        }
                        temp = "";
                        i++;
                        if(i > 4) continue;
                    }
                    temp += Character.toString(letter.charAt(p));
                    System.out.println(temp);
                    p++;
                }
            }
            System.out.println(List1.getItems().toString());
        } catch (IOException f){
            System.out.println(f);
        }
    }
    
    public void openCategoryList(){
                
        categories = new ArrayList<>();
        try{
            reader = new BufferedReader(new FileReader("src/CategoryList"));
        } catch(FileNotFoundException e){
            System.out.println(e);
        }
        
        CategoryList List1 = new CategoryList();
        
        // StringBuilder sb = new StringBuilder();
        String letter;
        StringBuilder sb = new StringBuilder();
                try{
            while((letter = reader.readLine()) != null){
                Category category = new Category(letter);
                List1.addCategoryFS(category);
            }
            
        }catch (IOException f){
            System.out.println(f);
        }
    }
}
    

