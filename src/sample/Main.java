package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

import javax.naming.spi.DirectoryManager;
import java.io.*;
import java.util.ArrayList;

public class Main extends Application {

    int currentRowCount = 12;
    int currentColCount = 12;
    Boolean swappedRowAndCol = false;

    GridPane userInputGrid = new GridPane();
    Label CrossSum = new Label("Cross Sum");
    TextField rowNumber = new TextField("10");
    Label rowLabel = new Label("Row");
    TextField colNumber = new TextField("10");
    Label colLabel = new Label("Col");
    TextField delay = new TextField("100");
    Label delayLabel = new Label("Delay");
    Button setGridSize = new Button("Set Grid Size");
    Button solvePuzzle = new Button("Solve Puzzle");
    Button clearPuzzle = new Button("Clear Puzzle");
    TextField savePuzzleFileName = new TextField("Puzzle_Save_Name");
    Button savePuzzle = new Button("Save Puzzle");
    TextField loadPuzzleFileName = new TextField("Puzzle_Save_Name");
    Button loadPuzzle = new Button("Load Puzzle");

    Label info = new Label(	"Across Clue:\tA# (Yellow Square)\n" +
            "Below Clue:\tB# (Yellow Square)\n" +
            "Combo Clue:\tB#A# or A#B# (Yellow Square)\n" +
            "Part of puzzle:\tBlank space (no text) (White Square)\n" +
                    "Excluded:\t\tX (Black Square)\n" +
            "Note: Row and Col might be swapped for setting grid size\n" +
            "Note Puzzles will be saved and loaded from:\n" +
            System.getProperty("user.dir") + "\n"
    );
    Label status = new Label("\nNo Puzzle Solved");

    BorderPane panal1 = new BorderPane();
    GridPane board = new GridPane();
    Square[][] boardList = new Square[12][12];

    /*
     * main method that sets up the gui
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        // Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Cross Sum Solver");

        rowNumber.setPrefWidth(40);
        colNumber.setPrefWidth(40);
        delay.setPrefWidth(60);
        CrossSum.setFont(new Font("Arial", 70));

        // adding things to our user input / info grid
        userInputGrid.add(CrossSum, 0,0);
        userInputGrid.add(colLabel,1,1);
        userInputGrid.add(rowLabel,2,1);
        userInputGrid.add(delayLabel,3,1);
        userInputGrid.add(rowNumber, 1, 2);
        userInputGrid.add(colNumber, 2, 2);
        userInputGrid.add(delay, 3, 2);
        userInputGrid.add(setGridSize, 4, 2);
        userInputGrid.add(solvePuzzle, 5, 2);
        userInputGrid.add(clearPuzzle, 6, 2);
        userInputGrid.add(savePuzzleFileName,0,3);
        userInputGrid.add(savePuzzle,1,3);
        userInputGrid.add(loadPuzzleFileName, 0, 4);
        userInputGrid.add(loadPuzzle,1,4);
        userInputGrid.add(info, 0, 5);
        userInputGrid.add(status, 0, 6);

        // allows the grid to be resized
        setGridSize.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            public void handle(MouseEvent event)
            {
                try {
                    currentRowCount = (Integer.parseInt(rowNumber.getText()) + 2);
                    currentColCount = (Integer.parseInt(colNumber.getText()) + 2);

                    // remove everything from the board and reset it
                    board.getChildren().remove(0, board.getChildren().size());
                    boardList = new Square[currentRowCount][currentColCount];
                    AddtoBoard();
                }
                catch (NumberFormatException e){}
            }
        });

        // solve the puzzle
        solvePuzzle.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            public void handle(MouseEvent event) {

                new Thread(() -> {
                    SolveMethod();
                }).start();

            }
        });

        // clear the puzzle squares
        clearPuzzle.setOnMouseClicked(new EventHandler<MouseEvent>(){
            public void handle(MouseEvent event) {
                ClearPuzzleSquares();
            }
        });

        // save the puzzle
        savePuzzle.setOnMouseClicked(new EventHandler<MouseEvent>(){
            public void handle(MouseEvent event) {
                SavePuzzle();
            }
        });

        // load the puzzle
        loadPuzzle.setOnMouseClicked(new EventHandler<MouseEvent>(){
            public void handle(MouseEvent event) {
                LoadPuzzle();
            }
        });

        AddtoBoard();

        // check to see if rows / cols have been swapped
        // encountered a bug where board ran:
        // top to bottom left to right
        // on my laptop boardList[0][1] references row 1 col 0
        // instead of:
        // left to right, top to bottom
        // if on my main pc boardList[0][1] references row 0 col 1
        ((TextField)board.getChildren().get(1)).setText("XA");

        // the board runs top to bottom, left to right (use tab to see this)
        if (boardList[0][1].textField.getText().contains("XA"))
        {
            swappedRowAndCol = true;
            ((TextField)board.getChildren().get(1)).setText("X");
            (board.getChildren().get(1)).setStyle("-fx-background-color: black; -fx-text-inner-color: black; -fx-border-color: black;");
        }

        // the board runs left to right, top to bottom (use tab to see this)
        else
        {
            ((TextField)board.getChildren().get(1)).setText("X");
            (board.getChildren().get(1)).setStyle("-fx-background-color: black; -fx-text-inner-color: black; -fx-border-color: black;");
        }

        // setup last part of the gui
        panal1.setTop(userInputGrid);
        panal1.setCenter(board);
        Scene scene1 = new Scene(panal1);
        primaryStage.setScene(scene1);
        primaryStage.show();
    }

    /*
     * Simple method used to populate the board with blank values
     */
    public void AddtoBoard(){
        for (int x = 0; x < currentRowCount; x++)
        {
            for (int y = 0; y < currentColCount; y++)
            {
                TextField newTexField = new TextField();
                newTexField.setAlignment(Pos.CENTER);
                newTexField.setPrefWidth(75);

                // listen to changes in the text boxes to style them differently :)
                newTexField.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue.contains("X")){
                        newTexField.setStyle("-fx-background-color: black; -fx-text-inner-color: white; -fx-border-color: white;");
                    }
                    else if(newValue.contains("A") || newValue.contains("B")){
                        newTexField.setStyle("-fx-background-color: yellow; -fx-text-inner-color: black; -fx-border-color: black;");
                    }
                    else{
                        newTexField.setStyle("-fx-background-color: white; -fx-text-inner-color: black; -fx-border-color: black;");
                    }
                });

