import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * User: SarahB
 * Date: 17.01.2019
 * Time: 18:56
 * <p>
 * Copyright Fari
 */
@SuppressWarnings("SpellCheckingInspection")
public class Sudoku {
  //------------------------------------------------------------------------------------------------------------------------------------------ region Variables

  private Map<Point, Set<Integer>> possibleValues = new LinkedHashMap<>();

  private int DIMENSION;
  private int FACTOR;

  int[][] sudokuToSolve;


  //--------------------------------------------------------------------------------------------------------------------------------------- endregion Variables
  //------------------------------------------------------------------------------------------------------------------------------------- region Initialization

  public static int[][] generateSudoko(int dimension, Level level) {
    Sudoku sudoku = new Sudoku(dimension);

    return sudoku.generateSudokuToSolve(level);
  }

  private Sudoku(int dimension) {
    this.DIMENSION     = dimension;
    this.FACTOR        = (int) Math.sqrt(DIMENSION);
    this.sudokuToSolve = new int[DIMENSION][DIMENSION];

    // fill possible values
    for (int x = 0; x < DIMENSION; x++) {
      for (int y = 0; y < DIMENSION; y++) {
        Point point = new Point(x, y);
        possibleValues.put(point, IntStream.range(1, DIMENSION + 1).boxed().collect(Collectors.toSet()));
      }
    }
  }

  private int[][] generateSudokuToSolve(Level level) {

    switch(level) {
      case EASY:
      case MIDDLE:
      case HARD:
      default:
        System.out.println("Sudoku to solve:");
        this.sudokuToSolve = getEasyExample(sudokuToSolve);
        Main.printSudoku(sudokuToSolve);
//        this.sudokuToSolve = getEasyExampleSecond(sudokuToSolve);
        solveSudoku(this.sudokuToSolve);
        //checkForValueInColumn(new Point(1,5), 1);
//        System.out.println(possibleValues.entrySet());
//        System.out.println(possibleValues.size());
        return sudokuToSolve;
    }
  }

  //---------------------------------------------------------------------------------------------------------------------------------- endregion Initialization
  //-------------------------------------------------------------------------------------------------------------------------------------------- region Methods

  private void solveSudoku(int[][] sudokuToSolve) {

    // delete all exiting numbers in possibleValues
    deleteAllInitiallyKnownValues(sudokuToSolve);    // TODO: 04/02/19 works

    int counter = 0;
    while(possibleValues.size() > 0){
      setKnownValues(sudokuToSolve);
      // print possible Values
//      printPossibleValues(possibleValues);
      counter++;
//      printPossibleValues(possibleValues);
//      deleteAllInitiallyKnownValues(sudokuToSolve);
    }
    // print possibleValues
//    printPossibleValues(possibleValues);
    System.out.println("It took " + counter + " iterations, to solve this sudoku.");
    System.out.println("My Solution");
//    Main.printSudoku(sudokuToSolve);
//    System.out.println("Actual Solution");
//    getEasySolution();

  }

  private void checkForValueInColumn(Point coordinate, int value) {
    int column = (int) coordinate.getY();
    for (int y = 0; y < DIMENSION; y++) {
      if (sudokuToSolve[y][column] == value) {
        System.out.println("Found value + " + value + "in column: " + column);
        deleteValueInColumn(coordinate, value);
      }
      //possibleValues.re(new Point(x, column))
    }
  }

  private void deleteAllInitiallyKnownValues(int[][] sudokuToSolve) {
    // get all keys, for that the value is not 0!
    // delete them in the map.
    for (int y = 0; y < sudokuToSolve.length; y++) {
      for (int x = 0; x < sudokuToSolve[0].length; x++) {
        int value = sudokuToSolve[x][y];
        Point coordinate = new Point(x , y);
        if (value != 0 && possibleValues.containsKey(coordinate)) {
          removeValue(coordinate, value);
        }
      }
    }
    // print possible values
//    printPossibleValues(possibleValues);
  }

  private void removeValue(Point coordinate, int value) {
    possibleValues.remove(coordinate);
    deleteValueInColumn(coordinate, value);
    deleteValueInRow(coordinate, value);
    deleteValueInSquare(coordinate, value);
  }

  private void setKnownValues(int[][] sudokuToSolve) {
    for (int y = 0; y < sudokuToSolve.length; y++) {
      for (int x = 0; x < sudokuToSolve[0].length; x++) {
        Point coordinate = new Point(x, y);
        if(possibleValues.containsKey(coordinate) && possibleValues.get(coordinate).size() == 1) {
          int value = possibleValues.get(coordinate).iterator().next();
          sudokuToSolve[x][y] = value;
          removeValue(coordinate, value);
        }
      }
    }
  }

  private void deleteValueInColumn(Point coordinate , int value) {
    int column = (int) coordinate.getY();
    for (int x = 0; x < DIMENSION; x++) {
      possibleValues.computeIfPresent(new Point(x, column), (k, v) -> {possibleValues.get(k).remove(value); return v;});
    }
  }

  private void deleteValueInRow(Point coordinate, int value) {
    int row = (int) coordinate.getX();
    for (int y = 0; y < DIMENSION; y++) {
      possibleValues.computeIfPresent(new Point(row, y), (k,v) -> {possibleValues.get(k).remove(value); return v;});
    }
  }

