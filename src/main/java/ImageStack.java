/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 
 */
public class ImageStack {
    ImageForStack root;

public ImageStack(ImageForStack root)
{
    this.root=root;
}
public boolean isEmpty()
{
        return root == null; 
}

public void push (ImageForStack ifs)
{
    if (isEmpty())
    {
        root=ifs;
    }
    else 
    {
        ImageForStack temp=root;
        root=ifs;
        ifs.setNext(temp);
    }
}

public ImageForStack pop()
{
    if (isEmpty())
    {
        System.out.println("EMPTY STACK");
        return null;
    }
    else
    {
        ImageForStack output=root;
        root=root.next;
        return output;
    }
}
public ImageForStack peek()
{
    if (isEmpty())
    {
        System.out.println("EMPTY STACK");
        return null;
    }
    else
    {
        return root;
    }
}










}