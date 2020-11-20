
package game2048.domain;

import java.util.ArrayList;
import java.util.Random;

public class GameLogic {
    private int[][] gameTable;
    private final Random ran;
    private ArrayList<byte[]> emptyCoordinates;
    private int tableLength;
    private Scoreboard scoreboard;
    
    public GameLogic(int size) {
        scoreboard = new Scoreboard();
        tableLength = size;
        gameTable = new int[size][size];
        ran = new Random();
        findEmptyCoordinates();
        initializeStartBoard();
    }
    
    public void initializeStartBoard() {
        for (int i = 0; i < 2; i++) {
            byte[] coordinatesXY = getRandomCoordinate();
            emptyCoordinates.remove(coordinatesXY);
            gameTable[coordinatesXY[0]][coordinatesXY[1]] = 2;
        }
    }
    
    public void updateBoard() {
        findEmptyCoordinates();
        addRandomSquare();
    }
    
    public void findEmptyCoordinates() {
        emptyCoordinates = new ArrayList();
        for (int x = 0; x < tableLength; x++) {
            for (int y = 0; y < tableLength; y++) {
                if (gameTable[x][y] == 0) {
                    byte[] coordinates = {(byte) x, (byte) y};
                    emptyCoordinates.add(coordinates);   
                }
            }
        }
    }

    public void addRandomSquare() {
        byte[] coordinate = getRandomCoordinate();
        gameTable[coordinate[0]][coordinate[1]] = Math.random() <= 0.1 ? 4 : 2;
    }
    
    public void setNewGame() {
        scoreboard.setToZeroCurrentScore();
        gameTable = new int[tableLength][tableLength];
        findEmptyCoordinates();
        initializeStartBoard();
    }
    
    public int[][] getTable() {
        return gameTable;
    }
    
    public void setTable(int[][] k) {
        gameTable = k;
    }
    
    public int getValueFromBoard(int x, int y) {
        return gameTable[x][y];
    }
     
    public byte[] getRandomCoordinate() {
        return emptyCoordinates.get(ran.nextInt(emptyCoordinates.size()));
    }
    
    public boolean isMoveableSquares() {
        return emptyCoordinates.size() != 1;
    }
    
    public int getTableSize() {
        return tableLength;
    }
    
    public int getHighScore() {
        return scoreboard.getHighScore();
    }
    
    public int getGamePoints() {
        return scoreboard.getCurrentScore();
    }
    
    public boolean isGameOver() {
        return !moveRight(true) && !moveUp(true) && !moveDown(true) && !moveLeft(true);
    }
    
    //spageth code down from here, need to be divided for couple of methods
    public boolean moveUp(boolean gameOverTest) {
        boolean isMoveMade = false;
        for (int y = 0; y < tableLength; y++) {
            int lastChangeNum = -1;
            int lastChangeNumIndex = -10;
            for (int x = 1; x < tableLength; x++) {
                for (int lastX = x; lastX >= 1; lastX--) {
                    if (gameTable[lastX][y] == 0) {
                        continue;
                    }
                    int currentValue = gameTable[lastX][y];
                    int positionToMoveValue = gameTable[lastX - 1][y];
                    if (positionToMoveValue == 0) {
                        if (gameOverTest) {
                            return true;
                        }     
                        isMoveMade = true;
                        gameTable[lastX - 1][y] = currentValue;
                        gameTable[lastX][y] = 0;
                    } else if (positionToMoveValue == currentValue && currentValue != lastChangeNum * 2 || 
                               positionToMoveValue == currentValue && currentValue == lastChangeNum * 2 && lastX - lastChangeNumIndex > 1) { 
                        if (gameOverTest) {
                            return true;
                        } 
                        isMoveMade = true;
                        lastChangeNum = currentValue;
                        lastChangeNumIndex = lastX - 1;
                        gameTable[lastX - 1][y] = currentValue * 2;
                        gameTable[lastX][y] = 0;
                        scoreboard.addCurrentPoints(currentValue);
                    }
                }
            }
        }
        if (isMoveMade) {
            updateBoard();
        }
        return false;
    }
    
