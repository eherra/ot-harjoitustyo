
package game2048.ui;

import game2048.dao.DBhighScoreDao;
import game2048.domain.GameLogic;
import game2048.domain.MoveExecutor;
import game2048.utils.Square;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;


public class Ui extends Application {
    private GameLogic logic;
    private MoveExecutor moveExecutor;
    private GridPane gridForSquares, gridToReturn, toReturnPane;
    private StackPane squareStack, gameOverStack, stackToReturn;
    private BorderPane rootSetting, mainTop;
    private VBox mainTopRight;
    private TextField chooseBoardSizeField;
    private Label currentScoreLabel, highScoreLabel, fontLabel, wrongInputErrors;
    private Button topNewGameButton, topMainMenuButton;
    private Rectangle square;
    private Stage currentStage;
    private double sceneHeigth, sceneWidth;
    private int wrongInputCount;
    private DBhighScoreDao highScoreService;
    
   @Override
   public void init() {
       highScoreService = new DBhighScoreDao();
   }
    
    @Override
    public void start(Stage stage) throws Exception {
        currentStage = stage;

        gameOverStack = new StackPane();
        currentStage.setScene(getMainMenuScene());
        currentStage.show();
        
        sceneHeigth = currentStage.getHeight();
        sceneWidth = currentStage.getWidth();
    }
    
    public Scene getMainMenuScene() {     
        gameOverStack.setVisible(false);
        BorderPane rootMenu = new BorderPane();
        rootMenu.setStyle("-fx-background-color:#008080");
        
        // big game namelabel
        Label topLabel = new Label("Game 2048");        
        topLabel.setFont(Font.font("Monospaced", FontWeight.BOLD, 60));
        topLabel.setUnderline(true);
        topLabel.setTextFill(Color.web("#131516"));

        // exit button X
        Button exitButton = new Button("X");
        exitButton.setStyle("-fx-background-color: #CD5C5C; ");
        exitButton.setOnMouseEntered(e -> exitButton.setStyle("-fx-background-color: #F08080"));
        exitButton.setOnMouseExited(e -> exitButton.setStyle("-fx-background-color: #CD5C5C"));
        
        Label playArea = new Label("Board set up");
        playArea.setFont(Font.font("Sans-serif", FontWeight.BOLD, 30));
        playArea.setAlignment(Pos.CENTER);
        playArea.setTextFill(Color.web("#131516"));
        
        // textField wrong inputs casehandeling
        wrongInputErrors = new Label("How about between 3-9?");
        wrongInputErrors.setFont(Font.font("Sans-serif", FontWeight.BOLD, 15));
        wrongInputErrors.setTextFill(Color.web("#FFFF99"));
        wrongInputErrors.setVisible(false);
        
        // main menu buttons
        Button highScoresButton = getHighScoreButton();
        Button playButton = styleMenuButtons("Play!");
        Button quickStart = styleMenuButtons("Quick start");
        Button rulesButton = styleMenuButtons("Rules"); // think a position for this
        chooseBoardSizeField = getBoardSetUpTextField();

        // AI menu
        Label feelingLazy = new Label("\tFeeling lazy? \nLet AI Doge play for you!");
        feelingLazy.setFont(Font.font("Sans-serif", FontWeight.BOLD, 20));
        feelingLazy.setAlignment(Pos.CENTER);
        feelingLazy.setTextFill(Color.web("#131516"));
        
        Image dogeImagePath = null;
        ImageView dogeImage = null;
        
        try {
            dogeImagePath = new Image("images/dogeImage.png", 60, 60, true, true);
            dogeImage = new ImageView(dogeImagePath);        
        } catch (Exception e) {
            System.out.println("Doge photo not found");
        }
        
        Button dogeButton = new Button("Release doge");
        dogeButton.setStyle("-fx-background-color: #e1b303; ");
        dogeButton.setOnMouseEntered(e -> dogeButton.setStyle("-fx-background-color: #cb9800"));
        dogeButton.setOnMouseExited(e -> dogeButton.setStyle("-fx-background-color: #e1b303"));
        dogeButton.setFont(new Font("Monospaced", 15));

        HBox dogeButtonArea = new HBox(dogeButton, dogeImage);
        dogeButtonArea.setSpacing(7);
        dogeButtonArea.setAlignment(Pos.CENTER);
        
        HBox gameStartButtons = new HBox(playButton, quickStart);
        gameStartButtons.setAlignment(Pos.CENTER);
        gameStartButtons.setSpacing(7);
        
        VBox gameSettingArea = new VBox(playArea, chooseBoardSizeField, gameStartButtons, wrongInputErrors);
        gameSettingArea.setSpacing(7);
        gameSettingArea.setAlignment(Pos.CENTER);

        VBox dogeArea = new VBox(feelingLazy, dogeButtonArea);
        dogeArea.setAlignment(Pos.CENTER);
        dogeArea.setSpacing(7);
        
        HBox rootCenterArea = new HBox(gameSettingArea, dogeArea);
        rootCenterArea.setSpacing(150);
        rootCenterArea.setAlignment(Pos.CENTER);
        
        VBox menuTopComponents = new VBox(topLabel, highScoresButton);
        VBox menuCenterComponents = new VBox(menuTopComponents, rootCenterArea);

        menuTopComponents.setAlignment(Pos.CENTER);
        menuTopComponents.setSpacing(10);
        menuCenterComponents.setSpacing(80);
        
        exitButton.setOnMouseClicked(event -> {
            currentStage.close();
        });
        
        wrongInputCount = 0;
        playButton.setOnMouseClicked((event) -> {
            handlePlayButtonEvent();
        });
               
        quickStart.setOnMouseClicked((event) -> {
            currentStage.setScene(getGameScene(4));
            sceneHeigth = currentStage.getHeight();
            sceneWidth = currentStage.getWidth();
        });
        
        
        
        menuCenterComponents.setAlignment(Pos.CENTER);
        rootMenu.setCenter(menuCenterComponents);
        rootMenu.setRight(exitButton);
        
        return new Scene(rootMenu, 700, 516);
    }
    
