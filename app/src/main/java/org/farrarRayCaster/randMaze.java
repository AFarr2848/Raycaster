package org.farrarRayCaster;

import java.util.ArrayList;
import java.awt.Rectangle;

class randMaze {

  public static ArrayList<Rectangle> makeMaze(int screenWidth, int screenHeight, int mazeSize) {
    int mx = screenWidth / mazeSize;
    int my = screenHeight / mazeSize;
    int randomChoice;
    ArrayList<Rectangle> outArray = new ArrayList<Rectangle>();
    ArrayList<Integer> runSet = new ArrayList<Integer>();

    for (int i = 0; i < mazeSize; i++) {
      for (int j = 0; j < mazeSize; j++) {
        runSet.add(j);
        if (i > 0 && (j + 1 == mazeSize || (int) (Math.random() * 3) == 0)) {
          randomChoice = runSet.get((int) (Math.random() * runSet.size()));
          outArray.add(new Rectangle((mx * j) + mx - 2, my * i, 4, mx));
          outArray.add(new Rectangle(mx * runSet.get(0), (my * i) - 2, mx * (randomChoice - runSet.get(0)), 4));
          outArray.add(new Rectangle((mx * randomChoice) + mx, (my * i) - 2,
              (runSet.get(runSet.size() - 1) * my) - (mx * randomChoice), 4));

          runSet.clear();
        }
      }
    }
    return outArray;
  }
}
