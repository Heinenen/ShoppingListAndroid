package smg.logic;

import java.io.Serializable;

/**
 *
 * @author smg
 */
public class Category implements Serializable {
    
    private String name;

    public Category (String name){
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    
}