    public Scene getGameScene(int boardSize) {
        logic = new GameLogic(boardSize, highScoreService);
        moveExecutor = new MoveExecutor(logic);
        gameOverStack = new StackPane();        
        gameOverStack.setVisible(false);
        
        // stack for moving squares
        squareStack = new StackPane();
        squareStack.setStyle("-fx-background-color:#bbada0");

        // root for game view
        rootSetting = new BorderPane();
        rootSetting.setStyle("-fx-background-color:#008080");
        
        // top right new game button
        topNewGameButton = getNewGameButton();
        topNewGameButton.setFocusTraversable(false);

        //back to menu button
        topMainMenuButton = getBackButton("Back");
        topMainMenuButton.setFocusTraversable(false);

        // top labels
        Label leftTopLabel = new Label("Game 2048");
        currentScoreLabel = new Label("Current Score \n" + logic.getGamePoints());
        highScoreLabel = new Label("High Score \n" + logic.getHighScore());
        
        // top labels styling
        leftTopLabel.setUnderline(true);
        leftTopLabel.setPadding(new Insets(20, 20, 20, 20));
        leftTopLabel.setFont(Font.font("Monospaced", FontWeight.BOLD, 35));
        highScoreLabel.setFont(new Font("Sans-Serif", 15));
        highScoreLabel.setTextFill(Color.web("#ffffff"));
        currentScoreLabel.setFont(new Font("Sans-Serif", 15));
        currentScoreLabel.setTextFill(Color.web("#ffffff"));
        
        // top right scoreshow
        HBox scoreShow = new HBox(currentScoreLabel, highScoreLabel);
        scoreShow.setSpacing(10);

        HBox buttonShow = new HBox(topNewGameButton, topMainMenuButton);
        buttonShow.setSpacing(5);

        // top setting
        mainTop = new BorderPane();
        mainTopRight = new VBox(scoreShow, buttonShow);

        mainTop.setRight(mainTopRight);
        mainTop.setLeft(leftTopLabel);
        
        gridForSquares = getUpdatedAndStyledPane();
        
        squareStack.getChildren().add(gridForSquares);
        rootSetting.setTop(mainTop);
        rootSetting.setCenter(squareStack);
        
        Scene gameSkene = new Scene(rootSetting);
        
        gameSkene.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode() == KeyCode.UP) {
                moveExecutor.moveUp(false);
            } else if (event.getCode() == KeyCode.DOWN) {
                moveExecutor.moveDown(false);
            } else if (event.getCode() == KeyCode.RIGHT) {
                moveExecutor.moveRight(false);
            } else if (event.getCode() == KeyCode.LEFT) {
                moveExecutor.moveLeft(false);
            } else {
                return;
            }
            gridForSquares.getChildren().clear();
            gridForSquares = getUpdatedAndStyledPane();
            currentScoreLabel.setText("Current Score \n" + logic.getGamePoints());
            highScoreLabel.setText("High Score \n" + logic.getHighScore()); 
            squareStack.getChildren().add(gridForSquares);
            
