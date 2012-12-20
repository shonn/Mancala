import java.awt.*;
import java.util.*;
import javax.swing.*;

/**
   A strategy that allows a board to be viewed with different styles.
   The shape and color of pits and Mancalas can be the part of the style.
*/
public interface StyleStrategy
{
    /**
     * Draws the components of the board with this style. 
     * @param model the model
     * @param board the viewer/bard
     * @param buttons the pits of the board
     * @param mancalas the board's mancalas
     */
    void drawComponents(Model model, Board board, JButton[][] buttons, JButton[] mancalas);
}
    
