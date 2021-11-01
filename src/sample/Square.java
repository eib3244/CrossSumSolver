package sample;

import javafx.scene.control.TextField;

/*
 * Simple class to keep track of the board and what is contains
 */
public class Square {

    boolean isAcrossClue;
    boolean isBelowClue;
    boolean isBlank;
    boolean isPuzzle;
    String stringValue;
    int intValue;
    int acrossClueValue;
    int belowClueValue;
    TextField textField;

    /*
     * main constructor
     */
    public Square(boolean isAcrossClue, boolean isBelowClue, boolean isBlank, boolean isPuzzle, String stringValue, int intValue, int acrossClueValue, int belowClueValue){
        this.isAcrossClue = isAcrossClue;
        this.isBelowClue = isBelowClue;
        this.isBlank = isBlank;
        this.isPuzzle = isPuzzle;
        this.stringValue = stringValue;
        this.intValue = intValue;
        this.acrossClueValue = acrossClueValue;
        this.belowClueValue = belowClueValue;
    }

    /*
     * allows us to set the text field
     */
    public void SetTextField(TextField textField){
        this.textField = textField;
    }
}
