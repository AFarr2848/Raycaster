package org.farrarRayCaster;

import java.util.*;
import java.awt.*;

class Character {
  private double turnDir;
  private Rectangle playerRect;
  private ArrayList<Integer[]> rayCollisions = new ArrayList<Integer[]>();
  private ArrayList<Color> collisionColors = new ArrayList<Color>();
  private ArrayList<Integer> checkedPoints = new ArrayList<Integer>();
  private ArrayList<Integer> lineDistances = new ArrayList<Integer>();

  public Character(int xPos, int yPos, int size, int turnDir) {
    this.turnDir = turnDir;
    playerRect = new Rectangle(xPos, yPos, size, size);

  }

  public Rectangle getRect() {
    return playerRect;
  }

  public double getTurnDir() {
    return turnDir;
  }

  /**
   * Casts a ray from the character that collides with horizontal walls
   *
   * @param rayAngle  The offset angle in radians from the character's facing
   *                  angle
   * @param gridSize  The number of squares on the grid
   * @param mapSize   The size in pixels of the screen
   * @param mazeRects An ArrayList of the rectangles on the map
   *
   * @return An int pair of the first collided point on the ray
   */
  private Integer[] castRayHorizontal(double rayOffset, int gridSize, int mapSize, ArrayList<Rectangle> mazeRects) {
    int rayX, rayY;
    int offsetX, offsetY;
    int gridDistance = mapSize / gridSize;
    Integer[] outList = new Integer[2];
    double rayAngle = turnDir + rayOffset;
    rayAngle = limitAngle(rayAngle);
    // Collisions with horizontal walls
    // Ray looking up
    if (rayAngle < Math.PI) {
      rayY = (int) (Math.floor(playerRect.getCenterY() / gridDistance) * gridDistance);
      rayX = (int) ((playerRect.getCenterY() - rayY) / Math.tan(rayAngle) + playerRect.getCenterX());
      offsetY = -gridDistance;
      offsetX = (int) (-offsetY / Math.tan(rayAngle));
    }
    // Ray looking down
    else if (rayAngle > Math.PI) {
      rayY = (int) (Math.ceil(playerRect.getCenterY() / gridDistance) * gridDistance);
      rayX = (int) ((playerRect.getCenterY() - rayY) / Math.tan(rayAngle) + playerRect.getCenterX());
      offsetY = gridDistance;
      offsetX = (int) (-offsetY / Math.tan(rayAngle));
    }
    // Ray is perfectly horizontal
    else {
      rayX = (int) playerRect.getCenterX();
      rayY = (int) playerRect.getCenterY();
      offsetX = 0;
      offsetY = 0;
      outList[0] = (int) (Double.POSITIVE_INFINITY);
      outList[1] = (int) (Double.POSITIVE_INFINITY);
      return outList;
    }
    // Checks the current ray X and Y for a collision, iterates them by the offset
    // if there's no collision
    while (true) {
      checkedPoints.add(rayX);
      checkedPoints.add(rayY);
      // Iterates through all maze rectangles, checking for collisions with the edge
      // of the map or the maze walls
      for (Rectangle rect : mazeRects) {
        if (rect.contains(rayX, rayY) || isPointOOB(rayX, rayY, mapSize)) {
          outList[0] = rayX;
          outList[1] = rayY;
          return outList;
        }
      }
      rayX += offsetX;
      rayY += offsetY;
    }
  }

