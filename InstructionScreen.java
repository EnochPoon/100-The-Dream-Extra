import greenfoot.*;

/**
 * Instruction Screen Coding 
 * 
 * NOTE: All instruction screen images are supposed to animate; however, due to 
 * heap space errors, they are not implemented. Also, as of now - some instruction
 * screen images (although gifs) do not animate. Some new revisions have been 
 * made and there is currently no access to software to change to animated gif.
 * 
 * @author Gary Yu
 * @version January 13, 2016
 */
public class InstructionScreen extends World {
    private GifImage myWorldBackground = new GifImage ("MyWorld.gif");
    private StartButton start = new StartButton ();
    private SkipButton skip = new SkipButton ();
    private NextButton next = new NextButton ();
    private BackButton back = new BackButton ();
    private boolean popInstructions = false;
    private boolean delayedInstructions = false;
    private int i = 0; // Used for changing instruction screens

    // Arrays to store instruction screen images
    String images[] = new String [4];
    GreenfootSound music = new GreenfootSound("titlescreen_music.mp3");
    
    /**
     * Default constructor for objects of class InstructionScreen.
     */
    public InstructionScreen()
    {    
        // Create a new world with 800x800 cells with a cell size of 1x1 pixels.
        super(800, 800, 1);  

        // Adding the necessary start button
        addObject (start, 405, 643); 

        for (int i = 0 ; i < images.length ; i ++) {
            images[i] = "Instruction" + (i) + ".gif";
        }

        music.setVolume(75);
        music.playLoop();

        Greenfoot.start();
    }

    /**
     * Helps with the screen transitions 
     */
    public void act () {
        if (!start.checkObjectRemoved()) {
            setBackground (myWorldBackground.getCurrentImage());
        }

        if (start.checkObjectRemoved()) {
            if (!delayedInstructions) {
                //myWorldBackground = new GifImage (images[0]);
                setBackground (new GreenfootImage (images[0]));
                delayedInstructions = true;
            }
            if (!addNextIcon()) {
                addNextIcon();
            }
            if (!addSkipIcon()) {
                addSkipIcon();
            }
            popInstructions = true; // Apppropriate to pop up instructions
            // setBackground (myWorldBackground.getCurrentImage());
        }

        // If skip button is clicked, game will auto-start with no instructions
        if (skip.buttonClicked()) {
            Greenfoot.setWorld (new LevelSelector(music));
        }

        changeInstructionScreens();
    }

    /**
     * @return The start button object 
     */
    public StartButton getStartButton () { 
        return start; 
    }

    /**
     * @return Whether the instruction screens have popped up or not 
     */
    public boolean poppedInstructions () {
        return popInstructions;
    }

    /**
     * @return Whether it's appropriate to add the next icon 
     */
    public boolean addNextIcon () {
        if (popInstructions) {
            addObject (next, 704, 705);
            return true;
        } else {
            return false;
        }
    }

    
    /**
     * @return Whether it's appropriate to add the skip icon 
     */
    public boolean addSkipIcon () {
        if (next.nextButtonHere()) {
            addObject (skip, 560, 720);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Changes each instruction screen by shuffling through an array of 
     * instruction screen images
     */
    public void changeInstructionScreens () {
        if (next.buttonClicked()) {
            if (i <= images.length-2) {
                // myWorldBackground = new GifImage (images[++i]);
                setBackground (new GreenfootImage (images[++i]));
                if (i == 1) {
                    addObject (back, 79, 705);
                } 
            } else {
                Greenfoot.setWorld (new LevelSelector(music));
            }
            next.buttonNotClicked();
        }
        if (back.buttonClicked()) {
            // myWorldBackground = new GifImage (images[--i]);
            setBackground (new GreenfootImage (images[--i]));
            if (i == 0) {
                back.buttonNotClicked();
                removeObject (back);
            }
            back.buttonNotClicked();
        }
    }
}
