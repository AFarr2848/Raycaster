package org.farrarRayCaster;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ScreenController {
  public static final int screenSize = 800;
  public static final int mazeSquares = 6;
  public static final int numRays = 100;
  public static ArrayList<Rectangle> mazeRects = randMaze.makeMaze(screenSize, screenSize, mazeSquares);
  public static Character player = new Character(0, 0, 10, 0);
  public static ArrayList<Integer> keysPressed = new ArrayList<Integer>();

  public static void loop() {
    if (keysPressed.indexOf(KeyEvent.VK_W) != -1 || keysPressed.indexOf(KeyEvent.VK_UP) != -1) {
      player.moveForwardWithCollisions(5, mazeRects);
    }
    if (keysPressed.indexOf(KeyEvent.VK_S) != -1 || keysPressed.indexOf(KeyEvent.VK_DOWN) != -1) {
      player.moveForwardWithCollisions(-5, mazeRects);
    }
    if (keysPressed.indexOf(KeyEvent.VK_A) != -1 || keysPressed.indexOf(KeyEvent.VK_LEFT) != -1) {
      player.turn(Math.toRadians(5));
    }
    if (keysPressed.indexOf(KeyEvent.VK_D) != -1 || keysPressed.indexOf(KeyEvent.VK_RIGHT) != -1) {
      player.turn(Math.toRadians(-5));
    }

    player.checkOutOfBounds(screenSize, screenSize);
    player.castRays(numRays, mazeSquares, screenSize, mazeRects, 1);
  }

  public static void main(String[] args) {
    JFrame frame2D = new JFrame("2D");
    DrawScreen.initKeys(frame2D, keysPressed);
    // Screen init
    frame2D.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame2D.setSize(screenSize, screenSize);
    DrawScreen panel2D = new DrawScreen();
    frame2D.add(panel2D);
    frame2D.setVisible(true);

    JFrame frame3D = new JFrame("3D");
    DrawScreen.initKeys(frame3D, keysPressed);
    // Screen init
    frame3D.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame3D.setSize(screenSize, screenSize);
    Draw3DScreen panel3D = new Draw3DScreen();
    frame3D.add(panel3D);
    frame3D.setVisible(true);

    // Starts the main loop
    Timer timer = new Timer(16, e -> {
      loop();
      frame2D.repaint(); // Triggers a call to paintComponent
      frame3D.repaint(); // Triggers a call to paintComponent
    });
    timer.start();

  }
}
