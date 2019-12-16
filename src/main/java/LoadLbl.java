
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 
 */
public class LoadLbl {
    public static  String startTime;
    public static  String altitude;
    public static String latitude;
    public static String longitude;
    
    
    static String read(String path)
    {
       

        String line = null; //current line
        String out=""; //output String
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                new FileReader(path);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                
                out=out+"\n"+line; //add current line to output string
                
                
                    if (line.contains("START_TIME"))  startTime=line.replaceAll("\\s", "");
                    else if (line.contains("SPACECRAFT_ALTITUDE")) altitude=line.replaceAll("\\s", "");
                    else if (line.contains("SUB_SPACECRAFT_LATITUDE")) latitude=line.replaceAll("\\s","");                    
                    else if (line.contains("SUB_SPACECRAFT_LONGITUDE")) longitude=line.replaceAll("\\s","");
                
            }   

            // Always close files.
            bufferedReader.close();
            Engine.setLbl(out); //passes read string to Engine
            LoadImage.setLBLPath(path); //passes path of read file to the image loader for opening associated LBL image
            return out; 
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                path + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + path + "'");                  
           
        }
        return null;
    }
}


        

