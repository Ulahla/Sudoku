
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * User: SarahB
 * Date: 17.01.2019
 * Time: 11:16
 * <p>
 *
 * Copyright LucaNet AG
 */
public class Main {
  //------------------------------------------------------------------------------------------------------------------------------------------ region Variables

  private static final int DIMENSION = 9;

  private static final int[] values = IntStream.range(1, DIMENSION + 1).toArray();

  private static int[][] solution = new int[DIMENSION][DIMENSION];

  //--------------------------------------------------------------------------------------------------------------------------------------- endregion Variables
  //------------------------------------------------------------------------------------------------------------------------------------- region Initialization

  public static void main(String[] args) {
//    Point2D t = new Point2D.Double(1, 0);
    Map<Point, int[]> possibleValues = new HashMap<>();

    for (int x = 0; x < DIMENSION; x++) {
      for (int y = 0; y < DIMENSION; y++) {
        possibleValues.put(new Point(x,y), values.clone());
      }
    }

    int[] test = {42};
    possibleValues.replace(new Point(0,0), new int[]{42});


    int[] t = possibleValues.get(new Point(0, 1));
    t[0] = 30;

//    Arrays.stream(possibleValues.get(new Point(8, 8))).forEach(System.out::print);

    // print the map
//    for (Map.Entry<Point, int[]> entry : possibleValues.entrySet()) {
//      Arrays.stream(entry.getValue()).forEach(System.out::print);
//      System.out.println(Arrays.stream(entry.getValue()).count());
//    }

//    System.out.println(new Point(1,1).getX());


//    System.out.println("Size: " + possibleValues.size());
//    System.out.println("Keys: " + possibleValues.keySet().size());
//    System.out.println("ValueSize: " + possibleValues.entrySet().stream().filter(key -> key.getValue() != null).count());//.forEach(key -> {if (possibleValues.get(key) != null){counter++;}}));//.size());

//    printSolution(solution);


    printSolution(Sudoku.generateSudoko(DIMENSION, Sudoku.Level.EASY));
    //printSolution(Sudoku.getEasySolution());
  }

  //---------------------------------------------------------------------------------------------------------------------------------- endregion Initialization  
  //-------------------------------------------------------------------------------------------------------------------------------------------- region Methods

  //------------------------------------------------------------------------------------------------------------------------------------ region Private Methods

  private static void printSolution(int[][] solution) {
    System.out.println(" -----------------------");
    for (int x = 0; x < DIMENSION; x++) {
      System.out.print("| ");
      for (int y = 0; y < DIMENSION; y++) {
        System.out.print(solution[x][y] + " ");
        if ((y+1) % 3 == 0) {
          System.out.print("| ");
        }
      }
      System.out.println();
    if ((x+1) % 3 == 0) {
      System.out.println(" -----------------------");
    }
    }
  }

  //--------------------------------------------------------------------------------------------------------------------------------- endregion Private Methods
  //----------------------------------------------------------------------------------------------------------------------------------------- endregion Methods  
  //---------------------------------------------------------------------------------------------------------------------------------------region Inner Classes

  //----------------------------------------------------------------------------------------------------------------------------------- endregion Inner Classes

  // =================================================================== End of Class ====================================================================== //
}

