package core;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class BoardPanel extends JPanel{
    private JTextArea message;
    private JTextArea command;

    private ArrayList<Square> allSquares = new ArrayList<>();
    private ArrayList<Square> unbuyableSquares = new ArrayList<>(); // squares like "Go", "Chances" etc...

    public JTextArea getMessage() {
        return message;
    }

    public JTextArea getCommand() {
        return command;
    }

    public Square getSquareAtIndex(int location) {
        return allSquares.get(location);
    }

    public BoardPanel() {
        JFrame jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setBounds(100, 100, 450, 300);
        jFrame.setSize(1080, 720);

        setBorder(new LineBorder(new Color(0, 0, 0)));
        setSize(1080, 720);
        this.setLayout(null);
        initializeSquares();

        jFrame.add(this);
        jFrame.setVisible(true);
    }

    private void initializeSquares() {

        Random rand = new Random();
        TileType[] boardTiles = new TileType[32];
        for (int i = 0; i < 32; i++) {
            //initialize all tiles as empty
            boardTiles[i] = TileType.EMPTY;
        }
        //hard code non-monster tiles
        boardTiles[0] = TileType.START;
        boardTiles[16] = TileType.CHEST;
        boardTiles[8] = boardTiles[24] = TileType.SHOP;

        TileType[] monsterTypes = {TileType.SIN_M, TileType.DUO_M, TileType.TRI_M};
        int[] monsterCount = {12, 8, 4};
        //generate monster_type[i] tiles for monsterCount[i] times
        for (int i = 0; i < 3; i++) {
            int count = 0;
            do {
                //generate number from 1 to 31 (since 0 is START TILE)
                int pos = rand.nextInt(31) + 1;
                //leave hard-coded tiles untouched
                if (pos % 4 != 0 && boardTiles[pos] == TileType.EMPTY) {
                    count++;
                    boardTiles[pos] = monsterTypes[i];
                }
            }
            while (count < monsterCount[i]);
        }

        int startX = 1080;
        int startY = 720;
        int width = 120;
        int height = 80;
        for (int i = 0; i < 9; i++) {

            Square square;
            if (i == 4) {
                square = new Square(startX - width, startY - height, width, height, "");
            } else {
                square = new Square(startX - width, startY - height, width, height, boardTiles[i].name());
            }

            this.add(square);
            allSquares.add(square);
            unbuyableSquares.add(square);
            startX = startX - width;
        }

        for (int i = 9; i < 17; i++) {

            startY = startY - height;

            Square square;
            if (i == 12) {
                square = new Square(startX, startY - height, width, height, "");
            } else {
                square = new Square(startX, startY - height, width, height, boardTiles[i].name());
            }
            this.add(square);
            allSquares.add(square);
            unbuyableSquares.add(square);

        }

        for (int i = 17; i < 25; i++) {

            startX = startX + width;

            Square square;
            if (i == 20) {
                square = new Square(startX, startY - height, width, height, "");
            } else {
                square = new Square(startX, startY - height, width, height, boardTiles[i].name());
            }
            this.add(square);
            allSquares.add(square);
            unbuyableSquares.add(square);
        }

        for (int i = 25; i < 32; i++) {

            startY = startY + height;

            Square square;
            if (i == 28) {
                square = new Square(startX, startY - height, width, height, "");
            } else {
                square = new Square(startX, startY - height, width, height, boardTiles[i].name());
            }
            this.add(square);
            allSquares.add(square);
            unbuyableSquares.add(square);
        }

        message = new JTextArea("");
        message.setEditable(false);
        message.setBounds(120, 80, 840, 340);
        message.setFont(new Font("Consolas", Font.PLAIN, 16));
        message.setBorder (BorderFactory.createLineBorder(Color.gray,5));
        this.add(message);

        command = new JTextArea("");
        command.setEditable(false);
        command.setBounds(120, 420, 840, 220);
        command.setFont(new Font("Consolas", Font.PLAIN, 16));
        command.setBorder (BorderFactory.createLineBorder(Color.gray,5));
        this.add(command);

    }
}