                // default value is X
                newTexField.textProperty().setValue("X");

                // set boarder for the puzzle
                if (x == 0 || x == currentRowCount-1 || y == 0 || y == currentColCount-1){
                    newTexField.setStyle("-fx-background-color: black; -fx-text-inner-color: black; -fx-border-color: black");
                    newTexField.editableProperty().setValue(false);
                }

                board.add(newTexField, x, y);
                boardList[x][y] = new Square(false, false, true, false, "X", 0,0,0);
                boardList[x][y].SetTextField(newTexField);
            }
        }

        // how to make it update !!!
        // in real time instead of all at once at the end,
        // start it in a new thread !! -> might need Platform.runlater
        /*
        new Thread(()-> {
            if(x != 0) {
                //new Thread(() -> {
                for (int i = 0; i < board.getChildren().size(); i++) {
                    System.out.println(i);
                    final int x = i;
                        ((TextField) board.getChildren().get(x)).clear();
                        ((TextField) board.getChildren().get(x)).setText("A");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //}).start();
            }
            //x++;
        }).start();
        */
    }

    /*
     * Simple method that is called when the user wants to try to solve the puzzle
     */
    private void SolveMethod(){
        int x = 0;
        int y = 0;
        for (int i = 0; i < board.getChildren().size(); i++) {
            Node currentSquare = board.getChildren().get(i);
            boardList[x][y] = new Square(false, false, false, false, "", 0,0,0);

            // Blank square
            if(((TextField)currentSquare).textProperty().getValue().contains("X"))
            {
                boardList[x][y].isBlank = true;
                boardList[x][y].stringValue = "X";
            }

            // below clue square
            if(((TextField)currentSquare).textProperty().getValue().contains("B")){
                boardList[x][y].isBelowClue = true;
                boardList[x][y].stringValue = ((TextField)currentSquare).textProperty().getValue();

                // multi clue square given
                if (boardList[x][y].stringValue.contains("A") && boardList[x][y].stringValue.contains("B")) {

                    // above clue given 1st
                    if (boardList[x][y].stringValue.charAt(0) == 'A') {
                        boardList[x][y].belowClueValue = Integer.parseInt(boardList[x][y].stringValue.split("B")[1]);
                    }

                    // below clue given 1st
                    else if (boardList[x][y].stringValue.charAt(0) == 'B') {
                        boardList[x][y].belowClueValue = Integer.parseInt(boardList[x][y].stringValue.split("B")[1].split("A")[0]);
                    }
                }

                // just one below clue given
                else{
                    boardList[x][y].belowClueValue = Integer.parseInt(boardList[x][y].stringValue.split("B")[1]);
                }
            }

            // across clue square
            if(((TextField)currentSquare).textProperty().getValue().contains("A"))
            {
                boardList[x][y].isAcrossClue = true;
                boardList[x][y].stringValue = ((TextField)currentSquare).textProperty().getValue();

                // multi clue square given
                if (boardList[x][y].stringValue.contains("A") && boardList[x][y].stringValue.contains("B")) {

                    // across clue given 1st
                    if (boardList[x][y].stringValue.charAt(0) == 'A') {
                        boardList[x][y].acrossClueValue = Integer.parseInt(boardList[x][y].stringValue.split("A")[1].split("B")[0]);
                    }

                    // below clue given 1st
                    else if (boardList[x][y].stringValue.charAt(0) == 'B' ){
                        boardList[x][y].acrossClueValue = Integer.parseInt(boardList[x][y].stringValue.split("A")[1]);
                    }
                }

                // just one across clue given
                else{
                    boardList[x][y].acrossClueValue = Integer.parseInt(boardList[x][y].stringValue.split("A")[1]);
                }

            }

            // part of the puzzle
            if(((TextField)currentSquare).textProperty().getValue().equals(""))
            {
                boardList[x][y].isPuzzle = true;
                boardList[x][y].stringValue = ((TextField)currentSquare).textProperty().getValue();
                boardList[x][y].intValue = 0;
            }

            boardList[x][y].SetTextField((TextField)currentSquare);

            y++;
            if (y == currentColCount)
            {
                y = 0;
                x++;
            }
        }

        // actually solve the puzzle
        Controller control = new Controller();
        control.Solve(boardList, currentRowCount, currentColCount, swappedRowAndCol, Integer.parseInt(delay.getText()));

        // give status of the puzzle
        if(control.solved){

            Platform.runLater(()->{
                status.textProperty().setValue("\nPuzzle solved!");
            });

        }
        else
        {
            Platform.runLater(()->{
                status.textProperty().setValue("\nPuzzle not solved :(");
            });
        }
    }

    /*
     * Simple method to clear the puzzle squares (not clue and blank parts)
     */
    public void ClearPuzzleSquares(){
        for (int i = 0; i < board.getChildren().size(); i++) {
            Node currentSquare = board.getChildren().get(i);

            // if its not a clue or blank square we can clear it
            if(!(((TextField)currentSquare).textProperty().getValue().contains("X") ||
                    ((TextField)currentSquare).textProperty().getValue().contains("A") ||
                    ((TextField)currentSquare).textProperty().getValue().contains("B"))){

                ((TextField)currentSquare).textProperty().setValue("");
            }
        }
    }

    /*
     * Method to save the puzzle
     */
    public void SavePuzzle(){
        try
        {
            FileOutputStream fos = new FileOutputStream(savePuzzleFileName.getText());
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            ArrayList<String> output = new ArrayList<>();

            // add row and col so we can set it again when we load the board
            output.add(rowNumber.getText());
            output.add(colNumber.getText());

            // iterate over board and save it line by line
            for(int i = 0; i < board.getChildren().size(); i++){
                output.add( ((TextField)board.getChildren().get(i)).getText());
            }

            // write out the data and close stuff
            oos.writeObject(output);
            oos.close();
            fos.close();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    public void LoadPuzzle(){
        try
        {
            FileInputStream fis = new FileInputStream(loadPuzzleFileName.getText());
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<String> input = (ArrayList<String>) ois.readObject();
            ois.close();
            fis.close();

            // set row and col
            rowNumber.setText(input.get(0));
            colNumber.setText(input.get(1));

            currentRowCount = (Integer.parseInt(rowNumber.getText()) + 2);
            currentColCount = (Integer.parseInt(colNumber.getText()) + 2);

            // remove everything from the board and reset it
            board.getChildren().remove(0, board.getChildren().size());
            boardList = new Square[currentRowCount][currentColCount];
            AddtoBoard();

            // we start at 2 to skip row and col data
            for (int i = 2; i < input.size()-2; i++){
                // off by 2 now in our count
                ((TextField)board.getChildren().get(i-2)).setText(input.get(i));
            }
        }
        catch (IOException ioe) {
            status.setText("\nError Loading File: could not find the file");
        } catch (ClassNotFoundException e) {
            status.setText("\nError Loading File: the file is corrupt");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
