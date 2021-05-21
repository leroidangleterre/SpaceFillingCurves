package spaceFillingCurves;

import java.awt.Color;
import java.awt.Graphics;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 *
 * @author arthu
 */
public class Curve {

    private int depth;
    private double size;
    private int rotationLevel;
    private double rotation;
    private Curve[] subCurves; // These are, in order, the subcurves A, B, C and D.
    private double xCenter, yCenter;

    private static int NB_CURVES_CREATED = 0;
    private int id;

    private static double DEFAULT_ANGLE = PI / 2;

    public Curve() {
        this(0, 1, 0, 0, 0);
    }

    public Curve(int depthParam, double sizeParam, int rotationLevelParam, double xCenterParam, double yCenterParam) {
        depth = depthParam;
        size = sizeParam;
        rotationLevel = rotationLevelParam;
        subCurves = null;
        xCenter = xCenterParam;
        yCenter = yCenterParam;

        id = NB_CURVES_CREATED;
        NB_CURVES_CREATED++;
    }

    public Curve(Curve origin) {
        this(origin.depth, origin.size, origin.rotationLevel, origin.xCenter, origin.yCenter);
    }

    public void paint(Graphics g, double x0, double y0, double zoom) {
        g.setColor(Color.red);
        switch (depth) {
        case 0:
            int xApp = (int) (x0 + this.xCenter * zoom);
            int yApp = (int) (y0 + this.yCenter * zoom);
            g.fillRect(xApp, yApp, 2, 2);
            break;
        case 1:try {
            // Draw the three segments
            for (int index = 0; index < 3; index++) {
                int xApp0 = (int) (x0 + subCurves[index].xCenter * zoom);
                int yApp0 = (int) (y0 + subCurves[index].yCenter * zoom);
                int xApp1 = (int) (x0 + subCurves[index + 1].xCenter * zoom);
                int yApp1 = (int) (y0 + subCurves[index + 1].yCenter * zoom);
                g.drawLine(xApp0, yApp0, xApp1, yApp1);
            }
        } catch (NullPointerException e) {
            System.out.println("Cannot paint curve " + this.id);
        }
        break;
        default:
            // Draw the four sub-curves
            for (int index = 0; index <= 3; index++) {
                subCurves[index].paint(g, x0, y0, zoom);
            }
            // Draw the links between the curves
            for (int index = 0; index < 3; index++) {
                paintLink(subCurves[index], subCurves[index + 1], g, x0, y0, zoom);
            }
            break;
        }
    }

    public void subdivide() {
        if (depth == 0) {
            // Create four new sub-curves
            subCurves = new Curve[4];
            for (int index = 0; index <= 3; index++) {
                subCurves[index] = new Curve(this);
                subCurves[index].xCenter = this.xCenter + this.size / 2 * (index == 0 || index == 1 ? -1 : +1);
                subCurves[index].yCenter = this.yCenter + this.size / 2 * (index == 0 || index == 3 ? -1 : +1);
                subCurves[index].size = this.size / 2;
                subCurves[index].depth = 0;
                subCurves[index].rotationLevel = this.rotationLevel;
            }
            subCurves[0].rotationLevel = this.rotationLevel - 1;
            subCurves[3].rotationLevel = this.rotationLevel + 1;
        } else {
            // Subdivide the sub-curves
            for (int index = 0; index <= 3; index++) {
                subCurves[index].subdivide();
            }
        }

        this.computeRotation();
        depth++;
    }

    /**
     * Remove the highest level of detail.
     *
     */
    public void merge() {
        this.depth--;
        if (depth > 0) {
            for (Curve child : subCurves) {
                child.merge();
            }
        }
    }

    /**
     * Rotate all the points around the center of the curve
     *
     * @param angle
     */
    private void computeRotation() {

        this.rotation = rotationLevel * DEFAULT_ANGLE;

        for (int index = 0; index <= 3; index++) {
            double xBefore = this.size / 2 * (index == 0 || index == 1 ? -1 : +1);
            double yBefore = this.size / 2 * (index == 0 || index == 3 ? -1 : +1);

            double xAfter = xBefore * cos(rotation) - yBefore * sin(rotation);
            double yAfter = xBefore * sin(rotation) + yBefore * cos(rotation);
            subCurves[index].xCenter = this.xCenter + xAfter;
            subCurves[index].yCenter = this.yCenter + yAfter;
        }
    }

    /**
     * Draw a line between the end of the first curve and the start of the
     * second one.
     *
     * @param subCurve
     * @param subCurve0
     * @param g
     * @param x0
     * @param y0
     * @param zoom
     */
    private void paintLink(Curve curve1, Curve curve2, Graphics g, double x0, double y0, double zoom) {

        double xStart, yStart, xEnd, yEnd;

        // We need to change this: the link shall be built between the curves so that it has minimal length.
        if (curve1.rotation % 4 == 0 || curve1.rotation % 4 == 2) {
            xEnd = curve1.getXEnd();
            yEnd = curve1.getYEnd();
        } else {
            xEnd = curve1.getXStart();
            yEnd = curve1.getYStart();
        }

        if (curve2.rotation % 4 == 0 || curve2.rotation % 4 == 2) {
            xStart = curve2.getXStart();
            yStart = curve2.getYStart();
        } else {
            xStart = curve2.getXEnd();
            yStart = curve2.getYEnd();
        }

        g.setColor(Color.blue);
        g.drawLine((int) (x0 + xEnd * zoom),
                (int) (y0 + yEnd * zoom),
                (int) (x0 + xStart * zoom),
                (int) (y0 + yStart * zoom));
    }

    private double getXEnd() {
        if (depth == 0) {
            return this.xCenter;
        } else {
            return this.subCurves[3].getXEnd();
        }
    }

    private double getYEnd() {
        if (depth == 0) {
            return this.yCenter;
        } else {
            return this.subCurves[3].getYEnd();
        }
    }

    private double getXStart() {
        if (depth == 0) {
            return this.xCenter;
        } else {
            return this.subCurves[0].getXEnd();
        }
    }

    private double getYStart() {
        if (depth == 0) {
            return this.yCenter;
        } else {
            return this.subCurves[0].getYEnd();
        }
    }

}
