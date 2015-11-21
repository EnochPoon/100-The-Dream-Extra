import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class MissileTarget here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MortarTarget extends Actor
{
    Mortar mortar;
    public MortarTarget(Mortar parent){
        mortar=parent;
        getImage().scale(60,60);
        getImage().setTransparency(0);
    }

    /**
     * Act - do whatever the MissileTarget wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() 
    {
        
        int distance=(Math.abs(mortar.getX()-getX())/2+Math.abs(mortar.getY()-getY())/2);
        if(255-distance<0){
            getImage().setTransparency(0);
        }else{
            getImage().setTransparency(255-distance);
        }

    }    
}