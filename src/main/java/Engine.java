
import com.jhlabs.image.BoxBlurFilter;
import com.jhlabs.image.ContrastFilter;
import com.jhlabs.image.RGBAdjustFilter;
import com.jhlabs.image.UnsharpFilter;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
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
public class Engine {
    
    public static BufferedImage unbalanced;
    public static BufferedImage uncontrasted;
    public static BufferedImage tempDisp;
    public static String lbl;
    public static boolean processed;
    public static BufferedImage[] spectralChannels;
    public static BufferedImage rgbCompiled;
    public static String RAWfilePath;
    public static BufferedImage RGB;
    public static boolean IsRGBCompiled;

    static void setTempDisp(BufferedImage tempDisp) {
        Engine.tempDisp = tempDisp;
      
    }

    static BufferedImage getTempDisp() {
        return Engine.tempDisp;

    }

    static void save() throws IOException {
        ImageIO.write(tempDisp, "png", new File("C:/Users/temp.png"));
    }

    static void setLbl(String lbl) {
        Engine.lbl = lbl;
    }

    static String getLbl() {
        return Engine.lbl;
    }

    static BufferedImage[] AssembleFrames(BufferedImage RAW) throws IOException //this is to slice and re-assemble from RAW Buffered Image to sliced 3 R, G and B Buffered Images
    {
        IsRGBCompiled = false; //marks that RGB is not yet available

        GUI_Window.setRGBButtonState(false); //allows to turn on RGB button in GUI
        int HeightMax = RAW.getHeight();
        ImageStack stack = new ImageStack(new ImageForStack("BLUE", RAW.getWidth(), 128, RAW.getSubimage(0, 0, RAW.getWidth(), 128))); //creates a new image stack, with first framelet (B filter), 128 pix high
        int currentHeight = 128;  //start at height of first framelet
        String[] filterNames = {"BLUE", "GREEN", "RED"}; //spectral filter names

        while (currentHeight < HeightMax - 128) {
            stack.push(new ImageForStack(filterNames[(currentHeight / 128) % 3], RAW.getWidth(), 128, RAW.getSubimage(0, currentHeight, RAW.getWidth(), 128))); //slice image into 128 pix high framelets
            currentHeight += 128; //move by 128 to next framelet

        }
        BufferedImage[] output = new BufferedImage[3]; //Buffered image array for output
        output[0] = new BufferedImage(RAW.getWidth(), RAW.getHeight() / 3, BufferedImage.TYPE_BYTE_GRAY); //Blue output
        Graphics B = output[0].getGraphics(); //graphics for B
        output[1] = new BufferedImage(RAW.getWidth(), RAW.getHeight() / 3, BufferedImage.TYPE_BYTE_GRAY); //Green output
        Graphics G = output[1].getGraphics(); //graphics for G
        output[2] = new BufferedImage(RAW.getWidth(), RAW.getHeight() / 3, BufferedImage.TYPE_BYTE_GRAY); //Red  output
        Graphics R = output[2].getGraphics(); //graphics for R
        currentHeight = 0; //reset current Height to start of image
        while (stack.isEmpty() == false) //while data is available
        {

            B.drawImage(stack.pop().getImg(), 0, ((RAW.getHeight() / 3) - currentHeight), null);  //add framelet to Blue image at proper height

            if (stack.isEmpty() == false) {

                G.drawImage(stack.pop().getImg(), 0, ((RAW.getHeight() / 3) - currentHeight), null);  //add next framelet to Green image at proper height

            }
            if (stack.isEmpty() == false) {
                //gets next Image from stack
                R.drawImage(stack.pop().getImg(), 0, ((RAW.getHeight() / 3) - currentHeight), null); //add next framelet to Blue image at proper height
            }
            currentHeight += 114; //move by next 126 pixels and overlap of 2 pix. 2 Pixel overlap is caused by a overlap between consecutive image framelets from JunoCam. Each framelet is 128 pixels high, but 2 pixel rows cover the same imaging region

        }

        spectralChannels = output; //sends the assembled images to a spectralChanenls variable which stores them
        tempDisp = spectralChannels[0];
        Engine.tempDisp = output[0];
        GUI_Window.setImagePrev(output[0]); //sets the resulting Green channel image to preview window
        return output;

    }

