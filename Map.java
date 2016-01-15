import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.*;
import java.io.*;

/**
 * The map class represents the world that the player is able to move around in and battle enemies.
 * 
 * @author Tiger Zhao
 * @version January 13, 2016
 */
public class Map extends World
{
    private Player player;
    private boolean gameOver = false;
    private boolean gameCompleted = false;

    //current absolute position within the world
    int curX =0;
    int curY=0;

    int curLevel;

    int curMapX=0; //indicates which map it is on
    int curMapY=0;
    GreenfootImage background = new GreenfootImage("grass.png");

    boolean fadedIn = false;

    boolean bossBattle = false;

    GreenfootSound music;

    /**
     * Constructor used for starting a map without player data.
     * 
     * @param level An integer indicating which level to load.
     */
    public Map(int level) {    

        super(800, 800, 1,false); 
        player=new Player();
        addObject(player, 400,400);
        player.setDefaults();
        player.setup();
        curLevel = level;

        setPaintOrder();

        String mapFile = "data/level_"+level+"/"+level+"_map_0_0.txt";
        loadMap(mapFile);
        fadeIn();
    }

    /**
     * Constructor used for starting a map with player data.
     * 
     * @param level An integer indicating which level to load.
     * @param player A player object that contains all the saved data.
     */
    public Map(int level, Player player) 
    {    
        super(800, 800, 1,false); 
        this.player = player;
        addObject(player, 400,400);
        player.setup();
        curLevel = level;

        setPaintOrder();

        String mapFile = "data/level_"+level+"/"+level+"_map_0_0.txt";
        loadMap(mapFile);

    }

    /**
     * Constructor used for switching between maps in a level.
     * 
     * @param level An integer indicating which level to load.
     * @param player A player object that contains all the saved data.
     * @param mapFile A string containing the file of the new map to load.
     * @param dirFrom A string indicating the direction that the player came from.
     * @param newX An integer indicating the new x coordinate of the map.
     * @param newY An integer indicating the new y coordinate of the map.
     */
    public Map(int level, Player player,String mapFile, String dirFrom, int newX, int newY){  //switching maps  
        super(800, 800, 1,false); 
        this.player=player;
        addObject(player, 400,400);
        player.setup();
        curLevel = level;
        setPaintOrder();
        curMapX=newX;
        curMapY=newY;
        loadMap(mapFile);

        //used to set player position
        if(bossBattle) return; //don't set new position if it is a boss battle, just stay in center
        int offset =64; //off set for setting player location according to passages
        if(dirFrom.equals("down")){
            List<DownPassage> li = getObjects(DownPassage.class);
            DownPassage d = li.get(0);
            player.setLocation(d.getX(), d.getY()-offset);
        }else if(dirFrom.equals("up")){
            List<UpPassage> li = getObjects(UpPassage.class);
            UpPassage d = li.get(0);
            player.setLocation(d.getX(), d.getY()+offset);
        }else if(dirFrom.equals("right")){
            List<RightPassage> li = getObjects(RightPassage.class);
            RightPassage d = li.get(0);
            player.setLocation(d.getX()-offset, d.getY());
        }else if(dirFrom.equals("left")){
            List<LeftPassage> li = getObjects(LeftPassage.class);
            LeftPassage d = li.get(0);
            player.setLocation(d.getX()+offset, d.getY());
        }
    }

    /**
     * Sets the paint order.
     */
    public void setPaintOrder(){
        super.setPaintOrder(Shade.class,GameOver.class, Equipment.class,Text.class, Button.class,InventoryBox.class,PlayerMenu.class,PlayerHealthBar.class,PlayerManaBar.class, PlayerExpBar.class, BossHealthBar.class,Boss1.class,Boss2.class, Boss3.class, Tree.class, EnemyExplosion.class, Player.class);
    }

    /**
     * Method used to end the game and transition to game over screen
     */
    public void endGame () {
        if (player.curHealth <= 0) {
            addObject (new GameOver(), 400, 400);
            gameOver = true;
        }
    }
    