            if (!logic.isMoveableSquares() && moveExecutor.isGameOver()) {
                topNewGameButton.setDisable(true);
                gameOverStack.getChildren().clear();
                gameOverStack = getGameOverStack();
                squareStack.getChildren().add(gameOverStack);
                if (logic.getGamePoints() == logic.getHighScore() 
                        || highScoreService.getFifthScore(logic.getTableSize()) < logic.getGamePoints()) {
                    logic.saveHighscore(logic.getGamePoints());
                }
            }
        });   
        
        return gameSkene;
    }
    
    public Scene getHighScoreScene() {
        BorderPane rootSetting = new BorderPane();
        rootSetting.setStyle("-fx-background-color:#008080");
        GridPane centerPane = new GridPane();
        
        Label highScores = styleHighScoreLabels("High Scores", 45, "#131516", true);
        Label threeLabel = styleHighScoreLabels("3x3", 19, "#131516", true);
        Label fourLabel = styleHighScoreLabels("4x4", 19, "#131516", true);
        Label fiveLabel = styleHighScoreLabels("5x5", 19, "#131516", true);
        
        Button backToMenuButton = getBackButton("Back to menu");
        
        VBox threeRowScores = getHighScoreList(highScoreService.getTopFiveScores(3), threeLabel);
        VBox fourRowScores = getHighScoreList(highScoreService.getTopFiveScores(4), fourLabel);
        VBox fiveRowScores = getHighScoreList(highScoreService.getTopFiveScores(5), fiveLabel);  
        
        VBox threeRow = new VBox(threeLabel, threeRowScores);
        VBox fourRow = new VBox(fourLabel, fourRowScores);
        VBox fiveRow = new VBox(fiveLabel, fiveRowScores);

        threeRow.setSpacing(5);
        fourRow.setSpacing(5);
        fiveRow.setSpacing(5);
        
        centerPane.add(threeRow, 1, 1);
        centerPane.add(fourRow, 2, 1);
        centerPane.add(fiveRow, 3, 1);
        centerPane.setAlignment(Pos.CENTER);
        centerPane.setHgap(20); 
        
        VBox topSet = new VBox();
        topSet.getChildren().addAll(highScores, backToMenuButton);
        highScores.setAlignment(Pos.CENTER);
        backToMenuButton.setAlignment(Pos.CENTER);
        topSet.setAlignment(Pos.CENTER);
        topSet.setSpacing(5);

        rootSetting.setCenter(centerPane);
        rootSetting.setTop(topSet);
        rootSetting.setAlignment(topSet, Pos.CENTER);
        
        return new Scene(rootSetting, 700, 516);
    }
    
    public StackPane getGameOverStack() {
        gameOverStack = new StackPane();
        
        Label gameOverLabel = new Label("You lost!");
        gameOverLabel.setFont(new Font("Sans-Serif", 30));
        gameOverLabel.setTextFill(Color.web("#FFFFFF", 0.9));
        
        Label endScore = new Label("Final score: " + logic.getGamePoints());
        endScore.setFont(new Font("Sans-Serif", 25));
        endScore.setTextFill(Color.web("#FFFFFF", 0.9));
        
        Rectangle endSquare = new Rectangle(sceneHeigth - 160, sceneWidth - 160, Color.web("#2F4F4F"));
        endSquare.setArcWidth(15);
        endSquare.setArcHeight(15);
        
        // opacity button
        Button opacityForStackButton = new Button("hold for boardview");
        opacityForStackButton.setFont(new Font("Sans-Serif", 14));
        opacityForStackButton.setStyle("-fx-background-color: #F9E79F; ");
        opacityForStackButton.setOnMousePressed((event) -> {gameOverStack.setOpacity(0.3);});
        opacityForStackButton.setOnMouseReleased((event) -> {gameOverStack.setOpacity(1);});
        opacityForStackButton.setOnMouseEntered(e -> opacityForStackButton.setStyle("-fx-background-color: #FEF9E7"));
        opacityForStackButton.setOnMouseExited(e -> opacityForStackButton.setStyle("-fx-background-color: #F9E79F"));
                
        HBox buttonCol = new HBox(getNewGameButton(), getHighScoreButton());
        buttonCol.setSpacing(18);
        buttonCol.setAlignment(Pos.CENTER);
        
        VBox centerRow = new VBox(opacityForStackButton, gameOverLabel, endScore, buttonCol);
        centerRow.setSpacing(20);
        centerRow.setAlignment(Pos.CENTER);
        gameOverStack.getChildren().addAll(endSquare, centerRow);

        return gameOverStack;
    }
    
    public GridPane getUpdatedAndStyledPane() {
        toReturnPane = setSquares();
        toReturnPane.setPadding(new Insets(5, 5, 5, 5));
        toReturnPane.setHgap(10);
        toReturnPane.setVgap(10);
        return toReturnPane;
    }
    
    public GridPane setSquares() {
        gridToReturn = new GridPane();
        StackPane squareStack = new StackPane();
        for (int i = 0; i < logic.getTableSize(); i++) {
            for (int j = 0; j < logic.getTableSize(); j++) {
                Square square = new Square(logic.getValueFromBoard(i, j));
                squareStack = square.getSquareStack(logic.getTableSize());
                gridToReturn.add(squareStack, j, i);
            }
        }
        return gridToReturn;
    }
    
    public Button styleMenuButtons(String name) {
        Button buttonToReturn = new Button(name);
        buttonToReturn.setStyle("-fx-background-color: #b0d3bf; ");
        buttonToReturn.setOnMouseEntered(e -> buttonToReturn.setStyle("-fx-background-color: #d3e5d1"));
        buttonToReturn.setOnMouseExited(e -> buttonToReturn.setStyle("-fx-background-color: #b0d3bf"));
        buttonToReturn.setFont(new Font("Sans-Serif", 15));    
        return buttonToReturn;
    }
       
    public Button getNewGameButton() {
        Button newGameButton = styleMenuButtons("New game");

        newGameButton.setOnMouseClicked((event) -> {
            squareStack.getChildren().remove(gameOverStack);
            gameOverStack.setVisible(false);
            topNewGameButton.setDisable(false);
            logic.setNewGame();
            gridForSquares = getUpdatedAndStyledPane();
            squareStack.getChildren().add(gridForSquares);
            currentScoreLabel.setText("Current Score \n" + logic.getGamePoints());
        });
        return newGameButton;
    }
    
    public Button getBackButton(String name) {
        Button topMainMenuButton = new Button(name);
        topMainMenuButton.setFont(new Font("Sans-Serif", 15));
        topMainMenuButton.setStyle("-fx-background-color: #CD5C5C; ");
        topMainMenuButton.setOnMouseEntered(e -> topMainMenuButton.setStyle("-fx-background-color: #F08080"));
        topMainMenuButton.setOnMouseExited(e -> topMainMenuButton.setStyle("-fx-background-color: #CD5C5C"));
        topMainMenuButton.setFocusTraversable(false);
        
        topMainMenuButton.setOnMouseClicked((event) -> {
            if (squareStack != null) {
                squareStack.getChildren().remove(gameOverStack);
            }
            currentStage.setScene(getMainMenuScene());
        });
        
        return topMainMenuButton;
    }
    
    public TextField getBoardSetUpTextField() {
        TextField chooseBoardSizeField = new TextField();
        chooseBoardSizeField.setFocusTraversable(false);
        chooseBoardSizeField.setPromptText("Choose board size (3-9)");
        
        chooseBoardSizeField.setOnKeyPressed((event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                handlePlayButtonEvent();
            }
        });
       
        return chooseBoardSizeField;
    }
    
    public void handlePlayButtonEvent() {
        if (!chooseBoardSizeField.getText().matches("[3-9]")) {
                if (chooseBoardSizeField.getText().equals("")) {
                    wrongInputErrors.setText("You forgot board size.");
                    wrongInputErrors.setVisible(true);
                    return;
                } else if (wrongInputCount == 0) {
                    wrongInputErrors.setText("How about between 3-9?");
                } else if (wrongInputCount == 1) {
                    wrongInputErrors.setText("Still not between 3-9.");
                } else if (wrongInputCount == 2) {
                    wrongInputErrors.setText("Not even funny.");
                } else if (wrongInputCount >= 3) {
                    wrongInputErrors.setText("Between 3-9 and we go.");
                } 
                wrongInputErrors.setVisible(true);
                wrongInputCount++;
                return;
            }
            
            int size = Integer.valueOf(chooseBoardSizeField.getText());
            
            currentStage.setScene(getGameScene(size));
            sceneHeigth = currentStage.getHeight();
            sceneWidth = currentStage.getWidth();
    }
    
    public Button getHighScoreButton() {
        Button highScoreButton = styleMenuButtons("High score");
        
        highScoreButton.setOnMouseClicked((event) -> {
            currentStage.setScene(getHighScoreScene());
        });
        
        return highScoreButton;
    }
    
    public Label styleHighScoreLabels(String name, int size, String hexColour, boolean underline) {
        Label highScoreLabel = new Label(name);
        highScoreLabel.setUnderline(underline);
        highScoreLabel.setFont(new Font("Sans-Serif", size));
        highScoreLabel.setTextFill(Color.web(hexColour));   
        
        return highScoreLabel;
    }
    
    public VBox getHighScoreList(List<String> scores, Label topic) {
        VBox row = new VBox(topic);
        row.setSpacing(15);
        if (scores.isEmpty()) {
            Label empty = styleHighScoreLabels("No games finished.", 15, "#FFFFFF", false);
            row.getChildren().add(empty);
        } else {
            for (String k : scores) {
                Label scoreAndDate = styleHighScoreLabels(k, 15, "#FFFFFF", false);
                row.getChildren().add(scoreAndDate);
            }
        }
        
        return row;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