    public boolean moveDown(boolean gameOverTest) {
        boolean isMoveMade = false;
        for (int y = 0; y < tableLength; y++) {
            int lastChangeNum = -1;
            int lastChangeNumIndex = -10;
            for (int x = tableLength - 2; x >= 0; x--) {
                for (int lastX = x; lastX < tableLength - 1; lastX++) {
                    if (gameTable[lastX][y] == 0) {
                        continue;
                    }
                    int currentValue = gameTable[lastX][y];
                    int positionToMoveValue = gameTable[lastX + 1][y];
                    if (positionToMoveValue == 0) {
                        isMoveMade = true;
                        if (gameOverTest) {
                            return true;
                        }
                        gameTable[lastX + 1][y] = currentValue;
                        gameTable[lastX][y] = 0;
                    } else if (positionToMoveValue == currentValue && currentValue != lastChangeNum * 2 || 
                               positionToMoveValue == currentValue && currentValue == lastChangeNum * 2 && lastChangeNumIndex - lastX > 1) {                         
                        isMoveMade = true;
                        if (gameOverTest) {
                            return true;
                        }                        
                        lastChangeNum = currentValue;
                        lastChangeNumIndex = lastX - 1;
                        gameTable[lastX + 1][y] = currentValue * 2;
                        gameTable[lastX][y] = 0;
                        scoreboard.addCurrentPoints(currentValue);
                    }
                }
            }
        }
        if (isMoveMade) {
            updateBoard();
        }
        return false;
    }
    
    public boolean moveLeft(boolean gameOverTest) {
        boolean isMoveMade = false;
        for (int x = 0; x < tableLength; x++) {
            int lastChangeNum = -1;
            int lastChangeNumIndex = -10;
            for (int y = 1; y < tableLength; y++) {
                for (int lastY = y; lastY > 0; lastY--) {
                    if (gameTable[x][lastY] == 0) {
                        continue;
                    }
                    int currentValue = gameTable[x][lastY];
                    int positionToMoveValue = gameTable[x][lastY - 1];
                    if (positionToMoveValue == 0) {
                        isMoveMade = true;
                        if (gameOverTest) {
                            return true;
                        } 
                        gameTable[x][lastY - 1] = currentValue;
                        gameTable[x][lastY] = 0;
                    } else if (positionToMoveValue == currentValue && currentValue != lastChangeNum * 2 || 
                               positionToMoveValue == currentValue && currentValue == lastChangeNum * 2 && lastChangeNumIndex - lastY > 1) {                         
                        isMoveMade = true;
                        if (gameOverTest) {
                            return true;
                        }
                        lastChangeNum = currentValue;
                        lastChangeNumIndex = lastY - 1;
                        gameTable[x][lastY - 1] = currentValue * 2;
                        gameTable[x][lastY] = 0;
                        scoreboard.addCurrentPoints(currentValue);
                    }
                }
            }
        }
        if (isMoveMade) {
            updateBoard();
        }
        return false;
    }
    
    public boolean moveRight(boolean gameOverTest) {
        boolean isMoveMade = false;
        for (int x = 0; x < tableLength; x++) {
            int lastChangeNum = -1;
            int lastChangeNumIndex = -10;
            for (int y = tableLength - 2; y >= 0; y--) {
                for (int lastY = y; lastY < tableLength - 1; lastY++) {
                    if (gameTable[x][lastY] == 0) {
                        continue;
                    }
                    
                    int currentValue = gameTable[x][lastY];
                    int positionToMoveValue = gameTable[x][lastY + 1];
                    if (positionToMoveValue == 0) {
                        isMoveMade = true;
                        if (gameOverTest) {
                            return true;
                        }                        
                        gameTable[x][lastY + 1] = currentValue;
                        gameTable[x][lastY] = 0;
                    } else if (positionToMoveValue == currentValue && currentValue != lastChangeNum * 2 || 
                               positionToMoveValue == currentValue && currentValue == lastChangeNum * 2 && lastY - lastChangeNumIndex > 1) {                         
                        isMoveMade = true;
                        if (gameOverTest) {
                            return true;
                        }                        
                        lastChangeNum = currentValue;
                        lastChangeNumIndex = lastY - 1;
                        gameTable[x][lastY + 1] = currentValue * 2;
                        gameTable[x][lastY] = 0;
                        scoreboard.addCurrentPoints(currentValue);
                    }
                }
            }
        }
        if (isMoveMade) {
            updateBoard();
        }
        return false;
    }
}
