
import java.awt.image.BufferedImage;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Tomasz Masztalerz
 */
public class imageForStack {
public String filter;
public int x; //Horinontal dimension of image
public int y; //Vertical dimension of image
public BufferedImage img; //junocam image
public imageForStack next;

public imageForStack(String filter, int x, int y, BufferedImage img)
{
    this.filter=filter;
    this.x=x;
    this.y=y;
    this.img=img;
}


public int getX()
{
    return x;
}
public int getY()
{
    return y;
}
public BufferedImage getImg()
{
    return img;
}

public void setNext(imageForStack next)
{
    this.next=next;
}
public imageForStack getNext()
{
    return next;
}
public String getFilter()
{
    return filter;
}


}