    /**
     * Method used to end the game and transition to victory screen 
     */
    public void displayGameCompletion () {
        if (player.curLevel == 4) {
            addObject (new VictoryScreen(), 400, 400);
            gameCompleted = true;
        }
    }

    /**
     * Act - do whatever the EnemeyWeapon wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() {
        if (!gameOver) { //if not gameover, check to see if it is
            endGame();     
        }
        
        if (!gameCompleted) { // Check to see if game has been completed 
            displayGameCompletion();
        }

        scrollWorld();

        if(!fadedIn){ //fade in the map
            fadedIn=true;
            fadeIn();
        }
    }

    /**
     * Used to make the world a scroller. 
     */
    public void scrollWorld(){
        int pX = player.getX();
        int pY=player.getY();
        int offY = pY-getHeight()/2;
        int offX = pX-getWidth()/2;

        if (offY ==0 && offX==0){ //already in middle, no need to move
            return;
        }

        player.setLocation(400,400);

        //scrolling all the objects:
        List<Actor> li = getObjects(null);
        for(Actor a : li){
            //some objects should not scroll:
            if(!a.getClass().equals(Player.class) && !a.getClass().equals(Button.class)&& !a.getClass().equals(HUD.class) && !a.getClass().equals(Text.class)&& !a.getClass().equals(PlayerHealthBar.class)&& !a.getClass().equals(PlayerManaBar.class)&& !a.getClass().equals(PlayerExpBar.class)&& !a.getClass().equals(Slash.class)&& !a.getClass().equals(BossHealthBar.class)){
                a.setLocation(a.getX()-offX,a.getY()-offY);
            }
        }

        scrollBackground(-offX,-offY);
        curX+=offX;
        curY += offY;
    }

    /**
     * Used to scroll the background image with the player's movement.
     */
    public void scrollBackground(int offX, int offY) {
        int x;
        int y;
        GreenfootImage bg = new GreenfootImage(getBackground());
        if (offX > 0) {
            for (x = offX; x > 0; x -= bg.getWidth()) ;
        }else {
            for (x = offX; x < 0; x += bg.getWidth()) ;
            x -= bg.getWidth();
        }
        if (offY > 0) {
            for (y = offY; y > 0; y -= bg.getHeight()) ;
        }else {
            for (y = offY; y < 0; y += bg.getHeight());
            y -= bg.getHeight();
        }
        getBackground().clear();
        for (int i = x; i < getWidth(); i += bg.getWidth()) {
            for (int j = y; j < getHeight(); j += bg.getHeight()) {
                getBackground().drawImage(bg, i, j);
            }
        }
    }

    /**
     * @return A boolean indicating if the game is over or not.
     */
    public boolean getGameStatus() {
        return gameOver;
    }
    
    /**
     * @return A boolean indicating if the game has been completed or not. 
     */
    public boolean getGameCompletionStatus() {
        return gameCompleted;
    }

