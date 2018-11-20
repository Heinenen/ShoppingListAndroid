package smg.logic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author smg
 */
public class CategoryList {
    
    private String name;
    private List<String> categories;
    private PrintWriter out;
    private File list;
    private String dir;
    private String fileName;
    
    public CategoryList(){
        this.name = "CategoryList";
        this.dir = "src/";
        this.fileName = dir + name + ".txt";
        
        
        try {
            new File(dir).mkdirs();
            new File(fileName).createNewFile();
            list = new File(fileName);
            categories = new ArrayList<>();
        } catch(IOException e){
            System.out.println(e);
        }
    }
    
    public void addCategory(String cat){
        categories.add(cat);
        try{
            FileWriter fw = new FileWriter(fileName,true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            pw.println(cat);
        } catch (IOException e){
            System.out.println(e);
        }
    }
    
    public void saveList(){
        list.delete();
        try{
            out = new PrintWriter(fileName);
            for (int i = 0; i < categories.size(); i++){
                out.println(categories.get(i));
            }
            out.close();
        } catch(IOException e){
            System.out.println(e);
        }
    }
    
    public void removeCategory(String cat){
        categories.remove(cat);
        saveList();
    }
    
    public void addCategoryFS(String cat){
        categories.add(cat);
    }
    
}
