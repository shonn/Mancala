import java.awt.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 * Model part of mancala.
 */
public class Model {

    private final int BOARD_LEN = 6;
    private final int PLAYERS = 2;
    private final int MAXUNDO = 3;
    private boolean canUndo;
    private int stones;
    private int currentPlayer;
    private int currentUndoPlayer;
    private boolean done;
    private boolean freeTurn;
    private int[] mancalas;
    private int[][] pits;
    private int[] prevMancalas;
    private int[][] prevPits;
    private int[] undoCount;
    private ArrayList <ChangeListener> listeners;

    /**
     * Constructor for the board's model.
     */
    public Model() {
        stones = 0;
        listeners = new ArrayList <ChangeListener> ();
        mancalas = new int[PLAYERS];
        undoCount = new int[PLAYERS];
        pits = new int[PLAYERS][BOARD_LEN];
        prevPits = new int[PLAYERS][BOARD_LEN];
        prevMancalas = new int[PLAYERS];
        done = false;
        currentPlayer = 0;
        canUndo = false;
        freeTurn = false;
        currentUndoPlayer = 0;
    }

    /**
     * Checks for an empty pitSide, and if it is found, will 
     * end the game.
     */
    private void checkForEmptyRow() {
        for (int i = 0; i < PLAYERS; i++) {
            int emptyPits = 0;
            for (int j = 0; j < BOARD_LEN; j++) 
                if (pits[i][j] == 0)
                    ++emptyPits;
            if (BOARD_LEN == emptyPits) {
                for (int k = 0; k < BOARD_LEN; k++) {
                    mancalas[nextPlayer(i)] += pits[nextPlayer(i)][k];
                    pits[nextPlayer(i)][k] = 0;
                }
                if (mancalas[nextPlayer(currentPlayer)] == mancalas[currentPlayer]) {
                    currentPlayer = -1; // no winner
                } else if (mancalas[nextPlayer(currentPlayer)] > mancalas[currentPlayer]) {
                    currentPlayer = nextPlayer(currentPlayer);
                }
                done = true;
                break;
            }
        }
    }

    /**
     * Gets the current player's turn.
     * @return an int representing the player's turn.
     */
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Sets the number of stones per pit.
     * @param stons the number of stones.
     */
    public void setStoneNumber(int stons) {
        stones = stons;
        for (int i = 0; i < PLAYERS; i++)
            for (int j = 0; j < BOARD_LEN; j++)
                pits[i][j] = stones;
        notifyListeners();
    }

    /**
     * Check if the last stone is in mancala or the last stone that a player
     * drops is in his own empty pit or if one pitSide of the board is
     * empty.
     * @param pitSide player's pitSide
     * @param pit the pit
     */
    private void checkTurnOver(int pitSide, int pit) {
        if (pit != BOARD_LEN && pit != -1)
            if (pitSide == currentPlayer && pits[pitSide][pit] == 1) {
                mancalas[pitSide] += 1 + pits[nextPlayer(pitSide)][pit];
                pits[nextPlayer(pitSide)][pit] = 0;
                pits[pitSide][pit] = 0;
            } 
        if (!freeTurn)
            currentPlayer = nextPlayer(currentPlayer);
        //check if there is a winner at end of turn
        checkForEmptyRow();
        notifyListeners();
    }

    /**
     * Get Mancala
     * @return an array of Mancalas
     */
    public int[] getMancalas() {
        //return a copy, not the actual reference
        return (int[]) mancalas.clone();
    }

    /**
     * Get the player's pits
     * @return a 2d array of player pits
     */
    public int[][] getPits() {
        //return a copy, not the actual reference
        return (int[][]) pits.clone();
    }

    /**
     * Checks for the current player used for view's presentation
     * @return the string representation of the current player
     */
    public String getPlayer() {
        String cur = "Player " + (currentPlayer + 1) + "'s Turn";
        return cur;
    }

    /**
     * Get count of possible undo moves for current Player
     * @return undo count
     */
    private int getUndoCount() {
        return MAXUNDO - undoCount[currentUndoPlayer];
    }

    /**
     * Check if the game is over or not.
     * @return whether the game is over or not
     */
    public boolean isDone() {
        return done;
    }

    /**
     * Move stones on the board from a pitSide to another pit.
     * @param pitSide the player
     * @param pit the pit
     */
    public void move(int pitSide, int pit) {
        if (pitSide != currentPlayer || pits[pitSide][pit] == 0) {
            return; //cannot choose other player's pitSide or empty pit
        }
        savePrevState();
        if (freeTurn) {
            undoCount[pitSide] = 0;
            freeTurn = false; } 
        else if (!freeTurn && undoCount[pitSide] == 0) {
            currentUndoPlayer = pitSide;
            undoCount[nextPlayer(pitSide)] = 0; }
        int left = pits[pitSide][pit];
        canUndo = true;
        pits[pitSide][pit] = 0;
        while (left > 0) {
            if (pitSide == 1)
                pit = prevPit(pit);
            else
                pit = nextPit(pit);
            if (pit == -1 || pit == BOARD_LEN) {
                if (currentPlayer == pitSide) { 
                    mancalas[pitSide] += 1;
                    left--;
                    if (left == 0) {
                        freeTurn = true;
                        break; //player gets a free turn again
                    }
                }
                pitSide = nextPlayer(pitSide);
            }
            else {
                left--;
                pits[pitSide][pit]++;
            }
        }
        checkTurnOver(pitSide, pit);
    }

    /**
     * Get the previous pit.
     * @param pit the prev pit.
     * @return the prev pit.
     */
    private int prevPit(int pit) {
        return --pit;
    }

    /**
     * Get the next pit
     * @param pit the next pit
     * @return the next pit
     */
    private int nextPit(int pit) {
        return ++pit;
    }

    /**
     * Get the next player index
     * @param pitSide the pitSide of the player
     * @return the opposing pitSide
     */
    private int nextPlayer(int pitSide) {
        return ++pitSide == PLAYERS ? 0 : pitSide;
    }

    /**
     * Save the current state
     */
    private void savePrevState() {
        int prePits[][] = getPits();
        for (int i = 0; i < PLAYERS; i++)
            for (int j = 0; j < BOARD_LEN; j++)
                prevPits[i][j] = prePits[i][j];
        prevMancalas = getMancalas();
    }

    /**
     * Undo of what the previous player has just done.
     */
    public void undo() {
        if (isDone() || !canUndo || getUndoCount() <= 0) { // nothing
            return;
        }
        mancalas = prevMancalas.clone();
        for (int i = 0; i < PLAYERS; i++)
            for (int j = 0; j < BOARD_LEN; j++)
                pits[i][j] = prevPits[i][j];
        currentPlayer = currentUndoPlayer;
        undoCount[currentUndoPlayer]++;
        freeTurn = false;
        canUndo = false;
        notifyListeners();
    }

    /**
     * Attaches a listener to this object.
     * @param listener the listener to attach
     */
    public void attach(ChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Notifies the attached listeners of a change.
     */
    private void notifyListeners() {
       for (int i = 0; i < listeners.size(); i++)
          listeners.get(i).stateChanged(new ChangeEvent(this)); 
    }
}
