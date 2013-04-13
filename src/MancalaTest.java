/**
 * A tester to initialize all components.
 */
public class MancalaTest
{
    public static void main(String[] args)
    {
        Model model = new Model();
        Board board = new Board(model);
        model.attach(board);
        board.getStyle();
    }
}
