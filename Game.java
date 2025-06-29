/**
 * Game Class - Primary game logic for a Java-based Processing Game
/**
 * Game Class - Primary game logic for a Java-based Processing Game
 * @author Damian M
 * @version 6/12/25
 * No need to create PImage for bg
 */

//import processing.sound.*;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;



public class Game extends PApplet{

  //------------------ GAME VARIABLES --------------------//

  // VARIABLES: Processing variable to do Processing things
  PApplet p;

  // VARIABLES: Title Bar
  String titleText = "SuperRocketRanger";
  String extraText = "CurrentLevel?";
  String name = "Undefined";

  // VARIABLES: Whole Game
  AnimatedSprite runningHorse;
  boolean doAnimation;

  // VARIABLES: splashScreen
  Screen splashScreen;
  String splashBgFile = "images/SRR.jpg";
  //SoundFile song;

  // VARIABLES: grid1Grid Screen
  Grid grid1;
  String grid1BgFile = "images/SpaceBG.jpg";
  PImage grid1Bg;
  AnimatedSprite rocket;
  String meteorFile = "sprites/Meteor.png";
  Sprite meteor;
  String rocketFile = "sprites/Rocket.png";
  String rocketJson = "sprites/Rocket.json";
  int rocketRow = 4;
  int rocketCol = 1;
  int health = 1;

  // VARIABLES: endScreen
  World endScreen;
  String endBgFile = "images/go.jpeg";

  // VARIABLES: Tracking the current Screen being displayed
  Screen currentScreen;
  CycleTimer slowCycleTimer;
  CycleTimer otherCycleTimer;
  boolean start = true;


  //------------------ REQUIRED PROCESSING METHODS --------------------//

  // Processing method that runs once for screen resolution settings
  public void settings() {
    //SETUP: Match the screen size to the background image size
    size(800,500);  //these will automatically be saved as width & height

    // Allows p variable to be used by other classes to access PApplet methods
    p = this;
    
  }

  //Required Processing method that gets run once
  public void setup() {

    //SETUP: Set the title on the title bar
    surface.setTitle(titleText);
    p.imageMode(PConstants.CORNER);    //Set Images to read coordinates at corners

    //SETUP: Construct each Screen, World, Grid
    splashScreen = new Screen(p, "splash", splashBgFile);
    grid1 = new Grid(p, "space", grid1BgFile, 6, 5);
    endScreen = new World(p, "end", endBgFile);
    currentScreen = splashScreen;

    //SETUP: Construct Game objects used in All Screens
    runningHorse = new AnimatedSprite(p, "sprites/horse_run.png", "sprites/horse_run.json", 50.0f, 75.0f, 1.0f);

    //SETUP: Setup more grid1 objects
    meteor = new Sprite(p, meteorFile, 3.0f, 0, 0, false);
    rocket = new AnimatedSprite(p, rocketFile, rocketJson, 0.0f, 0.0f, 0.2f);
    rocket.resize(100,100);

    grid1.setTileSprite(new GridLocation (rocketRow, rocketCol), rocket);
    String[][] tileMarks = {
      {"R","N","B","Q","K","B","N","R"},
      {"P","P","P","P","P","P","P","P"},
      {"", "", "", "", "", "", "", ""},
      {"", "", "", "", "", "", "", ""},
      {"P","P","P","P","P","P","P","P"},
      {"R","N","B","Q","K","B","N","R"}
    };
    grid1.setAllMarks(tileMarks);
    grid1.startPrintingGridMarks();
    System.out.println("Done loading Level 1 (grid1)...");
    
    //SETUP: Sound
    // Load a soundfile from the sounds folder of the sketch and play it back
     //song = new SoundFile(p, "sounds/Lenny_Kravitz_Fly_Away.mp3");
     //song.play();
    
    System.out.println("Game started...");

  } //end setup()


  //Required Processing method that automatically loops
  //(Anything drawn on the screen should be called from here)
  public void draw() {

    // DRAW LOOP: Update Screen Visuals
    updateTitleBar();
    updateScreen();

    // DRAW LOOP: Set Timers
    int cycleTime = 1;  //milliseconds
    int slowCycleTime = 1200;  //milliseconds
    int otherCycleTime = 300;

    if(slowCycleTimer == null){
      slowCycleTimer = new CycleTimer(p, slowCycleTime);
    }
  
    if(otherCycleTimer == null){
      otherCycleTimer = new CycleTimer(p, otherCycleTime);
    }
    

    // DRAW LOOP: Populate & Move Sprites
    if(slowCycleTimer.isDone()){
      populateSprites();
    }

    if(otherCycleTimer.isDone()){
      moveSprites();
    }

    // DRAW LOOP: Pause Game Cycle
    currentScreen.pause(cycleTime);   // slows down the game cycles

    // DRAW LOOP: Check for end of game
    if(isGameOver()){
      endGame();
    }

  } //end draw()

  //------------------ USER INPUT METHODS --------------------//


  //Known Processing method that automatically will run whenever a key is pressed
  public void keyPressed(){

    //check what key was pressed
    System.out.println("\nKey pressed: " + p.keyCode); //key gives you a character for the key pressed

    //What to do when a key is pressed?
    
    //KEYS FOR grid1
    if(currentScreen == grid1){

      //set [S] key to move the player1 up & avoid Out-of-Bounds errors
      if(p.key == 'a' && rocketCol != 0){
      
        //Store old GridLocation
        
    
        GridLocation oldLocl = new GridLocation(rocketRow, rocketCol);
        grid1.clearTileSprite(oldLocl);
        //Erase image from previous location
        

        //change the field for rocketRow
        rocketCol--;
      }

      if(p.key == 'd' && rocketCol != 4){
      
        //Store old GridLocation
        GridLocation oldLocr = new GridLocation(rocketRow, rocketCol);
        
        grid1.clearTileSprite(oldLocr);
        //Erase image from previous location
        

        //change the field for rocketRow
        rocketCol++;
      }


    }



    //CHANGING SCREENS BASED ON KEYS
    //change to grid1 if 1 key pressed, level2 if 2 key is pressed
    if(p.key == '1'){
      currentScreen = grid1;
    } else if(p.key == 'e'){
      currentScreen = endScreen;
    }

  }

