package spaceFillingCurves;

import java.awt.Dimension;
import javax.swing.JFrame;

/**
 *
 * @author arthu
 */
public class SpaceFillingCurves {

    public static void main(String[] args) {

        JFrame window = new JFrame();

        Curve c = new Curve();

        CurvePanel panel = new CurvePanel(c);
        window.setContentPane(panel);
        window.setVisible(true);
        window.setSize(new Dimension(800, 800));
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