    static void assembleRGB() throws IOException { //this is to combine separate spectral channels into a multispectral RGB Image in true colour pallete.

        BufferedImage channel0 = new BufferedImage(spectralChannels[0].getWidth(), spectralChannels[0].getHeight() + 500, BufferedImage.TYPE_INT_ARGB);  //convert image to INT ARGB Type for processing
        channel0.getGraphics().drawImage(spectralChannels[0], 0, 0, null);  //draw to new image

        BufferedImage channel1 = new BufferedImage(spectralChannels[1].getWidth(), spectralChannels[1].getHeight() + 500, BufferedImage.TYPE_INT_ARGB); //convert image to INT ARGB Type for processing
        channel1.getGraphics().drawImage(spectralChannels[1], 0, 0, null); //draw to new image

        BufferedImage channel2 = new BufferedImage(spectralChannels[2].getWidth(), spectralChannels[2].getHeight() + 500, BufferedImage.TYPE_INT_ARGB); //convert image to INT ARGB Type for processing
        channel2.getGraphics().drawImage(spectralChannels[2], 0, 0, null); //draw to new image

        channel2 = translateImage(39, channel2);  //translate red channel by 39 pix to align it. The aligment value is hard-coded to prevent user from accidental misuse. It should remain constant for most cases, unless spacecraft malfunction occurs.
        channel1 = translateImage(-156, channel1);  //translate Blue channel by -156 pix to align it

        //  ImageIO.write(channel2, "png", new File("C:/Users/Karol/Desktop/channel2Test.png"));  //RED output test save to check if aligment is correct
        //  ImageIO.write(channel1, "png", new File("C:/Users/Karol/Desktop/channel1Test.png"));  //BLUE output test save to check if aligment is correct
        //  ImageIO.write(channel0, "png", new File("C:/Users/Karol/Desktop/channel0Test.png")); //Green output test save to check if aligment is correct
        BufferedImage output = new BufferedImage(channel0.getWidth(), channel0.getHeight(), BufferedImage.TYPE_INT_ARGB);  //Create a ARGB Buffered Image to be filled with spectral data in RGB pallete
        for (int x = 0; x < spectralChannels[0].getWidth(); x++) { //iterate over width of raster
            for (int y = 0; y < spectralChannels[0].getHeight(); y++) { //iterate over height of raster
                int rgb = (channel2.getRGB(x, y) & 0x00FF0000) | (channel0.getRGB(x, y) & 0x0000FF00) | (channel1.getRGB(x, y) & 0x000000FF); //create RGB Value of given pixel at coordinates x,y to computed RGB Value based on corresponding pixel values in aligned R, G and B images

                output.setRGB(x, y, (rgb | 0xFF000000));  //assign this variable to output raster.
            }
        }
        // ImageIO.write(output, "png", new File("C:/Users/Karol/Desktop/Outred.png"));  //Write output for testing. Should contain a RGB Composite image
        RGB = output; //Send output to public variable responsible for storing image
        IsRGBCompiled = true; //mark that RGB compilation has terminated and RGB image is ready to display
        GUI_Window.setRGBButtonState(true); //allows to turn on RGB button in GUI to display RGB image.
        unbalanced = copyImage(RGB);  //copies the image to not color balanced version for future use.

    }

    public static BufferedImage[] getSpectralChannels() {
        return spectralChannels;
    }

    public static boolean getState() { //returns whether processing run has finished or not
        return processed;
    }

    public static BufferedImage getRGB() {  //returns the RGB Composed image from engine
        return RGB;
    }

