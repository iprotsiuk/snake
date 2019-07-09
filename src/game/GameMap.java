package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class GameMap extends JPanel implements ActionListener {

    private final int SIZEX = 640;
    private final int SIZEY = 480;
    private final int DOT_SIZE = 16;
    private final int ALL_DOTS = 1200;
    private final int DEFAULT_TIME = 200;
    private Image dot;
    private Image apple;
    private int appleX;
    private int appleY;
    private int[] x = new int[ALL_DOTS];
    private int[] y = new int[ALL_DOTS];
    private int dots;
    private Timer timer;
    private boolean inGame = true;
    private int count = 0;
    private LinkedList<Character> moves = new LinkedList<>();
    private char prevMove = 'r';
    private Object[][] gameField = new Object[40][30];
    private int time = 200;
    private boolean isButtonPressed = false;


    public GameMap(){
        setBackground(Color.pink);
        loadImages();
        initGame();
        addKeyListener(new MapKeyListener());
        setFocusable(true);
    }

    private void initGame(){
        dots = 3;
        for (int i = 0; i < dots; i++) {
            x[i] = 96 - i*DOT_SIZE;
            y[i] = 96;
        }
        if(timer == null)
            timer = new Timer(DEFAULT_TIME, this);
        moves.add('r');
        moves.add('r');
        timer.start();
        time = timer.getDelay();
        createApple();
        //isButtonPressed = false;
    }


    public void createApple(){
        appleX = new Random().nextInt(40)*DOT_SIZE;
        appleY = new Random().nextInt(30)*DOT_SIZE;
    }

    public void loadImages(){
        ImageIcon appleImg = new ImageIcon("resources/apple.png");
        apple = appleImg.getImage();
        ImageIcon dotImg = new ImageIcon("resources/dot.png");
        dot = dotImg.getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(inGame){
            g.drawString("Score: " + count, 0, 16);
            g.drawString("time is : " + time , 0, 32);
            g.drawImage(apple, appleX, appleY, this);
            for (int i = 0; i < dots; i++) {
                g.drawImage(dot,x[i],y[i],this);
            }
        } else{
            String str = "Game Over";
            g.drawString(str + "Score: " + count, 200, 200);
        }
    }

    private void move(){
        char nextMove;
        boolean isCheck = false;

        if(isButtonPressed)
            nextMove = moves.get(1);
        else {
            nextMove = moves.get(0);
            isCheck = true;
        }

        isButtonPressed = false;

        for (int i = dots; i > 0 ; i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }
        if(nextMove == 'l' && prevMove == 'r'){
            x[0] += DOT_SIZE;
        }
        else if(nextMove == 'u' && prevMove == 'd'){
            y[0] += DOT_SIZE;
        }
        else if(nextMove == 'r' && prevMove == 'l'){
            x[0] -= DOT_SIZE;
        }
        else if (nextMove == 'd' && prevMove == 'u'){
            y[0] -= DOT_SIZE;
        }
        else {
            if (nextMove == 'l')
                x[0] -= DOT_SIZE;
            if (nextMove == 'r')
                x[0] += DOT_SIZE;
            if (nextMove == 'u')
                y[0] -= DOT_SIZE;
            if (nextMove == 'd')
                y[0] += DOT_SIZE;
            if(isCheck) {
                prevMove = moves.get(0);
            }else{
                prevMove = moves.get(1);
            }
            moves.set(0, moves.get(1));

        }
    }

    private void checkApple() {
        if (x[0] == appleX && y[0] == appleY){
            dots++;
            createApple();
            count++;
        }
    }

    private void increaseSpeed(){
        timer.setDelay(timer.getDelay()/2);
        time = timer.getDelay();
    }

    private void decreaseSpeed(){
        timer.setDelay(timer.getDelay()*2);
        time = timer.getDelay();
    }

    private void checkCollisions(){
        for (int i = dots; i > 0; i--) {
            if(count > 1 && x[0] == x[i] && y[0] == y[i])
                inGame = false;
        }
        if(x[0] > SIZEX)
            inGame = false;
        if(x[0] < 0)
            inGame = false;
        if(y[0] > SIZEY)
            inGame = false;
        if(y[0] < 0)
            inGame = false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(inGame){
            checkApple();
            move();
            checkCollisions();
        }
        repaint();
    }

    private void restart(){
        inGame = true;
        count = 0;
        initGame();
        timer.setDelay(DEFAULT_TIME);
        time = DEFAULT_TIME;
    }

    class MapKeyListener extends KeyAdapter{

        @Override
        public void keyPressed(KeyEvent e) {
            super.keyPressed(e);
            int key = e.getKeyCode();
            {
                isButtonPressed = !isButtonPressed;
                if (key == KeyEvent.VK_LEFT) {
                    moves.set(0, moves.get(1));
                    moves.set(1, 'l');
                }
                if (key == KeyEvent.VK_RIGHT) {
                    moves.set(0, moves.get(1));
                    moves.set(1, 'r');
                }
                if (key == KeyEvent.VK_DOWN) {
                    moves.set(0, moves.get(1));
                    moves.set(1, 'd');
                }
                if (key == KeyEvent.VK_UP) {
                    moves.set(0, moves.get(1));
                    moves.set(1, 'u');
                }
            }
            if (key == KeyEvent.VK_R) {
                restart();
            }

            if(key == KeyEvent.VK_SHIFT)
                increaseSpeed();
            if(key == KeyEvent.VK_CONTROL)
                decreaseSpeed();
        }
    }
}