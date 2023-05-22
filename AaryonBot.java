package bots;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;


public class AaryonBot extends Bot {
    Image current, imageDown, imageUp, imageLeft, imageRight;
    BotHelper botHelper = new BotHelper();

    int moveCurrent = 9;
    int count = 0;

    // Variables for possible moves
    int moveRight = BattleBotArena.RIGHT;
    int moveLeft = BattleBotArena.LEFT;
    int moveDown = BattleBotArena.DOWN;
    int moveUp = BattleBotArena.UP;

    int fireRight = BattleBotArena.FIRERIGHT;
    int fireLeft = BattleBotArena.FIRELEFT;
    int fireDown = BattleBotArena.FIREDOWN;
    int fireUp = BattleBotArena.FIREUP;

    // To center the bots position since it starts at the top left corner
    int botOffset = RADIUS;

    boolean upEdge, downEdge, leftEdge, rightEdge;

    Bullet closestBullet;
    BotInfo closestBot;
    BotInfo closestDeadBot;

    /* (non-Javadoc)
     * @see bots.Bot#newRound()
     * Sets moveCurrent to be stay by default and resets all booleans
     */
    @Override
    public void newRound() {
        moveCurrent = 9;
        rightEdge = false;
        leftEdge = false;
        downEdge = false;
        upEdge = false;
    }

    /**
     * See's if my bot is at the edge of the arena
     * @param botX:int
     * @param botY:int
     */
    public void getLocation(double botX, double botY) {
        if(botX <= BattleBotArena.LEFT_EDGE + 5) { // Offset the position by a tiny amount to make it turn before it hits the edge
            leftEdge = true;
        } else if(botX >= BattleBotArena.RIGHT_EDGE - 35) {
            rightEdge = true;
        } else if(botY <= BattleBotArena.TOP_EDGE + 5) {
            upEdge = true;
        } else if(botY >= BattleBotArena.BOTTOM_EDGE - 35) {
            downEdge = true;
        }
    }

    /**
     * Method used to run the code to stay away from nearby bots
     * @param botX:int
     * @param botY:int
     * @param enemyX:int
     * @param enemyY:int
     * @param enemyDistance:int distance from my bot to the closest enemy
     */
    public void dodgeBot(double botX, double botY, double enemyX, double enemyY, double enemyDistance) {
        if(botX >= enemyX && botY >= enemyY) {
            if(enemyDistance <= 75) { // If the enemy bot is very close then go the perpendicular direction
                moveCurrent = moveRight;
            } else { // If not then just go parallel
                moveCurrent = moveDown;
            }
        } else if(botX >= enemyX && botY < enemyY) {
            if(enemyDistance <= 75) {
                moveCurrent = moveRight;
            } else {
                moveCurrent = moveUp;
            }
        } else if(botX < enemyX && botY >= enemyY) {
            if(enemyDistance <= 75) {
                moveCurrent = moveLeft;
            } else {
                moveCurrent = moveDown;
            }
        } else if(botX < enemyX && botY < enemyY) {
            if(enemyDistance <= 75) {
                moveCurrent = moveLeft;
            } else {
                moveCurrent = moveUp;
            }
        }

    }

    /**
     * Method used to dodge any bullets coming towards my bot
     * @param botX:int
     * @param botY:int
     * @param bulletX:int
     * @param bulletY:int
     * @param bulletSpeedX:int
     * @param bulletSpeedY:int
     * @param bulletDistance:int distance from my bot to the closest bullet
     */
    public void dodgeBullet(double botX, double botY, double bulletX, double bulletY, double bulletSpeedX, double bulletSpeedY, double bulletDistance) {
        // The bullet will either only have a speed in the x-direction or the y-direction
        if(botX >= bulletX && botY >= bulletY) {
            if(bulletSpeedX != 0) { // If it's moving along the x-axis move away
                moveCurrent = moveDown;
            } else if(bulletSpeedY != 0) { // Same thing but with the y-axis
                moveCurrent = moveRight;
            }
        } else if(botX >= bulletX && botY < bulletY) {
            if(bulletSpeedX != 0) {
                moveCurrent = moveUp;
            } else if(bulletSpeedY != 0) {
                moveCurrent = moveRight;
            }
        } else if(botX < bulletX && botY >= bulletY) {
            if(bulletSpeedX != 0) {
                moveCurrent = moveDown;
            } else if(bulletSpeedY != 0) {
                moveCurrent = moveLeft;
            }
        } else if(botX < bulletX && botY < bulletY) {
            if(bulletSpeedX != 0) {
                moveCurrent = moveUp;
            } else if(bulletSpeedY != 0) {
                moveCurrent = moveLeft;
            }
        }
    }

