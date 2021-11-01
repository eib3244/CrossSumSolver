package sample;

import javafx.application.Platform;

/*
 * main controller that handles solving our puzzle
 */
public class Controller {

    public static boolean solved = false;
    private boolean swappedRowAndCol = false;
    private int delay;

    /*
     * main method that is called to solve the puzzle
     */
    public void Solve(Square[][] puzzleBoard, int maxRow, int maxCol, boolean swappedRowAndCol, int delay){
        solved = false;
        this.swappedRowAndCol = swappedRowAndCol;
        this.delay = delay;
        RecursiveSolve(puzzleBoard, 0, 0, maxRow, maxCol);
    }

    /*
     * recursive solving algo
     */
    private void RecursiveSolve(Square[][] puzzleBoard, int currentRow, int currentCol, int maxRow, int maxCol){
        // if the current value is part of the puzzle the user solves (a blank space)
        if (puzzleBoard[currentRow][currentCol].isPuzzle)
        {
            // set prev values for if we come back
            int prevRow = currentRow;
            int prevCol = currentCol;
            Boolean shouldReturn = false;
            Boolean shouldRecurse;
            int guess = 0;

            // loop over numbers 1-9
            while(true) {
                guess++;

                if (solved) {
                    return;
                }

                // we have tried all nums must return to prev level and try another num there
                if (guess == 10){
                    shouldReturn = true;
                }

                // reset values and return to prev level
                if (shouldReturn){
                    final int x1 = currentRow;
                    final int x2 = currentCol;
                    final int delay = this.delay;

                    // update gui
                    Platform.runLater(()->{
                        puzzleBoard[x1][x2].textField.textProperty().setValue("");
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });

                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // update board model
                    puzzleBoard[currentRow][currentCol].stringValue = "";
                    puzzleBoard[currentRow][currentCol].intValue = 0;
                    return;
                }

                // if guess is duplicate to left or above inc guess
                if (CheckForDupAcross(puzzleBoard, currentRow, currentCol-1, guess) || CheckForDupAbove(puzzleBoard, currentRow-1, currentCol, guess))
                {
                    continue;
                }

                // if the cell to the right and the cell below are not puzzle pieces we must
                // check the validity of the current row & col as we have reached the end of both
                if (!puzzleBoard[currentRow+1][currentCol].isPuzzle && !puzzleBoard[currentRow][currentCol+1].isPuzzle) {

                    // if the current sum's for the row and col equal their clues
                    if (SumCurrentRowCheck(puzzleBoard, currentRow, currentCol - 1, guess, true) && SumCurrentColCheck(puzzleBoard, currentRow - 1, currentCol, guess, true)) {
                        shouldRecurse = true;
                    }
                    // we must try another value
                    else{
                        continue;
                    }
                }

                // if we have hit the end of a sum for a row only
                else if(!puzzleBoard[currentRow][currentCol+1].isPuzzle) {

                    // if the sum of the row is equal to the row's clue and the sum of the col is less than the cols clue
                    if (SumCurrentRowCheck(puzzleBoard, currentRow, currentCol - 1, guess, true) && SumCurrentColCheck(puzzleBoard, currentRow - 1, currentCol, guess, false)) {
                        shouldRecurse = true;
                    }
                    else{
                        continue;
                    }
                }

                // if we have hit the end of a sum for a col
                else if(!puzzleBoard[currentRow+1][currentCol].isPuzzle) {

                    // if the sum of the row is less than to the row's clue and the sum of the col is equal to the cols clue
                    if (SumCurrentRowCheck(puzzleBoard, currentRow, currentCol - 1, guess, false) && SumCurrentColCheck(puzzleBoard, currentRow - 1, currentCol, guess, true)) {
                        shouldRecurse = true;
                    }
                    else{
                        continue;
                    }
                }

                // if the sum of the row is less than to the row's clue and the sum of the col is less than the cols clue
                else if (SumCurrentRowCheck(puzzleBoard, currentRow, currentCol-1, guess, false) && SumCurrentColCheck(puzzleBoard, currentRow-1, currentCol, guess, false)) {
                    shouldRecurse = true;
                }
                else{
                    continue;
                }

                // recurse to next level
                if (shouldRecurse){
                    // we can set the value and recurse to the next level
                    final int x1 = currentRow;
                    final int x2 = currentCol;
                    final int guess1 = guess;

                    // update gui
                    Platform.runLater(()->{
                        puzzleBoard[x1][x2].textField.textProperty().setValue(Integer.toString(guess1));

                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });

                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // update board model
                    puzzleBoard[currentRow][currentCol].stringValue = Integer.toString(guess);
                    puzzleBoard[currentRow][currentCol].intValue = guess;

                    // go to next square
                    currentCol++;
                    if (currentCol == maxCol){
                        currentRow++;
                        currentCol = 0;
                    }

                    // recurse to next level
                    RecursiveSolve(puzzleBoard, currentRow, currentCol, maxRow, maxCol);

                    // if solved we can return!
                    if(solved){
                        return;
                    }

                    // reset values and continue solving
                    currentRow = prevRow;
                    currentCol = prevCol;

                    final int x11 = currentRow;
                    final int x22 = currentCol;

                    // reset gui back to blank
                    Platform.runLater(()->{
                        puzzleBoard[x11][x22].textField.textProperty().setValue("");
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });

                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // reset board model
                    puzzleBoard[currentRow][currentCol].stringValue = "";
                    puzzleBoard[currentRow][currentCol].intValue = 0;

                }
            }
        }

        // recurse to the next level as we are not on a part of the puzzle the user solves
        else
        {
            currentCol++;
            if (currentCol == maxCol){
                currentRow++;

                // we are at the end of the puzzle and can stop here !
                if (currentRow == maxRow){
                    solved = true;
                    return;
                }

                currentCol = 0;
            }

            // recurse to next level
            RecursiveSolve(puzzleBoard, currentRow, currentCol, maxRow, maxCol);

            if (solved)
                return;
        }

        return;
    }

