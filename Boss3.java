
import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.Random;
import java.util.List;
import java.awt.Color;
/**
 * The third and final boss of the game.
 * 
 * @author Enoch Poon
 * @version January 14, 2016
 */
public class Boss3 extends Boss
{
    int imageDelay = 5;
    int image = 0;
    boolean start = true;
    BossHealthBar hpBar;
    int maxAttackTimer = 100;
    int attackTimer = maxAttackTimer;
    int healTimer = 10;
    GreenfootImage[] images = {
            new GreenfootImage("boss3_0.png"),
            new GreenfootImage("boss3_1.png"),
            new GreenfootImage("boss3_0.png"),
            new GreenfootImage("boss3_idle.png"),
            new GreenfootImage("boss3_2.png"),
            new GreenfootImage("boss3_3.png"),
            new GreenfootImage("boss3_2.png"),
            new GreenfootImage("boss3_idle.png")
        };
    int moveDelay = 1;
    boolean chasing = false;
    
    /**
     * Act - do whatever the Boss3 wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() 
    {

        if(paused){
            return;
        }
        Player p = (Player)getWorld().getObjects(Player.class).get(0);
        if(start){
            hpBar = new BossHealthBar(200000, this);
            getWorld().addObject(hpBar, 400, 775);
            getWorld().addObject(new Text("Master", 18,Color.BLACK),400,775);
            start = false;
            p.curse = true;
            //add spawners and shrines
            getWorld().addObject(new Spawner(0), 0, 0);
            getWorld().addObject(new Spawner(1), 0, getWorld().getWidth());
            getWorld().addObject(new Shrine(), getX(), getY());
        }
        if(imageDelay-- <=0){
            imageDelay = (chasing)? 3:5;
            setImage(images[image++%8]);
        }
        if(getWorld().getObjects(Boss3shield.class).size() == 0 && 
        getWorld().getObjects(LaserBeam.class).size() == 0 && 
        getWorld().getObjects(ExplodeWarning.class).size() == 0){
            turnTowards(p.getX(), p.getY());
        }else{
            image = 3;
            attackTimer = maxAttackTimer;
        }
        controlWeapons();
        controlMovement();
        controlDeath();

    }    

    public void controlWeapons(){
        Random r = new Random();
        if(!chasing)attackTimer--;
        if(isTouching(Player.class) && chasing){
            getWorld().addObject(new ExplodeWarning(),getX(),getY());
            chasing = false;
            attackTimer = maxAttackTimer;
            return;
        }
        if(isTouching(Player.class) && attackTimer < 25){
            getWorld().addObject(new ExplodeWarning(),getX(),getY());
            chasing = false;
            attackTimer = maxAttackTimer;
            return;
        }
        if(attackTimer < 0 && !isTouching(Boss3shield.class)){
            int curAttack = r.nextInt(3);

            switch(curAttack){
                case 0 ://charge laser
                
                chargeLaser(getRotation(), 50, getX(), getY());
                break;

                case 1 ://spawn knifemen from spawners, create shield
                GreenfootSound e = new GreenfootSound("spawn_effect.wav");
                e.setVolume(80);
                e.play();
                List<Spawner> spawners = getWorld().getObjects(Spawner.class);
                for(Spawner s : spawners){
                    s.activate(40);
                }
                getWorld().addObject(new Boss3shield(), getX(), getY());
                break;

                case 2 ://chase
                if(canSeePlayer()){
                    chasing = true;
                }else{
                    chargeLaser(getRotation(), 50, getX(), getY());
                }
                break;
            }

            attackTimer = maxAttackTimer;
        }

        if(isTouching(Boss3shield.class)){
            hpBar.damage(-10);
            if(hpBar.health >= 100000){
                hpBar.health = 100000;
            }

        }
    }

    /**
     * Recursively trace a laser beam
     */
    public void chargeLaser(int rotation, int length, int x, int y){
        if(length == 0)return;
        LaserBeam beam = new LaserBeam(length);
        getWorld().addObject(beam, x, y);
        beam.setRotation(rotation);
        beam.move((length==50)? 50:99);
        chargeLaser(rotation, length - 1, beam.getX(), beam.getY());
    }

    public void damage(int d){
        if(isTouching(Boss3shield.class) && (isTouching(PlayerProjectile.class) || isTouching(PlayerExplosion.class))){//shield makes boss immune to ranged
            return;
        }
        else if(isTouching(Boss3shield.class) && isTouching(Slash.class)){//shield makes boss resistant to melee
            hpBar.damage(d/2);
        }else{
            hpBar.damage(d);
        }

        if (hpBar.health<=0){
            dead =true;
        }
    }

    public void controlDeath(){
        if(dead){
            Player p = (Player) getWorld().getObjects(Player.class).get(0);
            p.gainExp(30000);

            if(p.curGameLevel<4){ //increase the player's game progress
                p.curGameLevel=4;
                //Text text = new Text("Congratulations! You have defeated the Master!", 24, Color.WHITE);
                //getWorld().addObject(text,400,200);
            }

            LevelExit exit = new LevelExit();
            getWorld().addObject(exit, getX(), getY());
            exit.removeBlocking();
            p.curse = false;
            getWorld().removeObject(this);
        }
    }

    public void controlMovement(){
        if(moveDelay-- <= 0 &&
        getWorld().getObjects(Boss3shield.class).size() == 0 && 
        getWorld().getObjects(LaserBeam.class).size() == 0 && 
        getWorld().getObjects(ExplodeWarning.class).size() == 0){
            move((chasing)?7:3);
            moveDelay = (chasing)?0:1;
        }
    }
}