    /**
     * If it runs into a grave then it will move in the perpendicular direction
     * @param botX:int
     * @param botY:int
     * @param deadBotX:int
     * @param deadBotY:int
     */
    public void avoidDeadBot(double botX, double botY, double deadBotX, double deadBotY) {
        // Checks each edge of the grave along the entire edge, so if my bot is between the top and bottom or left and right of the grave
        if(((int)botX + botOffset) == ((int)deadBotX - botOffset) && ((int)botY + botOffset) >= ((int)deadBotY - botOffset) && ((int)botY - botOffset) <= ((int)deadBotY + botOffset)) {
            upEdge = true;
        } else if(((int)botX - botOffset) == ((int)deadBotX + botOffset) && ((int)botY + botOffset) >= ((int)deadBotY - botOffset) && ((int)botY - botOffset) <= ((int)deadBotY + botOffset)) {
            downEdge = true;
        } else if(((int)botY - botOffset) == ((int)deadBotY + botOffset) && ((int)botX + botOffset) >= ((int)deadBotX - botOffset) && ((int)botX - botOffset) <= ((int)deadBotX + botOffset)) {
            rightEdge = true;
        } else if(((int)botX + botOffset) == ((int)deadBotY - botOffset) && ((int)botX + botOffset) >= ((int)deadBotX - botOffset) && ((int)botX - botOffset) <= ((int)deadBotX + botOffset)) {
            leftEdge = true;
        }
    }

    /**
     * Shoots nearby bots depedning on their location
     * @param botX:int
     * @param botY:int
     * @param enemyX:int
     * @param enemyY:int
     * @param enemyDistance:int distance from my bot to the closest enemy
     * @param shotOk:boolean is true if my bot has enough bullets to shoot one
     */
    public void shoot(double botX, double botY, double enemyX, double enemyY, double enemyDistance, boolean shotOk) {
        // If the bot is more along the x-axis then shoot along it
        // If it is further along the y-axis then shoot in that direction
        if(botX > enemyX && botY >= enemyY && shotOk) {
            if(Math.abs(botX-enemyX) >= Math.abs(botY-enemyY)) {
                moveCurrent = fireLeft;
            } else if(Math.abs(botX-enemyX) < Math.abs(botY-enemyY)) {
                moveCurrent = fireUp;
            }
        } else if(botX > enemyX && botY < enemyY && shotOk) {
            if(Math.abs(botX-enemyX) >= Math.abs(botY-enemyY)) {
                moveCurrent = fireLeft;
            } else if(Math.abs(botX-enemyX) < Math.abs(botY-enemyY)) {
                moveCurrent = fireDown;
            }
        } else if(botX < enemyX && botY >= enemyY && shotOk) {
            if(Math.abs(botX-enemyX) >= Math.abs(botY-enemyY)) {
                moveCurrent = fireRight;
            } else if(Math.abs(botX-enemyX) < Math.abs(botY-enemyY)) {
                moveCurrent = fireUp;
            }
        } else if(botX < enemyX && botY < enemyY && shotOk) {
            if(Math.abs(botX-enemyX) >= Math.abs(botY-enemyY)) {
                moveCurrent = fireRight;
            } else if(Math.abs(botX-enemyX) < Math.abs(botY-enemyY)) {
                moveCurrent = fireDown;
            }
        } else if(botX == enemyX && botY > enemyY) {
            moveCurrent = fireDown;
        } else if(botX == enemyX && botY < enemyY) {
            moveCurrent = fireUp;
        }
    }

