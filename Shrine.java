import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Shrine here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Shrine extends Actor
{
    boolean offCurse = false;
    int timer = 1000;
    boolean hit = false;
    /**
     * Act - do whatever the Shrine wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() 
    {
        //when the shrine is hit by a melee attack, the player loses its curse from boss3 for a short amount of time. The boss will be healed too.
        Player p = (Player)getWorld().getObjects(Player.class).get(0);
        Boss3 boss = (Boss3)getWorld().getObjects(Boss3.class).get(0);
        turn(4);
        if(isTouching(Slash.class) || isTouching(PlayerProjectile.class)){
            p.curse = false;
            offCurse = true;
            if(isTouching(PlayerProjectile.class)){

                PlayerRocket rocket = (PlayerRocket) getOneIntersectingObject(PlayerRocket.class);
                if(rocket != null){
                    getWorld().addObject(new PlayerExplosion(rocket.damage), rocket.getX(), rocket.getY());
                    
                }
                removeTouching(PlayerProjectile.class);
            }
            if(!hit){
                boss.hpBar.damage((boss.hpBar.startHealth - boss.hpBar.health)/-4);//heals a quarter of missing health
                hit = true;
                timer = 1000;
            }

        }

        if(offCurse){
            timer--;
            if(timer == 0){
                offCurse = false;
                p.curse = true;
                timer = 1000;
                hit = false;
            }
        }

        if(timer == 985){//to not be affected twice by the same slash
            hit = false;
        }
    }    
}