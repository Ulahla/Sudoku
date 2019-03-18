import org.intellij.lang.annotations.RegExp;

/**
 * User: SarahB
 * Date: 2019-02-20
 * Time: 17:27
 * <p>
 * Copyright: Fari
 */

public class SudokuParser {

  private static final String validCharacters = "[^\\d\\.]";

  public static int[][] generateFromString(String input) {
    if (input.length() != 81) {
      throw new IllegalArgumentException("Sorry, you have to insert 81 digits");
    }
//    input = input.replace(" ","");
    input = input.replaceAll(validCharacters, "");
    int dimension  = (int) Math.sqrt(input.length());
    int index      = 0;
    int[][] sudoku = new int[dimension][dimension];

//    System.out.println(input.toCharArray());

    for (int y = 0; y < dimension; y++) {
      for (int x = 0; x < dimension; x++) {
        char c = input.charAt(index);
//        System.out.println(c);
//        int i = c == '.' ? 0 : Character.getNumericValue(c);
//        System.out.println(i);
        sudoku[x][y] = c == '.' ? 0 : Character.getNumericValue(c);
//        char = input.substring(index, index+1).equals(".") ? 0 :
//        int value =
//        sudoku[x][y] = input.substring(index, index+1).equals(".") ? 0 : Integer.valueOf(input.substring(index, index+1));
        index ++;
      }
    }
    return sudoku;
  }

}