    static void denoise(int amount, int radius) throws IOException {  //JHLabs denoising filter using Box Blurring
        BoxBlurFilter blur = new BoxBlurFilter(); //create box blur filter
        blur.setRadius(radius); //set radius from GUI slider
        blur.setIterations(amount); //set amount from GUI slider
        tempDisp = blur.filter(tempDisp, tempDisp);  //apply filter to the current image
        GUI_Window.setImagePrev(tempDisp); //updates temp disp preview after blurring

    }
//    static void denoise2(int amount, int radius) throws IOException {  //JHLabs denoising filter using Median of 8 pixel radius
//        MedianFilter blur = new MedianFilter(); //create box blur filter
//
//        tempDisp = blur.filter(tempDisp, tempDisp);  //apply filter to the current image
//        GUI_Window.setImagePrev(tempDisp); //updates temp disp preview after blurring
//
//    }

    public static void colourBalance() {
        RGBAdjustFilter Adjust = new RGBAdjustFilter((float) 0.08, (float) 0.12, (float) 0.65);
        Adjust.filter(tempDisp, tempDisp);
        GUI_Window.setImagePrev(tempDisp); //updates temp disp after blurring
    }

    public static void colourBalance(float R, float G, float B) {
       
        RGBAdjustFilter Adjust = new RGBAdjustFilter(R, G, B);
        Adjust.filter(unbalanced, tempDisp);
        GUI_Window.setImagePrev(tempDisp); //updates temp disp after blurring
    }

    public static void contrast() {
        ContrastFilter cf = new ContrastFilter();
        cf.setContrast((float) 1.3);
        cf.setBrightness((float) 0.85);
        cf.filter(tempDisp, tempDisp);
        GUI_Window.setImagePrev(tempDisp);
    }

    public static void contrast(float contrast, float brightness) {
        ContrastFilter cf = new ContrastFilter();
        cf.setContrast(contrast);
        cf.setBrightness(brightness);
        cf.filter(uncontrasted, tempDisp);
        GUI_Window.setImagePrev(tempDisp);
    }

    public static void sharpen(int amount, int radius) { //JHLabs sharpening filter
        UnsharpFilter usf = new UnsharpFilter(); //creates Unsharp mask filter
        usf.setAmount((float) amount); //sets amount of sharpening
        usf.setRadius(radius); //sets radius of sharpening
        tempDisp = usf.filter(tempDisp, tempDisp);
        GUI_Window.setImagePrev(tempDisp); //updates temp disp after blurring

    }

    static void resetToRaw() throws IOException, InterruptedException { //resets engine to original state by reloading RAW data. Used to revert all processing.
        BufferedImage i = LoadImage.readImg(new File(RAWfilePath));
        //   GUI_Window.setImagePrev(Scalr.resize(i, Scalr.Method.SPEED, 650));
        //Engine.setTempDisp(i); //passes the loaded image into Temp variable of Engine for processing
        if (GUI_Window.CH4Checkbox.isSelected() == true) //if working with a CH4 image, use proper assembly method on reset
        {
            Engine.AssembleFramesOfCH4(i);
        } else {
            Engine.AssembleFrames(i);  //assemble frames that are in engine after loading, works for casual RGB images, not CH4
        }
    }

    static void setRAWFilePath(String s) {  //set file path to RAW image.
        Engine.RAWfilePath = s;

    }

    static BufferedImage translateImage(int y, BufferedImage input) //function to translate image by Y pixels vertically, negative input is upwards. Used for RGB channels aligment
    {
        AffineTransform tx = new AffineTransform();
        tx.translate(0, y);

        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        input = op.filter(input, null);
        return input;
    }

    public static boolean getRGBStatus() {  //return whether RGB Compilation has been terminated or not. Used to prevent user from displaying image which has not yet been processed.
        return IsRGBCompiled;
    }

    public static void batchProcessRAWPNGtoRGBComposites(String inputPath, String outputPath) throws IOException {
        File folder = new File(inputPath);
        File[] listOfFiles = folder.listFiles();
        BufferedImage[] outputImages = new BufferedImage[listOfFiles.length];  //list of BufferedImages for processed outputs
        for (int i = 0; i < listOfFiles.length; i++) //iterate over all files in folder
        {
            outputImages[i] = ImageFileToFolder(listOfFiles[i]);

        }
        for (int i = 0; i < listOfFiles.length; i++) //iterate over all files in folder
        {
            ImageIO.write(outputImages[i], "png", new File(outputPath + "/" + i + ".png"));
        }

    }

