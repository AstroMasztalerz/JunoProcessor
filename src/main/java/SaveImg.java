
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *

 */
public class SaveImg {
    

public static void toFile(String path) throws IOException  //saves currently previewed image to PNG.
{
ImageIO.write(Engine.getTempDisp(),"png", new File(path));
}

public static void toFilesRGB(String path) throws IOException  //Saves separate R, G and B channels
{
    ImageIO.write(Engine.getSpectralChannels()[0],"png",new File(path+"G.png"));  //saves G channel
     ImageIO.write(Engine.getSpectralChannels()[1],"png",new File(path+"B.png")); //saves B channel
      ImageIO.write(Engine.getSpectralChannels()[2],"png",new File(path+"R.png")); //saves R channel
}


    
}