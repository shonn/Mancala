import java.awt.*;
import javax.swing.*;
/**
 * A Style Strategy that implements the style strategy interface.
 * Team Happy.
 */
public class StyleStrategyA implements StyleStrategy {
    /**
     * Constructs a style strategy B.
     */
    public StyleStrategyA() {
    }

    /**
     * Draws the components of the board with this style. 
     * @param model the model
     * @param board the viewer/bard
     * @param buttons the pits of the board
     * @param mancalas the board's mancalas
     */
    public void drawComponents(Model model, Board board, JButton[][] buttons, JButton[] mancalas) {
        int[][] pits = model.getPits();
        int[] man = model.getMancalas();
        for (int i = 0; i < buttons.length; i++)
            for (int j = 0; j < buttons[0].length; j++) {
                String rocks = "<html>";
                for (int k = 0; k < pits[i][j]; k++) {
                    if (k % 4 == 0)
                        rocks += "<br />";
                    rocks += "o";
                }
                rocks += "</html>";
                buttons[i][j].setText(rocks);
                buttons[i][j].setBackground(Color.RED);
                buttons[i][j].setIcon(new ImageIcon(getClass().getResource("Circle_Red.png")));
                buttons[i][j].setHorizontalTextPosition(JButton.CENTER);
                buttons[i][j].setVerticalTextPosition(JButton.CENTER);
            }
        mancalas[0].setText("P1: " + man[0]);
        mancalas[1].setText("P2: " + man[1]);
    }
}