    static BufferedImage AssembleFramesOfCH4(BufferedImage RAW) throws IOException //this is to slice and re-assemble from RAW Buffered Image to sliced 3 R, G and B Buffered Images
    {

        int HeightMax = RAW.getHeight();
        ImageStack stack = new ImageStack(new ImageForStack("CH4", RAW.getWidth(), 128, RAW.getSubimage(0, 0, RAW.getWidth(), 128))); //creates a new image stack, with first framelet (B filter), 128 pix high
        int currentHeight = 128;  //start at height of first framelet

        while (currentHeight < HeightMax - 128) {
            stack.push(new ImageForStack("CH4", RAW.getWidth(), 128, RAW.getSubimage(0, currentHeight, RAW.getWidth(), 128))); //slice image into 128 pix high framelets
            currentHeight += 128; //move by 128 to next framelet

        }

        BufferedImage output = new BufferedImage(RAW.getWidth() + 100, RAW.getHeight() / 3, BufferedImage.TYPE_BYTE_GRAY); //Blue output
        Graphics CH4 = output.getGraphics(); //graphics for CH4
        currentHeight = 0; //reset current Height to start of image
        int i = 0;
        while (stack.isEmpty() == false) //while data is available
        {

            if (GUI_Window.getCH4AligmentCheckboxState() == true) {
                CH4.drawImage(stack.pop().getImg(), (-10 * i) + ((i ^ 2) / 2), ((RAW.getHeight() / 3) - currentHeight), null);  //add framelet to CH4 composed image, at proper height and with specific horizontal transormation to align framelets in X axis according to a best-fit function.
                if (i < 4) {
                    i++;
                }
            } else {
                CH4.drawImage(stack.pop().getImg(), 0, ((RAW.getHeight() / 3) - currentHeight), null);  //add framelet to CH4 composed image, at proper height without the geometric correction in horizontal axis. Utilise for imagery taken at close range
            }

            currentHeight += 128; //move by next 126 pixels and overlap of 2 pix. 2 Pixel overlap is caused by a overlap between consecutive image framelets from JunoCam. Each framelet is 128 pixels high, but 2 pixel rows cover the same imaging region

        }

        GUI_Window.setImagePrev(output); //sets the resulting Green channel image to preview window
        tempDisp = output;
        Engine.tempDisp = output;
        return output;

    }

