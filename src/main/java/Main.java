
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *

 */
public class Main {
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
    //new GUI();
    
//    imageForStack Z= new imageForStack("Blue", 600,400,null);
//    imageForStack F= new imageForStack("Red", 942,445,null);
//    ImageStack is=new ImageStack(Z);
//    System.out.println(Z.getX());
//    is.push(F);
//    
//    System.out.println(is.pop().getX());
//    
//    System.out.println(is.pop().getX());
   System.out.println("RUNNING"); 
    new GUI_Window().setVisible(true);
    new Engine();
    
 
    
    
}
}
