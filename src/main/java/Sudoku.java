import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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

  private Map<Point, Set<Integer>> possibleValues = new ConcurrentHashMap<>();

  private int DIMENSION;
  private int FACTOR;

  private final int MAX_NUMBER_OF_ATEMPTS = 5000;

  int[][] sudokuToSolve;

  public static Sudoku sudoku;

  //--------------------------------------------------------------------------------------------------------------------------------------- endregion Variables
  //------------------------------------------------------------------------------------------------------------------------------------- region Initialization

  @NotNull
  public static int[][] generateSudoku(int dimension, Level level) {
    sudoku = new Sudoku(dimension);
    return sudoku.generateSudokuToSolve(level);
  }

  public static void solveSudoku(int[][] sudokuToSolve) {
    sudoku.solveSudokuTrivialArray(sudokuToSolve);
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

  @NotNull
  private int[][] generateSudokuToSolve(Level level) {

    switch(level) {
      case EASY:
        return getEasyExample(sudokuToSolve);
//        return getEasyExampleSecond(sudokuToSolve);
      case MIDDLE:
      case HARD:
      default:
        return sudokuToSolve;
    }
  }

  //---------------------------------------------------------------------------------------------------------------------------------- endregion Initialization
  //-------------------------------------------------------------------------------------------------------------------------------------------- region Methods

  private void solveSudokuTrivialArray(int[][] sudokuToSolve) {

    // delete all exiting values in the map possibleValues
    deleteAllInitiallyKnownValues(sudokuToSolve);    // TODO: 04/02/19 works

    long start   = System.currentTimeMillis();
    int  counter = 0;

    while(possibleValues.size() > 0 && counter < MAX_NUMBER_OF_ATEMPTS){
      setKnownValues(sudokuToSolve);
      counter ++;
    }

    if (counter >= MAX_NUMBER_OF_ATEMPTS) {
      System.out.println("Sorry - could not solve Sudoku");
      printPossibleValues(possibleValues);
    }
    else {
      long timespent = (System.currentTimeMillis() - start);
      System.out.println("Spent " +  timespent + " milliseconds to solve");

      System.out.println("My Solution");
    }
    Main.printSudoku(this.sudokuToSolve);
  }

  private void deleteAllInitiallyKnownValues(@NotNull int[][] sudokuToSolve) {
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
    deleteValueInColumn((int) coordinate.getX(), value);
    deleteValueInRow(   (int) coordinate.getY(), value);
    deleteValueInSquare(coordinate, value);
  }

  /**
   * This method is only called, iif the algorithm iterates over the map {@param possibleValues} rather than the array {@code sudokuToSolve}.
   *
   * @param possibleValues a copy of the map that is iterated over
   */
  private void setKnownValues(@NotNull Map<Point, Set<Integer>> possibleValues) {
    for (Map.Entry<Point, Set<Integer>> possibleValue : possibleValues.entrySet()) {
      Point coordinate = possibleValue.getKey();
      if (possibleValues.get(coordinate).size() == 1) {
        int value = possibleValues.get(coordinate).iterator().next();
        sudokuToSolve[coordinate.x][coordinate.y] = value;
        removeValue(coordinate, value);
        this.possibleValues.remove(possibleValue.getKey());
      }
    }
  }



  private void setKnownValues(@NotNull int[][] sudokuToSolve) {
    for (int y = 0; y < sudokuToSolve.length; y++) {
      for (int x = 0; x < sudokuToSolve[0].length; x++) {
        Point coordinate = new Point(x, y);
        if(possibleValues.containsKey(coordinate) && possibleValues.get(coordinate).size() == 1) {
          int value = possibleValues.get(coordinate).iterator().next();
          sudokuToSolve[x][y] = value;
          removeValue(coordinate, value);
        }
        else {
          // try to find out the fields that contain only two values.   // TODO: 2019-02-16 done
          // get all all values with 2 digits                           // TODO: 2019-02-16 done
          // get duplicates                                             // TODO: 2019-02-16 done
          // determine whether they are in one row || column || square
          // delete the values in the others.

          Map<Point, Set<Integer>> twoDigitFields = possibleValues.entrySet().stream().filter(entry -> entry.getValue().size() == 2)
                                                                                      .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));
          for (Map.Entry<Point, Set<Integer>> twoDigitField : twoDigitFields.entrySet()) {
            //          for (Map.Entry<Point, Set<Integer>> twoDigitField : twoDigitFields.entrySet()) {
            Set<Integer> values = twoDigitField.getValue();
            for (Map.Entry<Point, Set<Integer>> possibleDuplicate : twoDigitFields.entrySet()) {
              if (!twoDigitField.equals(possibleDuplicate) && values.equals(possibleDuplicate.getValue())) {
                int[] twoDigitFieldSquare = identifySquare(twoDigitField.getKey());
                // determine whether they are in one column
                if (twoDigitField.getKey().getX() == possibleDuplicate.getKey().getX()) {
                  System.out.println("Found in one column.");
                  System.out.println((int) twoDigitField.getKey().getX() + ", " + (int) twoDigitField.getKey().getY() + " : " + (int) possibleDuplicate.getKey().getX() + ", " + (int) possibleDuplicate.getKey().getY());
//                  int[] values = twoDigitField.getValue();
                  deleteValuesInColumn((int) twoDigitField.getKey().getX(), twoDigitField.getValue(), (int) twoDigitField.getKey().getY(), (int) possibleDuplicate.getKey().getY());
                  twoDigitFields.remove(twoDigitField.getKey());
                  twoDigitFields.remove(possibleDuplicate.getKey());
                }
              // determine whether they are in one row
                else if (!twoDigitField.equals(possibleDuplicate) && twoDigitField.getKey().getY() == possibleDuplicate.getKey().getY()) {
                  System.out.println("Found in one row.");
                  System.out.println((int) twoDigitField.getKey().getX() + ", " + (int) twoDigitField.getKey().getY() + " : " + (int) possibleDuplicate.getKey().getX() + ", " + (int) possibleDuplicate.getKey().getY());
                  deleteValuesInRow((int) twoDigitField.getKey().getY(), twoDigitField.getValue(), (int) twoDigitField.getKey().getX(), (int) possibleDuplicate.getKey().getX());
                  twoDigitFields.remove(twoDigitField.getKey());
                  twoDigitFields.remove(possibleDuplicate.getKey());
                }
              // determine whether they are in one square
                else if (Arrays.equals(twoDigitFieldSquare, identifySquare(possibleDuplicate.getKey()))) {
                  System.out.println("Found in a square");
                  System.out.println((int) twoDigitField.getKey().getX() + ", " + (int) twoDigitField.getKey().getY() + " : " + (int) possibleDuplicate.getKey().getX() + ", " + (int) possibleDuplicate.getKey().getY());
                  deleteValuesInSquare(twoDigitFieldSquare, twoDigitField.getValue(), twoDigitField.getKey(), possibleDuplicate.getKey());
                  twoDigitFields.remove(twoDigitField.getKey());
                  twoDigitFields.remove(possibleDuplicate.getKey());
                }

//                System.out.println("Duplicate found");
//                System.out.println(values);
//                System.out.println(possibleDuplicate.getValue());
              }
            }

          }

//          printPossibleValues(twoDigitFields);
        }
      }
    }
  }

  private void deleteValueInColumn(int column, int value) {
    for (int y = 0; y < DIMENSION; y++) {
      possibleValues.computeIfPresent(new Point(column, y), (k, v) -> {possibleValues.get(k).remove(value); return v;});
    }
  }

  private void deleteValuesInColumn(int column, Set<Integer> values, int... exclusion) {
    for (int y = 0; y < DIMENSION; y++) {
      int finalY = y;
      if(Arrays.stream(exclusion).noneMatch(e -> e == finalY)) {
        possibleValues.computeIfPresent(new Point(column, y), (k, v) -> {
          possibleValues.get(k).removeAll(values);
          return v;
        });
      }
    }
  }

  private void deleteValueInRow(int row, int value) {
    for (int x = 0; x < DIMENSION; x++) {
      possibleValues.computeIfPresent(new Point(x, row), (k,v) -> {possibleValues.get(k).remove(value); return v;});
    }
  }

  private void deleteValuesInRow(int row, Set<Integer> values, int... exclusion) {
    for (int x = 0; x < DIMENSION; x++) {
      int finalX = x;
      if(Arrays.stream(exclusion).noneMatch(e -> e == finalX)) {
        possibleValues.computeIfPresent(new Point(x, row), (k, v) -> {
          possibleValues.get(k).removeAll(values);
          return v;
        });
      }
    }
  }

  private void deleteValueInSquare(@NotNull Point coordinate, int value) {
    int[] square = identifySquare(coordinate);
    for (int y = square[1] * FACTOR; y < square[1] * FACTOR + 3; y++) {
      for (int x = square[0] * FACTOR; x < square[0] * FACTOR + 3; x++) {
        possibleValues.computeIfPresent(new Point(x, y), (k, v) -> {possibleValues.get(k).remove(value); return v;});
      }
    }
  }

  private void deleteValuesInSquare(int[] square, Set<Integer> values, Point... exclusions) {
    for (int y = square[1] * FACTOR; y < square[1] * FACTOR + 3; y++) {
      for (int x = square[0] * FACTOR; x < square[0] * FACTOR + 3; x++) {
        int finalX = x;
        int finalY = y;
        if(Arrays.stream(exclusions).noneMatch(a -> a.equals(new Point(finalX, finalY)))) {
          possibleValues.computeIfPresent(new Point(x, y), (k, v) -> {
            possibleValues.get(k).removeAll(values);
            return v;
          });
        }
      }
    }
  }

  private int[] identifySquare(@NotNull Point coordinate) {
    // identify square
    int[] square = new int[2];
    square[0] = (int) coordinate.getX() / FACTOR;
    square[1] = (int) coordinate.getY() / FACTOR;
    return square;
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

