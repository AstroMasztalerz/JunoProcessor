
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.concurrent.TimeUnit;
//import static javafx.beans.binding.Bindings.or;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import org.apache.commons.io.FilenameUtils;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 
 */
public class LoadImage {

    public static String pathToProcessorFolder=null;
  public static String pathToLabel=null;
        

private static String getFileExtension(String fullName) {  //gets file extension to check whether IMG or PNG is being loaded
    
    String fileName = new File(fullName).getName();  
    int dotIndex = fileName.lastIndexOf('.');  //search for index where dot occurs. After the dot, file extension is expected
    return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);  //if dot is found, obtain the substring of characters after the dot
}


public static BufferedImage readImg(File f) throws IOException, InterruptedException  //read from PNG or IMG file to Buffered Image
{   if (getFileExtension(f.getName()).equalsIgnoreCase("png")==false && getFileExtension(f.getName()).equalsIgnoreCase("img")==false ) //if wrong file type loaded
        {
                JOptionPane.showMessageDialog(null, "WARNING: WRONG FILE TYPE. LOAD IMG OR PNG FILE!", "WARNING",2);
            }
else if (getFileExtension(f.getName()).equalsIgnoreCase("png")) //if input image is PNG  
    {
        return ImageIO.read(f); //return output of Java ImageIO PNG reader from file
    }
    else if ((getFileExtension(f.getName()).equalsIgnoreCase("img")))  //if processed image is IMG or img (Case ignored) and label is linked
            {
        if( pathToLabel!=null&&pathToProcessorFolder!=null) //if all is set up correctly
        {
           Runtime rt = Runtime.getRuntime();  //create new runtime process
        String[] commands = {pathToProcessorFolder+"/main.exe", "-input", f.getAbsolutePath(), "-label",  pathToLabel};  //commands for the proces: Execute the file converter, -input flag, path to input, -label flag, path to label
        Process proc = rt.exec(commands); //execute the process with the commands
        
             BufferedReader stdInput = new BufferedReader(new  //reader to read output from converter process and await end of processing
                InputStreamReader(proc.getInputStream()));  //input stream
 
        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(proc.getErrorStream())); //error stream shall any errors occur
 
        System.out.println("stdout:\n"); //outputs
        String s;
        while ((s = stdInput.readLine()) != null) { //while external process provides lines
            System.out.println(s); //print the lines
        }
 
        System.out.println("stderr:\n"); //same case for errors
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
            }
        
     String newFilePath=FilenameUtils.removeExtension(f.getAbsolutePath())+".png"; //this is to point to newly processed PNG file, in same directory and under same name as input IMG file, but with .png extension
  return  ImageIO.read(new File(newFilePath) ); //return the BufferedImage read from converted PNG at path.
    
}
       
        else if (pathToProcessorFolder==null) JOptionPane.showMessageDialog(null,"WARNING: NO PATH TO IMG PROCESSOR. Input Path to folder of IMG converter in Settings tab. Can not proceed.", "WARNING", 2);
        else if (pathToLabel==null)  JOptionPane.showMessageDialog(null,"WARNING: NO PATH TO IMG LABEL. Open corresponding IMG Label first by going to File/LoadLBL.", "WARNING", 2);
    }
return null; //if reading failed, return null
}

    static void setLBLPath(String absolutePath) {
     pathToLabel=absolutePath;
    }
    
     static void setPathToProcessorFolder(String absolutePath) {
     pathToProcessorFolder=absolutePath;
    }
    
}