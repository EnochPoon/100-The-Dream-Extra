import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.Color;
/**
 * Used by the Boss3 to display a warning of an explosive attack.
 * 
 * @author Enoch Poon
 * @version January 14, 2016
 */
public class ExplodeWarning extends Actor
{
    int timer = 50;
    public ExplodeWarning(){
        getImage().scale(100,100);
        
    }
    /**
     * Act - do whatever the ExplodeWarning wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() 
    {
        Player p = (Player) getWorld().getObjects(Player.class).get(0);
        if(timer-- == 0){
            getWorld().addObject(new EnemyExplosion(p, 700), getX(), getY());
            getWorld().removeObject(this);
            return;
        }
        getImage().setTransparency(getImage().getTransparency() - 5);
    }    
}
