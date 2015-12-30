import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class P90 here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class P90 extends Weapon
{
    int bulletSpeed =20;
    int bulletDamage=30;
    GreenfootImage gunSprite = new GreenfootImage("gun_sprite.png");
    
    public P90(){
        //super("p90");
        super();
        speedDelay=10;
        speed =10;
    }

    /**
     * Act - do whatever the P90 wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() 
    {
        super.act();
    }    

    public void use(Player player){ //x and y: current player pos
        GreenfootImage gunImage = new GreenfootImage(player.getImage());

        if (speedDelay >= speed){
            
            GreenfootSound effect = new GreenfootSound("p90_shoot.wav");
            effect.setVolume(75);
            effect.play();
            speedDelay =0;
            PlayerBullet bullet = new PlayerBullet(20,30);
            getWorld().addObject(bullet, player.getX(),player.getY());

        }
        gunImage.drawImage(gunSprite, 12,0);
        player.setImage(gunImage);
        
    }

}
