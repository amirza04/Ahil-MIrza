import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;

// Main class
public class StuntRacer extends JPanel implements Runnable {
    private Car car;
    private Track track;

    // Input flags
    private boolean accelerate = false;
    private boolean brake = false;
    private boolean turnLeft = false;
    private boolean turnRight = false;

    public StuntRacer() {
        car = new Car(200, 200);
        track = new Track();

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        accelerate = true;
                        break;
                    case KeyEvent.VK_DOWN:
                        brake = true;
                        break;
                    case KeyEvent.VK_LEFT:
                        turnLeft = true;
                        break;
                    case KeyEvent.VK_RIGHT:
                        turnRight = true;
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        accelerate = false;
                        break;
                    case KeyEvent.VK_DOWN:
                        brake = false;
                        break;
                    case KeyEvent.VK_LEFT:
                        turnLeft = false;
                        break;
                    case KeyEvent.VK_RIGHT:
                        turnRight = false;
                        break;
                }
            }
        });

        // Start game loop
        new Thread(this).start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        track.render(g);
        car.render(g);
    }

    @Override
    public void run() {
        while (true) {
            car.update(accelerate, brake, turnLeft, turnRight);
            repaint();
            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Stunt Racer");
        StuntRacer gamePanel = new StuntRacer();
        frame.add(gamePanel);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

// Car class
class Car {
    private int x, y;
    private int width = 50, height = 30;
    private double speed = 0;
    private double angle = 0; // In radians
    private final double MAX_SPEED = 8.0;
    private final double ACCELERATION = 0.1;
    private final double DECELERATION = 0.05;
    private final double TURN_SPEED = Math.PI / 60;

    public Car(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update(boolean accelerate, boolean brake, boolean turnLeft, boolean turnRight) {
        if (accelerate) {
            speed = Math.min(speed + ACCELERATION, MAX_SPEED);
        } else if (brake) {
            speed = Math.max(speed - ACCELERATION, -MAX_SPEED / 2); // Reverse has half the speed limit
        } else {
            if (speed > 0) {
                speed = Math.max(speed - DECELERATION, 0);
            } else if (speed < 0) {
                speed = Math.min(speed + DECELERATION, 0);
            }
        }

        if (turnLeft && speed != 0) {
            angle -= TURN_SPEED;
        }
        if (turnRight && speed != 0) {
            angle += TURN_SPEED;
        }

        // Update position based on speed and direction
        x += (int)(speed * Math.cos(angle));
        y += (int)(speed * Math.sin(angle));
    }

    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.RED);

        // Save current transform
        AffineTransform old = g2d.getTransform();

        // Rotate the car
        g2d.translate(x + width / 2, y + height / 2);
        g2d.rotate(angle);
        g2d.translate(-width / 2, -height / 2);

        // Draw car
        g2d.fillRect(0, 0, width, height);

        // Restore old transform
        g2d.setTransform(old);
    }
}

// Track class
class Track {
    private int[][] layout; // Simple 2D array for track tiles (1 = track, 0 = grass)

    public Track() {
        layout = new int[][] {
            {0, 0, 0, 1, 1, 1, 0, 0, 0},
            {0, 0, 0, 1, 0, 1, 0, 0, 0},
            {0, 0, 0, 1, 0, 1, 1, 1, 0},
            {1, 1, 1, 1, 0, 0, 0, 1, 0},
            {0, 0, 0, 1, 0, 0, 0, 1, 0},
            {0, 0, 0, 1, 1, 1, 1, 1, 0}
        };
    }

    public void render(Graphics g) {
        for (int row = 0; row < layout.length; row++) {
            for (int col = 0; col < layout[row].length; col++) {
                if (layout[row][col] == 1) {
                    g.setColor(Color.GRAY); // Track
                } else {
                    g.setColor(Color.GREEN); // Grass
                }
                g.fillRect(col * 100, row * 100, 100, 100);
            }
        }
    }
}