  /**
   * Casts a ray from the character that collides with vertical walls
   *
   * @param rayAngle  The offset angle in radians from the character's facing
   *                  angle
   * @param gridSize  The number of squares on the grid
   * @param mapSize   The size in pixels of the screen
   * @param mazeRects An ArrayList of the rectangles on the map
   *
   * @return An int pair of the first collided point on the ray
   */
  private Integer[] castRayVertical(double rayOffset, int gridSize, int mapSize, ArrayList<Rectangle> mazeRects) {
    int rayX, rayY;
    int offsetX, offsetY;
    int gridDistance = mapSize / gridSize;
    Integer[] outList = new Integer[2];
    double rayAngle = turnDir + rayOffset;
    rayAngle = limitAngle(rayAngle);
    // Collisions with Vertical walls
    // Ray looking right
    if (rayAngle < Math.PI / 2 || rayAngle > 3 * Math.PI / 2) {
      rayX = (int) (Math.ceil(playerRect.getCenterX() / gridDistance) * gridDistance);
      rayY = (int) ((playerRect.getCenterX() - rayX) * Math.tan(rayAngle) + playerRect.getCenterY());
      offsetX = gridDistance;
      offsetY = (int) (-offsetX * Math.tan(rayAngle));
    }
    // Ray looking left
    else if (rayAngle > Math.PI / 2 && rayAngle < 3 * Math.PI / 2) {
      rayX = (int) (Math.floor(playerRect.getCenterX() / gridDistance) * gridDistance);
      rayY = (int) ((playerRect.getCenterX() - rayX) * Math.tan(rayAngle) + playerRect.getCenterY());
      offsetX = -gridDistance;
      offsetY = (int) (-offsetX * Math.tan(rayAngle));
    }
    // Ray is perfectly vertical
    else {
      rayX = (int) playerRect.getCenterX();
      rayY = (int) playerRect.getCenterY();
      offsetX = 0;
      offsetY = 0;
      outList[0] = (int) Double.POSITIVE_INFINITY;
      outList[1] = (int) Double.POSITIVE_INFINITY;
      return outList;
    }
    // Checks the current ray X and Y for a collision, iterates them by the offset
    // if there's no collision
    while (true) {
      checkedPoints.add(rayX);
      checkedPoints.add(rayY);
      // Iterates through all maze rectangles, checking for collisions with the edge
      // of the map or the maze walls
      for (Rectangle rect : mazeRects) {
        if (rect.contains(rayX, rayY) || isPointOOB(rayX, rayY, mapSize)) {
          outList[0] = rayX;
          outList[1] = rayY;
          return outList;
        }
      }
      rayX += offsetX;
      rayY += offsetY;
    }
  }

  /**
   * Casts an amount of spread-out rays from the character, calculates their
   * closest collisions, and stores them in rayCollisions
   *
   * @param numRays   The number of rays to cast
   * @param gridSize  The side length of the screen in grid squares
   * @param mapSize   The side length of the screen in pixels
   * @param mazeRects A list of the maze rectangles to collide with
   */
  public void castRays(int numRays, int gridSize, int mapSize, ArrayList<Rectangle> mazeRects,
      double rayAngleSeperation) {
    Integer[] horizontalRayCol;
    Integer[] verticalRayCol;
    double vRayDist;
    double hRayDist;
    double distDiff;
    rayCollisions.clear();
    checkedPoints.clear();
    lineDistances.clear();
    collisionColors.clear();

    for (int i = numRays / 2; i >= -numRays / 2; i -= rayAngleSeperation) {
      verticalRayCol = castRayVertical(Math.toRadians(i), gridSize, mapSize,
          mazeRects);
      horizontalRayCol = castRayHorizontal(Math.toRadians(i), gridSize, mapSize, mazeRects);
      vRayDist = Math.hypot(verticalRayCol[0] - playerRect.getCenterX(),
          verticalRayCol[1] - playerRect.getCenterY());
      hRayDist = Math.hypot(horizontalRayCol[0] - playerRect.getCenterX(),
          horizontalRayCol[1] - playerRect.getCenterY());

      // Adds the line with the smaller distance
      distDiff = hRayDist - vRayDist;
      if (distDiff < -5) {

        rayCollisions.add(horizontalRayCol);
        lineDistances.add((int) (hRayDist * Math.cos(Math.toRadians(i * rayAngleSeperation))));
        collisionColors.add(new Color(153, 0, 255));
      } else if (distDiff > 5) {

        rayCollisions.add(verticalRayCol);
        lineDistances.add((int) (vRayDist * Math.cos(Math.toRadians(i * rayAngleSeperation))));
        collisionColors.add(new Color(102, 0, 204));
      }
      // If the lines distances are roughly equal, just copy the color from the row to
      // the left. Very lazy and will probably screw me over :)
      else {
        if (rayCollisions.size() > 1) {
          rayCollisions.add(verticalRayCol);
          lineDistances.add((int) (vRayDist * Math.cos(Math.toRadians(i * rayAngleSeperation))));
          collisionColors.add(collisionColors.get(collisionColors.size() - 1));
        } else {
          rayCollisions.add(verticalRayCol);
          lineDistances.add((int) (vRayDist * Math.cos(Math.toRadians(i * rayAngleSeperation))));
          collisionColors.add(new Color(102, 0, 204));
        }

      }

    }

  }