    /*
     * used to check and see if we have a duplicate in the row
     */
    private Boolean CheckForDupAcross(Square[][] puzzleBoard, int currentRow, int currentCol, int currentGuess){
        Boolean dupFould = false;

        while(true){

            // reached the end of the row
            if (currentCol < 0){
                break;
            }

            // we have reached a clue or a blank box (no apart of the puzzle and we can stop)
            if (puzzleBoard[currentRow][currentCol].isPuzzle != true){
                break;
            }

            // we have found a part of the puzzle and we must check to see if it is equal to our current guess
            if (puzzleBoard[currentRow][currentCol].intValue == currentGuess){
                dupFould = true;
                break;
            }
            currentCol--;
        }

        // return if we found a duplicate or not
        return dupFould;
    }

    /*
     * used to check and see if we have a duplicate in the column
     */
    private Boolean CheckForDupAbove(Square[][] puzzleBoard, int currentRow, int currentCol, int currentGuess){
        Boolean dupFould = false;

        while(true){

            // reached the end of the row
            if (currentRow < 0){
                break;
            }

            // we have reached a clue or a blank box (no apart of the puzzle and we can stop)
            if (puzzleBoard[currentRow][currentCol].isPuzzle != true){
                break;
            }

            // we have found a part of the puzzle and we must check to see if it is equal to our current guess
            if (puzzleBoard[currentRow][currentCol].intValue == currentGuess){
                dupFould = true;
                break;
            }
            currentRow--;
        }

        // return if we found a duplicate or not
        return dupFould;
    }

    /*
     * used to check and see if the current row is equal to the current row clue or not
     */
    private boolean SumCurrentRowCheck(Square[][] puzzleBoard, int currentRow, int currentCol, int currentGuess, boolean hasToBeEqual){
        boolean correctValue = false;
        int currentSum = currentGuess;

        while (true)
        {
            // reached the end of the row
            if (currentCol < 0){
                break;
            }

            // found a part of the puzzle and we can add it to our sum
            if(puzzleBoard[currentRow][currentCol].isPuzzle){
                currentSum += puzzleBoard[currentRow][currentCol].intValue;
            }

            // found a clue space we can stop and check if it is less than or equal depending on what we want
            else if(!puzzleBoard[currentRow][currentCol].isBlank){
                Boolean isClue;
                int value;

                // sometimes row and col gets swapped (see Main)
                // this effects if we should check for an across or below clue here
                if (swappedRowAndCol){
                    isClue = puzzleBoard[currentRow][currentCol].isBelowClue;
                    value = puzzleBoard[currentRow][currentCol].belowClueValue;
                }

                else{
                    isClue = puzzleBoard[currentRow][currentCol].isAcrossClue;
                    value = puzzleBoard[currentRow][currentCol].acrossClueValue;
                }

                // found clue, we can check the current sum now to see if it is equal
                if (isClue){

                    // we need the exact value as we are at the end of a row
                    if (hasToBeEqual){
                        if (value == currentSum){
                            correctValue = true;
                            break;
                        }
                    }

                    // just has to be less than our clue as we are not at the end of a row yet
                    else
                    {
                        if (value > currentSum){
                            correctValue = true;
                            break;
                        }
                    }
                }


            }

            currentCol--;
        }
        return correctValue;
    }

    /*
     * used to check and see if the current col is equal to the current col clue or not
     */
    private boolean SumCurrentColCheck(Square[][] puzzleBoard, int currentRow, int currentCol, int currentGuess, boolean hasToBeEqual){

        boolean correctValue = false;
        int currentSum = currentGuess;

        while (true)
        {
            // reached the end of the col
            if (currentRow < 0){
                break;
            }

            // found a part of the puzzle and we can add it to our sum
            if(puzzleBoard[currentRow][currentCol].isPuzzle){
                currentSum += puzzleBoard[currentRow][currentCol].intValue;
            }

            // found a clue space we can stop and check if it is less than or equal depending on what we want
            else if (!puzzleBoard[currentRow][currentCol].isBlank){
                Boolean isClue;
                int value;

                // sometimes row and col gets swapped (see Main)
                // this effects if we should check for an across or below clue here
                if(swappedRowAndCol){
                    isClue = puzzleBoard[currentRow][currentCol].isAcrossClue;
                    value = puzzleBoard[currentRow][currentCol].acrossClueValue;
                }
                else{
                    isClue = puzzleBoard[currentRow][currentCol].isBelowClue;
                    value = puzzleBoard[currentRow][currentCol].belowClueValue;
                }

                // found clue, we can check the current sum now to see if it is equal
                if (isClue){

                    // we need the exact value as we are at the end of a row
                    if (hasToBeEqual){
                        if (value == currentSum){
                            correctValue = true;
                            break;
                        }
                    }

                    // just has to be less than our clue as we are not at the end of a col yet
                    else
                    {
                        if (value > currentSum){
                            correctValue = true;
                            break;
                        }
                    }
                }
            }

            currentRow--;
        }
        return correctValue;
    }
}