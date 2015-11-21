import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class EnemyBullet here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class EnemyBullet extends Actor
{
    double getX;//bullet coordinates
    double getY;//bullet coordinates
    int markerX;//marker coordinates
    int markerY;//marker coordinates
    boolean start=true;
    int speed;
    int damage;
    double x0;//change in bullet coords
    double y0;//change in bullet coords

    public EnemyBullet(int speed, int damage){

        this.speed=speed;
        this.damage=damage;
    }

    /**
     * Act - do whatever the Bullet wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() 
    {
        if(start){
            Player player=(Player)getWorld().getObjects(Player.class).get(0);
            markerX=player.getX();
            markerY=player.getY();
            getX=(double)getX();
            getY=(double)getY();
            turnTowards(markerX, markerY);
            double h=Math.sqrt(Math.pow(markerY-getY, 2)+Math.pow(markerX-getX, 2));
            x0=(markerX-getX)*speed/h;
            y0=(markerY-getY)*speed/h;
            start=false;
        }
        getX=getX+x0;
        getY=getY+y0;
        setLocation((int)getX,(int)getY);
        detectCollision();
    }    

    public void detectCollision(){
        Player player =(Player) getOneIntersectingObject(Player.class);
        if (player != null){
            player.damage(20);
            if(player.knockback==false){
                player.knockback=true;
                player.knockbackStrength=5;
                player.knockbackRotation=getRotation();
            }
            getWorld().removeObject(this);
            return;
        }

        Impassable wall = (Impassable) getOneIntersectingObject(Impassable.class);
        if (wall != null){
            getWorld().removeObject(this);
            return;
        }
    }
}