  public ArrayList<Integer[]> getRayCollisions() {
    return rayCollisions;
  }

  public ArrayList<Color> getCollisionColors() {
    return collisionColors;
  }

  public ArrayList<Integer> getCheckedPoints() {
    return checkedPoints;
  }

  public ArrayList<Integer> getLineDistances() {
    return lineDistances;
  }

  /**
   * Takes a radian angle and clamps it between PI and 2*PI
   *
   * @param radians The angle to be clamped
   * @return The limited angle
   */
  public static double limitAngle(double radians) {
    radians %= 2 * Math.PI;
    if (radians < 0) {
      radians += 2 * Math.PI; // Ensure it's positive
    }
    return radians;
  }

  /**
   * Checks if the character collides with any Rectangle in a given list
   *
   * @param rects An ArrayList of rects to check collisions with
   * @return True if a collision was detected
   */
  public boolean checkCollisions(ArrayList<Rectangle> rects) {
    boolean collides = false;
    for (Rectangle rect : rects) {
      if (this.playerRect.intersects(rect))
        collides = true;
    }
    return collides;
  }

  /**
   * Turns the character by a given radian value
   *
   * @param radians The angle to turn by
   */
  public void turn(double radians) {
    turnDir += radians;
    if (turnDir < 0)
      turnDir += 2 * Math.PI;
    turnDir %= 2 * Math.PI;
  }

  /**
   * Checks if a given point is out of bounds of a given screen size
   *
   * @param x          The X value of a point
   * @param y          THe Y value of a point
   * @param screenSize THe screen size
   * @return True if the point is OOB
   */
  public boolean isPointOOB(int x, int y, int screenSize) {
    if (x <= 0 || x >= screenSize - 5)
      return true;

    if (y <= 0 || y >= screenSize - 5)
      return true;

    return false;
  }

  /**
   * Checks if the character is out of bounds and moves it in bounds if true
   *
   * @param screenHeight The height of the screen
   * @param screenWidth  The width of the screen
   */
  public void checkOutOfBounds(int screenHeight, int screenWidth) {
    if (playerRect.x < 0)
      playerRect.x = 0;

    if (playerRect.x > screenWidth - playerRect.width)
      playerRect.x = screenWidth - playerRect.width;

    if (playerRect.y < 0)
      playerRect.y = 0;

    if (playerRect.y > screenHeight - playerRect.height)
      playerRect.y = screenHeight - playerRect.height;
  }

  /**
   * Moves the character forward by a distance given that it will not collide with
   * anything
   *
   * @param moveDist The distance to move
   * @param rectList An ArrayList of rects to collide with
   */
  public void moveForwardWithCollisions(int moveDist, ArrayList<Rectangle> rectList) {
    playerRect.x += moveDist * Math.cos(turnDir);
    if (checkCollisions(rectList))
      playerRect.x -= Math.floor((double) moveDist * Math.cos(turnDir));

    playerRect.y -= moveDist * Math.sin(turnDir);
    if (checkCollisions(rectList))
      playerRect.y += Math.ceil((double) moveDist * Math.sin(turnDir));
  }

  public void moveForward(int moveDist) {
    playerRect.x += moveDist * Math.cos(turnDir);
    playerRect.y -= moveDist * Math.sin(turnDir);
  }

}
