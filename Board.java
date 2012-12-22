import java.awt.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 * A class representing the Mancala this. Serves as 
 * the viewer and controller for the game of Mancala.
 * @author Shawn Nguyen
 */
public class Board extends JFrame implements ChangeListener
{
    private Model model;
    private StyleStrategy style = null;
    private final static int BUTTONS = 6;
    private final static int ROWS = 2;
    private final static int MANCALAS = 2;
    private JButton buttons[][];
    private JButton mancalas[];
    private JTextField textField;

    /**
     * Constructs a Mancala this.
     * @param model the Mancala data model.
     */
    public Board(Model m)
    {
        model = m;
        this.setTitle("Mancala");
    }

    /**
     * Gets the style chosen by the player for the
     * this's GUI.
     * @return style the style chosen by the player.
     */
    public void getStyle()
    {
        final JFrame frame = new JFrame("Please select a type");
        frame.setSize(400, 200);
        frame.setLayout(new BorderLayout());
        JButton buttonA = new JButton(" I want Style A (Red)! ");
        JButton buttonB = new JButton(" I want Style B (Green)! ");
        buttonA.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event) {
                frame.setVisible(false);
                frame.dispose();
                setStyle(new StyleStrategyA());
            }
        });
        buttonB.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event) {
                frame.setVisible(false);
                frame.dispose();
                setStyle(new StyleStrategyB());
            }
        });
        frame.add(buttonA, BorderLayout.WEST);
        frame.add(buttonB, BorderLayout.EAST);
        frame.pack();
        frame.setVisible(true);
        while (style == null);
    }

    /**
     * Sets the style accordingly to the style chosen 
     * by the player.
     * @param style the style chosen by the player
     */
    private void setStyle(StyleStrategy style)
    {
        this.style = style;
        buttons = new JButton[ROWS][BUTTONS];
        mancalas = new JButton[MANCALAS];
        mancalas[0] = new JButton();
        mancalas[1] = new JButton();
        //textField stating current player's turn
        textField = new JTextField();
        textField.setPreferredSize(new Dimension(200, 40));
        
        this.add(mancalas[1], BorderLayout.EAST);
        
        for (int i = 1; i >= 0; i--)
        {
            for (int j = 0; j < BUTTONS; j++) 
            {
                buttons[i][j] = new JButton();
                final int a = i;
                final int b = j;
                buttons[i][j].addActionListener(new ActionListener() 
                {
                        public void actionPerformed(ActionEvent e) 
                        {
                            model.move(a, b);
                        }
                });
                this.add(buttons[i][j]);
                
            }
            if (i == 1) {
                this.add(mancalas[0], BorderLayout.WEST);
                this.add(textField);
            }
        }
        //undo player's move
        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    model.undo();
                }
        });
        this.add(undoButton);
        this.setSize(950, 550);
        this.setLayout(new GridLayout(ROWS, BUTTONS));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        //draw the style
        style.drawComponents(model, this, buttons, mancalas);

        final JFrame requestor = new JFrame();
        requestor.setSize(250, 100);
        final JTextField textfield = new JTextField(
                "Please enter stones per pit: ");
        textfield.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                   String text = textfield.getText();
                   final int s = Integer.parseInt(text.replaceAll("\\D+",""));
                   if (s == 3 || s == 4) {
                        requestor.setVisible(false);
                        requestor.dispose();
                        model.setStoneNumber(s);
                   }
                   else
                        textfield.setText("Invalid input. Please re-enter: ");
                }
        });
        requestor.add(textfield);
        requestor.setVisible(true);
        stateChanged(new ChangeEvent(this));
    }

    /**
     * Adjusts the this when the model's state changes.
     * @param event the change event.
     */
    public void stateChanged(ChangeEvent event)
    {
        textField.setText(model.getPlayer());
        style.drawComponents(model, this, buttons, mancalas); 
        if (model.isDone()) {
            String status = " Player " + (model.getCurrentPlayer() + 1) + " won!";
            if (model.getCurrentPlayer() == -1)
                status = "Tie game, no winner.";
            JPanel endPanel = new JPanel();
            JOptionPane.showMessageDialog(endPanel, status);
        }
    }
}