  private void deleteValueInSquare(Point coordinate, int value) {
  // identify square
    int squareX = (int) coordinate.getX() / FACTOR;
    int squareY = (int) coordinate.getY() / FACTOR;
    for (int y = squareY * FACTOR; y < squareY * FACTOR + 3; y++) {
      for (int x = squareX * FACTOR; x < squareX * FACTOR + 3; x++) {
        possibleValues.computeIfPresent(new Point(x, y), (k, v) -> {possibleValues.get(k).remove(value); return v;});
      }
    }
  }

  private void printPossibleValues(Map<Point, Set<Integer>> possibleValues) {
    // print the map
    System.out.println("All possible values");
    for (Map.Entry<Point, Set<Integer>> entry : possibleValues.entrySet()) {
      System.out.println((int) entry.getKey().getX() + "," + (int) entry.getKey().getY() + ": " + entry.getValue());
//      Arrays.stream(entry.getValue().toArray()).forEach(System.out::print);
      //System.out.println(Arrays.stream(entry.getValue()).count());
    }
  }

  //------------------------------------------------------------------------------------------------------------------------------------ region Private Methods
  private static int[][] getEasyExampleSecond(int[][] sudoku) {
    sudoku[0][7] = 6;

    sudoku[1][1] = 5;
    sudoku[1][2] = 6;
//    sudoku[1][3] = 4;
    sudoku[1][5] = 2;
    sudoku[1][8] = 8;

    sudoku[2][1] = 3;
    sudoku[2][2] = 1;
//    sudoku[2][3] = 8;
    sudoku[2][5] = 7;

    sudoku[3][0] = 5;
    sudoku[3][2] = 7;
    sudoku[3][4] = 9;
    sudoku[3][6] = 2;
    sudoku[3][7] = 1;

    sudoku[4][5] = 3;

    sudoku[5][3] = 7;
//    sudoku[5][6] = 6;
    sudoku[5][7] = 9;

    sudoku[6][5] = 4;
    sudoku[6][6] = 7;
    sudoku[6][7] = 2;

    sudoku[7][0] = 7;
//    sudoku[7][1] = 8;
//    sudoku[7][6] = 1;
    sudoku[7][7] = 3;

    sudoku[8][1] = 1;
    sudoku[8][5] = 5;

    return sudoku;
  }




  private static int[][] getEasyExample(int[][] sudoku) {
    sudoku[0][7] = 6;

    sudoku[1][1] = 5;
    sudoku[1][2] = 6;
    sudoku[1][3] = 4;
    sudoku[1][5] = 2;
    sudoku[1][8] = 8;

    sudoku[2][1] = 3;
    sudoku[2][2] = 1;
    sudoku[2][3] = 8;
    sudoku[2][5] = 7;

    sudoku[3][0] = 5;
    sudoku[3][2] = 7;
    sudoku[3][4] = 9;
    sudoku[3][6] = 2;
    sudoku[3][7] = 1;

    sudoku[4][5] = 3;

    sudoku[5][3] = 7;
    sudoku[5][6] = 6;
    sudoku[5][7] = 9;

    sudoku[6][5] = 4;
    sudoku[6][6] = 7;
    sudoku[6][7] = 2;

    sudoku[7][0] = 7;
    sudoku[7][1] = 8;
    sudoku[7][6] = 1;
    sudoku[7][7] = 3;

    sudoku[8][1] = 1;
    sudoku[8][5] = 5;

    return sudoku;
  }

  static int[][] getEasySolution() {
    int[][] solution = new int[9][9];

    getEasyExample(solution);

    solution[0][0] = 2;
    solution[0][1] = 7;
    solution[0][2] = 8;
    solution[0][3] = 5;
    solution[0][4] = 3;
    solution[0][5] = 9;
    solution[0][6] = 4;
    solution[0][8] = 1;

    solution[1][0] = 9;
    solution[1][4] = 1;
    solution[1][6] = 3;
    solution[1][7] = 7;

    solution[2][0] = 4;
    solution[2][4] = 6;
    solution[2][6] = 9;
    solution[2][7] = 5;
    solution[2][8] = 2;

    solution[3][1] = 4;
    solution[3][3] = 6;
    solution[3][5] = 8;
    solution[3][8] = 3;

    solution[4][0] = 1;
    solution[4][1] = 6;
    solution[4][2] = 9;
    solution[4][3] = 2;
    solution[4][4] = 4;
    solution[4][6] = 5;
    solution[4][7] = 8;
    solution[4][8] = 7;

    solution[5][0] = 8;
    solution[5][1] = 2;
    solution[5][2] = 3;
    solution[5][4] = 5;
    solution[5][5] = 1;
    solution[5][8] = 4;

    solution[6][0] = 3;
    solution[6][1] = 9;
    solution[6][2] = 5;
    solution[6][3] = 1;
    solution[6][4] = 8;
    solution[6][8] = 6;

    solution[7][2] = 4;
    solution[7][3] = 9;
    solution[7][4] = 2;
    solution[7][5] = 6;
    solution[7][8] = 5;

    solution[8][0] = 6;
    solution[8][2] = 2;
    solution[8][3] = 3;
    solution[8][4] = 7;
    solution[8][6] = 8;
    solution[8][7] = 4;
    solution[8][8] = 9;


    return solution;
  }
  //--------------------------------------------------------------------------------------------------------------------------------- endregion Private Methods
  //----------------------------------------------------------------------------------------------------------------------------------------- endregion Methods
  //---------------------------------------------------------------------------------------------------------------------------------------region Inner Classes

  public enum Level {
    EASY,
    MIDDLE,
    HARD,
    EXPERT
  }

  //----------------------------------------------------------------------------------------------------------------------------------- endregion Inner Classes

  // =================================================================== End of Class ====================================================================== //
}

