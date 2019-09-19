/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Tomasz Masztalerz
 */
public class ImageStack {
    imageForStack root;

public ImageStack(imageForStack root)
{
    this.root=root;
}
public boolean isEmpty()
{
        return root == null; 
}

public void push (imageForStack ifs)
{
    if (isEmpty())
    {
        root=ifs;
    }
    else 
    {
        imageForStack temp=root;
        root=ifs;
        ifs.setNext(temp);
    }
}

public imageForStack pop()
{
    if (isEmpty())
    {
        System.out.println("EMPTY STACK");
        return null;
    }
    else
    {
        imageForStack output=root;
        root=root.next;
        return output;
    }
}
public imageForStack peek()
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