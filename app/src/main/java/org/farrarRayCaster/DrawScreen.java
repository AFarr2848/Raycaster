package org.farrarRayCaster;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class DrawScreen extends JPanel {
  @Override
  protected void paintComponent(Graphics g) {
    Character player = ScreenController.player;
    int mazeSquares = ScreenController.mazeSquares;
    int screenSize = ScreenController.screenSize;
    ArrayList<Rectangle> mazeRects = ScreenController.mazeRects;

    super.paintComponent(g); // Clear the canvas
    Graphics2D g2d = (Graphics2D) g;
    Rectangle playerRect = player.getRect();

    g2d.setColor(Color.GRAY);
    for (int i = 1; i <= mazeSquares; i++) {
      g2d.drawLine(i * (screenSize / mazeSquares), 0, i * (screenSize / mazeSquares), screenSize);
      g2d.drawLine(0, i * (screenSize / mazeSquares), screenSize, i * (screenSize / mazeSquares));
    }

    g2d.setColor(Color.BLACK);
    for (Rectangle rect : mazeRects) {
      g2d.fill(rect);
    }

    g2d.setColor(Color.RED);
    g2d.fillOval(playerRect.x, playerRect.y, playerRect.width, playerRect.height);
    g2d.drawLine((int) playerRect.getCenterX(), (int) playerRect.getCenterY(),
        (int) (playerRect.getCenterX() + 50 * Math.cos(player.getTurnDir())),
        (int) (playerRect.getCenterY() - 50 * Math.sin(player.getTurnDir())));

    g2d.setColor(Color.GREEN);
    ArrayList<Integer[]> rayCollisions = player.getRayCollisions();
    for (Integer[] point : rayCollisions) {
      g2d.drawLine((int) (playerRect.getCenterX()), (int) (playerRect.getCenterY()),
          point[0],
          point[1]);
    }
    g2d.setColor(Color.BLUE);
    ArrayList<Integer> checkedPoints = player.getCheckedPoints();
    for (int i = 0; i < checkedPoints.size(); i += 2) {
      g2d.fillOval(checkedPoints.get(i), checkedPoints.get(i + 1), 5, 5);
    }
  }

  public static void initKeys(JFrame frame, ArrayList<Integer> keysPressed) {
    frame.addKeyListener(new KeyAdapter() {
      // Key Pressed method
      public void keyPressed(KeyEvent e) {
        keysPressed.add(e.getKeyCode());
      }

      public void keyReleased(KeyEvent e) {
        while (keysPressed.remove((Object) e.getKeyCode())) {
        }
      }
    });
  }

}
