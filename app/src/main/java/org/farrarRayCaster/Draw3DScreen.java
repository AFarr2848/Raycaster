package org.farrarRayCaster;

import java.awt.*;
import javax.swing.*;
import java.util.*;

class Draw3DScreen extends JPanel {

  public Draw3DScreen() {
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g); // Clear the canvas
    Graphics2D g2d = (Graphics2D) g;

    Character player = ScreenController.player;
    ArrayList<Integer> rayDists = player.getLineDistances();
    ArrayList<Color> collisionColors = player.getCollisionColors();
    int mapSize = ScreenController.screenSize;

    int rayDist;
    // Prevents division by zero
    int numRays = rayDists.size() != 0 ? rayDists.size() : 1;
    int lineThickness = this.getWidth() / numRays + 1;
    int lineH;
    int lineO;

    for (int i = 0; i < numRays; i++) {
      try {
        rayDist = rayDists.get(i);

        lineH = (this.getHeight() * mapSize / 10) / rayDist;
        if (lineH > this.getHeight())
          lineH = this.getHeight();
        lineO = this.getHeight() / 2 - lineH / 2;
        g2d.setColor(collisionColors.get(i));
        g2d.fillRect(i * lineThickness, lineO, lineThickness, lineH);
        g2d.setColor(Color.GRAY);
        g2d.fillRect(i * lineThickness, lineO + lineH, lineThickness, this.getHeight() - lineO - lineH);

      } catch (ArithmeticException divZero) {
        System.err.println("Tried to divide by zero");
      } catch (IndexOutOfBoundsException indexOOB) {
        System.err.println("OOB");
      }

    }

  }
}