    /**
     * Moves towards an enemy bot if it is not near one
     * @param botX:int
     * @param botY:int
     * @param enemyX:int
     * @param enemyY:int
     */
    public void chase(double botX, double botY, double enemyX, double enemyY) {
        // Similar to shooting code, depending on where the enemy is then move in that direction
        if(botX >= enemyX && botY >= enemyY) {
            if(Math.abs(botX-enemyX) >= Math.abs(botY-enemyY)) {
                moveCurrent = moveUp;
            } else if(Math.abs(botX-enemyX) < Math.abs(botY-enemyY)) {
                moveCurrent = moveLeft;
            }
        } else if(botX >= enemyX && botY < enemyY) {
            if(Math.abs(botX-enemyX) >= Math.abs(botY-enemyY)) {
                moveCurrent = moveDown;
            } else if(Math.abs(botX-enemyX) < Math.abs(botY-enemyY)) {
                moveCurrent = moveLeft;
            }
        } else if(botX < enemyX && botY >= enemyY) {
            if(Math.abs(botX-enemyX) >= Math.abs(botY-enemyY)) {
                moveCurrent = moveUp;
            } else if(Math.abs(botX-enemyX) < Math.abs(botY-enemyY)) {
                moveCurrent = moveRight;
            }
        } else if(botX < enemyX && botY < enemyY) {
            if(Math.abs(botX-enemyX) >= Math.abs(botY-enemyY)) {
                moveCurrent = moveDown;
            } else if(Math.abs(botX-enemyX) < Math.abs(botY-enemyY)) {
                moveCurrent = moveRight;
            }
        }
    }
    
    /* (non-Javadoc)
     * @see bots.Bot#getMove(arena.BotInfo, boolean, arena.BotInfo[], arena.BotInfo[], arena.Bullet[])
     * Code that handles all my logic and runs all my methods
     */
    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        if(count >= 10) {
            // Makes sure that after the bot hits the edge it has some time to turn around
            newRound();
            count = 0;
        }
        count++;

        // Next 3 if statements ensures that it catches errors and doesn't pass a blank list
        if(bullets.length != 0) {
            closestBullet = botHelper.findClosest(me, bullets);
        } else {
            closestBullet = null;
        }

        if(liveBots.length != 0) {    
            closestBot = botHelper.findClosest(me, liveBots);
        } else {
            closestBot = null;
        }

        if(deadBots.length != 0) {
            closestDeadBot = botHelper.findClosest(me, deadBots);
        } else {
            closestDeadBot = null;
        }

        // Centers the (x,y) of the bot
        double botX = me.getX() + botOffset;
        double botY = me.getY() + botOffset;

        double enemyX;
        double enemyY;
        double deadBotX;
        double deadBotY;
        double bulletX;
        double bulletY;
        double bulletSpeedX;
        double bulletSpeedY;
        double bulletDistance;
        double enemyDistance;

        // Checks if there is any bullet in the arena, if not then set it equal to 0
        try {
            bulletX = closestBullet.getX();
            bulletY = closestBullet.getY();
            bulletSpeedX = closestBullet.getXSpeed();
            bulletSpeedY = closestBullet.getYSpeed();

            bulletDistance = botHelper.calcDistance(botX, botY, bulletX, bulletY);
        } catch(NullPointerException e) {
            bulletDistance = 0;
            bulletX = 0;
            bulletY = 0;
            bulletSpeedX = 0;
            bulletSpeedY = 0;
        }

        // Checks if there's any enemies in the arena
        try {
            enemyX = closestBot.getX() + botOffset;
            enemyY = closestBot.getY() + botOffset;
            enemyDistance = botHelper.calcDistance(botX, botY, enemyX, enemyY);
        } catch(NullPointerException e) {
            enemyDistance = 0;
            enemyX = 0;
            enemyY = 0;
        }

        // Checks if there's any graves in the arena
        try {
            deadBotX = closestDeadBot.getX() + botOffset;
            deadBotY = closestDeadBot.getY() + botOffset;
        } catch(NullPointerException e) {
            deadBotX = 0;
            deadBotY = 0;
        }