  // Known Processing method that automatically will run when a mouse click triggers it
  public void mouseClicked(){
    
    // Print coordinates of mouse click
    System.out.println("\nMouse was clicked at (" + p.mouseX + "," + p.mouseY + ")");

    // Display color of pixel clicked
    int color = p.get(p.mouseX, p.mouseY);
    PColor.printPColor(p, color);

    // if the Screen is a Grid, print grid coordinate clicked
    if(currentScreen instanceof Grid){
      System.out.println("Grid location --> " + ((Grid) currentScreen).getGridLocation());
    }

    // if the Screen is a Grid, "mark" the grid coordinate to track the state of the Grid
    if(currentScreen instanceof Grid){
      ((Grid) currentScreen).setMark("X",((Grid) currentScreen).getGridLocation());
    }

    // what to do if clicked? (ex. assign a new location to piece1)
    if(currentScreen == grid1){


    }
    
    // what to do if clicked? (ex. assign a new location to piece1)
    if(currentScreen == grid1){


    }
    

  }



  //------------------ CUSTOM  GAME METHODS --------------------//

  // Updates the title bar of the Game
  public void updateTitleBar(){

    if(!isGameOver()) {

      extraText = currentScreen.getName();

      //set the title each loop
      surface.setTitle(titleText);

      //adjust the extra text as desired
    
    }
  }

  // Updates what is drawn on the screen each frame
  public void updateScreen(){

    // UPDATE: first lay down the Background
    currentScreen.showBg();

    // UPDATE: splashScreen
    if(currentScreen == splashScreen){

      // Print an s in console when splashscreen is up
      System.out.print("s");

      // Change the screen to level 1 between 3 and 5 seconds
      if(splashScreen.getScreenTime() > 3000 && splashScreen.getScreenTime() < 5000){
        currentScreen = grid1;
      }
    }

    // UPDATE: grid1 Screen
    if(currentScreen == grid1){

      // Print a '1' in console when grid1
      System.out.print("1");

      // Displays the rocket image
      GridLocation rocketLoc = new GridLocation(rocketRow, rocketCol);
      grid1.setTileSprite(rocketLoc, rocket);

    
    }
    

    // UPDATE: End Screen
    if(currentScreen == endScreen){
    }

    // UPDATE: Any Screen
    if(doAnimation){
      runningHorse.animateHorizontal(0.5f, 1.0f, true);
    }

    // UPDATE: Other built-in to current World/Grid/HexGrid
    currentScreen.show();

  }

  // Populates enemies or other sprites on the Screen
  public void populateSprites(){

    if(currentScreen == grid1){
      int spawnCol = (int)(Math.random()*5);
      grid1.copyTileSprite(new GridLocation(0, spawnCol), meteor);
      // grid1.setTileSprite(new GridLocation(0, spawnCol), meteor);
    }

 
  }

  // Moves around the enemies/sprites on the Screen
  public void moveSprites(){

    if(currentScreen == grid1){

      for(int r = grid1.getNumRows()-2; r >= 0;r--){
        for(int c = 0; c < grid1.getNumCols(); c++){

        GridLocation locc = new GridLocation(r, c);
        GridLocation nextLoc = new GridLocation(r+1, c);
      
        if(!rocket.equals(grid1.getTileSprite(locc))){

          Sprite currentSprite = grid1.getTileSprite(locc);
          Sprite nextSprite = grid1.getTileSprite(nextLoc);

          if(meteor.equals(currentSprite) && rocket.equals(nextSprite)){
            health = 0;
          }
          else if(meteor.equals(currentSprite)){
            grid1.clearTileSprite(locc);
            grid1.setTileSprite(nextLoc, currentSprite);
          }
          else{

      
          }
        }
      } // closes col for loop
    } // closes row for loop
  } // closes grid1 check
} // closes moveSprites()
    
    //Loop through all of the rows & cols in the grid

        //Store the current GridLocation

        //Store the next GridLocation

        //Check if the current tile has an image that is not piece1      


          //Get image/sprite from current location
            

          //CASE 1: Collision with piece1
          //CASE 1: Collision with piece1


          //CASE 2: Move enemy over to new location


          //Erase image/sprite from old location

          //System.out.println(loc + " " + grid.hasTileImage(loc));

            
        //CASE 3: Enemy leaves screen at first column

  

  // Checks if there is a collision between Sprites on the Screen
  public boolean checkCollision(GridLocation loc, GridLocation nextLoc){

    //Check what image/sprite is stored in the CURRENT location
    // PImage image = grid.getTileImage(loc);
    // AnimatedSprite sprite = grid.getTileSprite(loc);

    //if empty --> no collision

    //Check what image/sprite is stored in the NEXT location

    //if empty --> no collision

    //check if enemy runs into player

      //clear out the enemy if it hits the player (using cleartTileImage() or clearTileSprite() from Grid class)

      //Update status variable

    //check if a player collides into enemy

    return false; //<--default return
  }

  // Indicates when the main game is over
  public boolean isGameOver(){
    if (health == 0){
      return true;
    }
    return false; //by default, the game is never over
  }

  // Describes what happens after the game is over
  public void endGame(){
      System.out.println("Game Over!");

      // Update the title bar

      // Show any end imagery
      currentScreen = endScreen;

  }

} // end of Game class
