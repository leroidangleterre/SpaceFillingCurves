package spaceFillingCurves;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JPanel;

/**
 *
 * @author arthu
 */
public class CurvePanel extends JPanel implements KeyListener, MouseMotionListener, MouseWheelListener {

    private Curve curve;

    private double x0, y0, zoom;

    private int mouseX, mouseY;
    private int currentPanelHeight;

    public CurvePanel() {
        super();
        Dimension preferredSize = new Dimension(800, 600);
        setPreferredSize(preferredSize);
        setSize(preferredSize);
        setVisible(true);
        this.setFocusable(true);
        this.addKeyListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);

        currentPanelHeight = 0;

        x0 = 918;
        y0 = 502;
        zoom = 489;
    }

    public CurvePanel(Curve c) {
        this();
        this.curve = c;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.white);
        int width = g.getClipBounds().width;
        int height = g.getClipBounds().height;
        g.fillRect(0, 0, width, height);

        currentPanelHeight = g.getClipBounds().height;

        curve.paint(g, x0, y0, zoom);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        switch (e.getKeyChar()) {
        case '+': // For some reason this is what works, not VK_PLUS or VK_ADD
            curve.subdivide();
            repaint();
            break;
        case KeyEvent.VK_MINUS:
            curve.merge();
            repaint();
            break;
        case KeyEvent.VK_0:
            resetView();
            break;
        default:
            break;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        x0 += e.getX() - mouseX;
        y0 -= e.getY() - mouseY;
//        System.out.println("x0: " + x0 + ", y0: " + y0);
        mouseX = e.getX();
        mouseY = e.getY();
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    /**
     * Mouse wheel up: zoom in around the mouse pointer.
     * Mouse wheel down: zoom out.
     *
     * @param e
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double coef = 1.1;
        double fact = (e.getWheelRotation() > 0 ? 1 / coef : coef);
        x0 = e.getX() + fact * (x0 - e.getX());
        y0 = currentPanelHeight - e.getY() - fact * (currentPanelHeight - y0 - e.getY());
        zoom = fact * zoom;
//        System.out.println("zoom: " + zoom);
        repaint();
    }

    private void resetView() {
        zoom = 1;
        x0 = 0;
        y0 = 0;
        repaint();
    }
}