        // Checks if the bot is hitting the edge or a grave
        getLocation(botX, botY);
        avoidDeadBot(botX, botY, deadBotX, deadBotY);

        if((bulletDistance <= 50 || enemyDistance <= 100) && count != 10) {
            // If the bot is within 50 units of a bullet then dodge
            if(bulletDistance <= 50 && bulletDistance > 0) {
                dodgeBullet(botX, botY, bulletX, bulletY, bulletSpeedX, bulletSpeedY, bulletDistance);
            } else {
                // Otherwise if it is within 100 units of an enemy then dodge
                if(enemyDistance <= 100 && enemyDistance > 0) {
                    dodgeBot(botX, botY, enemyX, enemyY, enemyDistance);
                }
            }
        } else if(enemyDistance > 100 && count != 10) {
            // If it is farther than 100 units then get close to it
            chase(botX, botY, enemyX, enemyY);
        } else {
            // If the closest bot isn't on my team then shoot it once count reaches 8
            if(closestBot.getTeamName() != "Elite 4") {
                shoot(botX, botY, enemyX, enemyY, enemyDistance, shotOK);
            }
            
        }

        // If any of the edge/graveyard detection varaibles are true then act accordingly
        // This essentially overwrites what move it was supposed to do
        if(upEdge) {
            moveCurrent = moveDown;
        }
        if(downEdge) {
            moveCurrent = moveUp;
        }
        if(rightEdge) {
            moveCurrent = moveLeft;
        }
        if(leftEdge) {
            moveCurrent = moveRight;
        }

        return moveCurrent;
    }

    /* (non-Javadoc)
     * @see bots.Bot#draw(java.awt.Graphics, int, int)
     * Draws my bot with the appropriate image on the screen
     */
    @Override
    public void draw(Graphics g, int x, int y) {
        if(moveCurrent == BattleBotArena.UP) {
            current = imageUp;
        } else if(moveCurrent == BattleBotArena.DOWN) {
            current = imageDown;
        } else if(moveCurrent == BattleBotArena.LEFT) {
            current = imageLeft;
        } else if(moveCurrent == BattleBotArena.RIGHT) {
            current = imageRight;
        }
        if (current != null)
			g.drawImage(current, x, y, RADIUS *2, RADIUS *2, null);
		else
		{
			g.setColor(Color.lightGray);
			g.fillOval(x, y, RADIUS *2, RADIUS *2);
		}
    }

    /* (non-Javadoc)
     * @see bots.Bot#getName()
     * Sets my bot name
     */
    @Override
    public String getName() {
        return "AanuBot";
    }

    /* (non-Javadoc)
     * @see bots.Bot#getTeamName()
     * Sets team name
     */
    @Override
    public String getTeamName() {
        return "Elite 4";
    }

    /* (non-Javadoc)
     * @see bots.Bot#outgoingMessage()
     */
    @Override
    public String outgoingMessage() {
        return null;
    }

    /* (non-Javadoc)
     * @see bots.Bot#incomingMessage(int, java.lang.String)
     */
    @Override
    public void incomingMessage(int botNum, String msg) {
        
    }

    /* (non-Javadoc)
     * @see bots.Bot#imageNames()
     * Sets the names of all my images
     */
    @Override
    public String[] imageNames() {
        String[] images = {"Monster_Down.png", "Monster_Up.png", "Monster_Left.png", "Monster_Right.png"};
        return images;
    }

    /* (non-Javadoc)
     * @see bots.Bot#loadedImages(java.awt.Image[])
     * Loads an image depending on the direction the bot is moving
     */
    @Override
    public void loadedImages(Image[] images) {
        if(images != null) {
            if(images.length > 0) {
                imageDown = images[0];
            }
            if(images.length > 1) {
                imageUp = images[1];
            }
            if(images.length > 2) {
                imageLeft = images[2];
            }
            if(images.length > 3) {
                imageRight = images[3];
            }

            current = imageUp;
        }
    }
}