    public static BufferedImage ImageFileToFolder(File f) throws IOException {
        BufferedImage RAW = ImageIO.read(f);

        int HeightMax = RAW.getHeight();
        ImageStack stack = new ImageStack(new ImageForStack("BLUE", RAW.getWidth(), 128, RAW.getSubimage(0, 0, RAW.getWidth(), 128))); //creates a new image stack, with first framelet (B filter), 128 pix high
        int currentHeight = 128;  //start at height of first framelet
        String[] filterNames = {"BLUE", "GREEN", "RED"}; //spectral filter names

        while (currentHeight < HeightMax - 128) {
            stack.push(new ImageForStack(filterNames[(currentHeight / 128) % 3], RAW.getWidth(), 128, RAW.getSubimage(0, currentHeight, RAW.getWidth(), 128))); //slice image into 128 pix high framelets
            currentHeight += 128; //move by 128 to next framelet

        }
        BufferedImage[] output = new BufferedImage[3]; //Buffered image array for output
        output[0] = new BufferedImage(RAW.getWidth(), RAW.getHeight() / 3, BufferedImage.TYPE_BYTE_GRAY); //Blue output
        Graphics B = output[0].getGraphics(); //graphics for B
        output[1] = new BufferedImage(RAW.getWidth(), RAW.getHeight() / 3, BufferedImage.TYPE_BYTE_GRAY); //Green output
        Graphics G = output[1].getGraphics(); //graphics for G
        output[2] = new BufferedImage(RAW.getWidth(), RAW.getHeight() / 3, BufferedImage.TYPE_BYTE_GRAY); //Red  output
        Graphics R = output[2].getGraphics(); //graphics for R
        currentHeight = 0; //reset current Height to start of image
        while (stack.isEmpty() == false) //while data is available
        {

            B.drawImage(stack.pop().getImg(), 0, ((RAW.getHeight() / 3) - currentHeight), null);  //add framelet to Blue image at proper height

            if (stack.isEmpty() == false) {

                G.drawImage(stack.pop().getImg(), 0, ((RAW.getHeight() / 3) - currentHeight), null);  //add next framelet to Green image at proper height

            }
            if (stack.isEmpty() == false) {
                //gets next Image from stack
                R.drawImage(stack.pop().getImg(), 0, ((RAW.getHeight() / 3) - currentHeight), null); //add next framelet to Blue image at proper height
            }
            currentHeight += 114; //move by next 126 pixels and overlap of 2 pix. 2 Pixel overlap is caused by a overlap between consecutive image framelets from JunoCam. Each framelet is 128 pixels high, but 2 pixel rows cover the same imaging region

        }

        BufferedImage channel0 = new BufferedImage(output[0].getWidth(), output[0].getHeight() + 500, BufferedImage.TYPE_INT_ARGB);  //convert image to INT ARGB Type for processing
        channel0.getGraphics().drawImage(output[0], 0, 0, null);  //draw to new image

        BufferedImage channel1 = new BufferedImage(output[1].getWidth(), output[1].getHeight() + 500, BufferedImage.TYPE_INT_ARGB); //convert image to INT ARGB Type for processing
        channel1.getGraphics().drawImage(output[1], 0, 0, null); //draw to new image

        BufferedImage channel2 = new BufferedImage(output[2].getWidth(), output[2].getHeight() + 500, BufferedImage.TYPE_INT_ARGB); //convert image to INT ARGB Type for processing
        channel2.getGraphics().drawImage(output[2], 0, 0, null); //draw to new image

        channel2 = translateImage(39, channel2);  //translate red channel by 39 pix to align it. The aligment value is hard-coded to prevent user from accidental misuse. It should remain constant for most cases, unless spacecraft malfunction occurs.
        channel1 = translateImage(-156, channel1);  //translate Blue channel by -156 pix to align it


        BufferedImage outputimg = new BufferedImage(channel0.getWidth(), channel0.getHeight(), BufferedImage.TYPE_INT_ARGB);  //Create a ARGB Buffered Image to be filled with spectral data in RGB pallete
        for (int x = 0; x < output[0].getWidth(); x++) { //iterate over width of raster
            for (int y = 0; y < output[0].getHeight(); y++) { //iterate over height of raster
                int rgb = (channel2.getRGB(x, y) & 0x00FF0000) | (channel0.getRGB(x, y) & 0x0000FF00) | (channel1.getRGB(x, y) & 0x000000FF); //create RGB Value of given pixel at coordinates x,y to computed RGB Value based on corresponding pixel values in aligned R, G and B images

                outputimg.setRGB(x, y, (rgb | 0xFF000000));  //assign this variable to output raster.
            }
        }

        return outputimg;

    }

    public static BufferedImage copyImage(BufferedImage source) {
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }
    
   public static BufferedImage WriteTextOnImg(BufferedImage image)
   {
       String channel=""; //this is to get spectral channel of temp disp
       if (GUI_Window.CH4Checkbox.isSelected()) channel="CH4";
       else if(GUI_Window.Red_Button.isSelected()) channel="Red";
       else if(GUI_Window.Green_Button.isSelected()) channel="Green";  
       else if(GUI_Window.Blue_Button.isSelected()) channel="Blue";
       else channel="RGB";
       
       
   
    Graphics g = image.getGraphics();
    g.setFont(g.getFont().deriveFont(50f));
    g.drawString("JunoCam image:"+channel,0,100);
    g.drawString(LoadLbl.startTime, 0,150);
    g.drawString(LoadLbl.altitude, 0, 200);
    g.drawString(LoadLbl.longitude, 0, 250);
    g.drawString(LoadLbl.latitude, 0, 300);
    
    
    
    
    
    
    
    
    g.dispose();

   return image;
   }
}
