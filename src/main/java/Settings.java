
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Student 1
 */
public class Settings implements Serializable {
    private String pathToIMGLoader;
    
    public Settings(String pathToIMGLoader)
    {
    this.pathToIMGLoader=pathToIMGLoader;    
    }
    
    
    public String getPathToIMGLoader()
    {
        return this.pathToIMGLoader;
    }
    
    public static Settings loadSettings() throws IOException, ClassNotFoundException
    {
            Settings stg;
            ObjectInputStream is;
        
            FileInputStream fis = new FileInputStream("settings.stg");
            is = new ObjectInputStream( new BufferedInputStream(fis) );
            stg=(Settings)is.readObject();
            return stg;
        }
    }
   