    /**
     * Used to convert an input stream to a string.
     * 
     * @return A string containing the data from the input stream.
     */
    public String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "\n";
    }

    /**
     * Loads the map from a text file.
     */
    public void loadMap(String filename){
        try{
            InputStream input = Map.class.getResourceAsStream(filename);
            String data = convertStreamToString(input);
            ArrayList<String> parts =new ArrayList(Arrays.asList(data.split("\n")));
            if(parts.size()>0){
                String line = parts.get(0).trim();
                //declare background
                setBackground(line+".png");
                //set music based on background;
                music = new GreenfootSound(line+".mp3");
                //music = new GreenfootSound("grass.mp3");
                music.setVolume(75);

            }
            parts.remove(0);

            //load all the objects
            for (String s : parts) {

                String[] temp = s.split(" ");
                String name = temp[0].trim();
                int x = Integer.valueOf(temp[1].trim());
                int y = Integer.valueOf(temp[2].trim());
                
                Random rand = new Random();
                int level=1;
                if(curLevel ==1){
                    level = rand.nextInt(9)+1;
                }else if(curLevel==2){
                    level = rand.nextInt(20)+10;
                }else if(curLevel ==3){
                    level = rand.nextInt(40)+30;
                }

                if(name.equals("tree")){
                    Tree t = new Tree();
                    addObject(t,x,y);
                }else if (name.equals("rock")){
                    Rock t = new Rock();
                    addObject(t,x,y);
                }else if (name.equals("sandbag")){
                    Sandbag t = new Sandbag();
                    addObject(t,x,y);
                }else if (name.equals("sentry")){
                    Sentry t = new Sentry(level);
                    addObject(t,x,y);
                }else if (name.equals("dog")){
                    Dog t = new Dog(level);
                    addObject(t,x,y);
                }else if (name.equals("mortar")){
                    MortarTower t = new MortarTower(level);
                    addObject(t,x,y);
                }else if (name.equals("gunman")){
                    Gunman t = new Gunman(level);
                    addObject(t,x,y);
                }else if (name.equals("bush")){
                    Bush t = new Bush();
                    addObject(t,x,y);
                }else if (name.equals("leftpassage")){
                    LeftPassage t = new LeftPassage();
                    addObject(t,x,y);
                }else if (name.equals("rightpassage")){
                    RightPassage t = new RightPassage();
                    addObject(t,x,y);
                }else if (name.equals("downpassage")){
                    DownPassage t = new DownPassage();
                    addObject(t,x,y);
                }else if (name.equals("uppassage")){
                    UpPassage t = new UpPassage();
                    addObject(t,x,y);
                }else if (name.equals("levelexit")){
                    LevelExit t = new LevelExit();
                    addObject(t,x,y);
                }else if (name.equals("boss1")){
                    Boss1 t = new Boss1();
                    addObject(t,x,y);
                    music = new GreenfootSound("boss_music.mp3");
                    bossBattle = true;
                    music.setVolume(40);
                }else if (name.equals("boss2")){
                    Boss2 t = new Boss2();
                    addObject(t,x,y);
                    music = new GreenfootSound("boss_music.mp3");
                    bossBattle = true;
                    music.setVolume(40);
                }else if (name.equals("boss3")){
                    Boss3 t = new Boss3();
                    addObject(t,x,y);
                    music = new GreenfootSound("boss_music.mp3");
                    bossBattle = true;
                    music.setVolume(40);
                }
            }
            input.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        music.playLoop();
        
    }

    /**
     * x y indicates how much to change.
     * y: + up ; - down
     * x: + right; - left
     * 
     * @param changeX The x change.
     * @param changeY The y change.
     * 
     */
    public void changeMap(int changeX, int changeY){ 
        int newX = curMapX+changeX;
        int newY = curMapY+changeY;
        String newMapFile = "data/level_"+curLevel+"/"+curLevel+"_map_"+newX+"_"+newY+".txt";

        String dirFrom="up";
        if (changeX >0){//going right
            dirFrom = "left";
        }else if (changeX <0){//going left
            dirFrom = "right";
        }if (changeY >0){//going up
            dirFrom = "down";
        }if (changeY <0){//going down
            dirFrom = "up";
        }

        fadeOut();
        music.stop();
        Greenfoot.setWorld(new Map(curLevel, player, newMapFile, dirFrom,newX,newY));

    }

    /**
     * Used to fade the screen in.
     */
    public void fadeIn(){
        Shade shade = new Shade();
        addObject(shade,getWidth()/2, getHeight()/2);
        for (int i =255; i >=0; i-=15){
            shade.getImage().setTransparency(i);
            Greenfoot.delay(1);
        }
        removeObject(shade);
    }

    /**
     * Used to fade the screen out.
     */
    public void fadeOut(){
        Shade shade = new Shade();
        addObject(shade,getWidth()/2, getHeight()/2);
        for (int i =0; i <=255; i+=15){
            shade.getImage().setTransparency(i);
            Greenfoot.delay(1);
        }

    }

    /**
     * Used to pause all objects.
     */
    public void pauseAll(){
        /*
         * objects to pause: player, bullets, enemy, enemy bullet, mortar, particle, mortar target,weapons
         */

        List<Player> a = getObjects(Player.class);
        for (Player p: a){
            p.paused =true;
            p.curse = false;
        }

        List<PlayerProjectile> b = getObjects(PlayerProjectile.class);
        for (PlayerProjectile p: b){
            p.paused =true;
        }

        List<Enemy> c = getObjects(Enemy.class);
        for (Enemy p: c){
            p.paused =true;
        }

        List<EnemyBullet> d = getObjects(EnemyBullet.class);
        for (EnemyBullet p: d){
            p.paused =true;
        }

        List<Mortar> e = getObjects(Mortar.class);
        for (Mortar p: e){
            p.paused =true;
        }

        List<Particle> f = getObjects(Particle.class);
        for (Particle p: f){
            p.paused =true;
        }

        List<Weapon> g = getObjects(Weapon.class);
        for (Weapon p: g){
            p.paused =true;
        }

        List<MortarTarget> h = getObjects(MortarTarget.class);
        for (MortarTarget p: h){
            p.paused =true;
        }

        List<EnemyExplosion> i = getObjects(EnemyExplosion.class);
        for (EnemyExplosion p: i){
            p.paused =true;
        }

        List<PlayerExplosion> j = getObjects(PlayerExplosion.class);
        for (PlayerExplosion p: j){
            p.paused =true;
        }
        
        List<Shrine> l = getObjects(Shrine.class);
        for (Shrine p: l){
            a.get(0).paused =true;
            a.get(0).curse = false;
        }
        
        List<Boss3> k = getObjects(Boss3.class);
        for (Boss3 p: k){
            a.get(0).paused =true;
            a.get(0).curse = false;
        }
        
        List<Spawner> m = getObjects(Spawner.class);
        for (Spawner p: m){
            p.paused =true;
        }
        
        List<Boss3shield> n = getObjects(Boss3shield.class);
        for (Boss3shield p: n){
            p.paused =true;
        }
        
    }

    /**
     * Used to unpause all objects.
     */
    public void unpauseAll(){
        List<Player> a = getObjects(Player.class);
        for (Player p: a){
            p.paused =false;
            if(getObjects(Shrine.class).size()>0){
                Shrine shrine = (Shrine) getObjects(Shrine.class).get(0);
                if(shrine.offCurse == false){
                    p.curse = true;
                }
            }
        }

        List<PlayerProjectile> b = getObjects(PlayerProjectile.class);
        for (PlayerProjectile p: b){
            p.paused =false;
        }

        List<Enemy> c = getObjects(Enemy.class);
        for (Enemy p: c){
            p.paused =false;
        }

        List<EnemyBullet> d = getObjects(EnemyBullet.class);
        for (EnemyBullet p: d){
            p.paused =false;
        }

        List<Mortar> e = getObjects(Mortar.class);
        for (Mortar p: e){
            p.paused =false;
        }

        List<Particle> f = getObjects(Particle.class);
        for (Particle p: f){
            p.paused =false;
        }

        List<Weapon> g = getObjects(Weapon.class);
        for (Weapon p: g){
            p.paused =false;
        }

        List<MortarTarget> h = getObjects(MortarTarget.class);
        for (MortarTarget p: h){
            p.paused =false;
        }

        List<EnemyExplosion> i = getObjects(EnemyExplosion.class);
        for (EnemyExplosion p: i){
            p.paused =false;
        }

        List<PlayerExplosion> j = getObjects(PlayerExplosion.class);
        for (PlayerExplosion p: j){
            p.paused =false;
        }
        
        List<Shrine> l = getObjects(Shrine.class);
        for (Shrine p: l){
            p.paused =false;
        }
        
        List<Spawner> m = getObjects(Spawner.class);
        for (Spawner p: m){
            p.paused =false;
        }
        
        List<Boss3> k = getObjects(Boss3.class);
        for (Boss3 p: k){
            a.get(0).paused =false;
            a.get(0).curse = true;
        }
        
        List<Boss3shield> n = getObjects(Boss3shield.class);
        for (Boss3shield p: n){
            p.paused =false;
        }
    }

}
