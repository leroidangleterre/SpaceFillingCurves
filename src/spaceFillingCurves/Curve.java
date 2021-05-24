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
    private double xStart, yStart, xEnd, yEnd; // Points that are used to link the curve to other curves.

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
        xStart = xCenterParam;
        yStart = yCenterParam;
        xEnd = xCenterParam;
        yEnd = yCenterParam;

        id = NB_CURVES_CREATED;
        NB_CURVES_CREATED++;
    }

    public Curve(Curve origin) {
        this(origin.depth, origin.size, origin.rotationLevel, origin.xCenter, origin.yCenter);
        this.xStart = origin.xStart;
        this.yStart = origin.yStart;
        this.xEnd = origin.xEnd;
        this.yEnd = origin.yEnd;
    }

    public void paint(Graphics g, double x0, double y0, double zoom) {

        int panelHeight = g.getClipBounds().height;

        g.setColor(Color.red);
        switch (depth) {
        case 0:
            int xApp = (int) (x0 + this.xCenter * zoom);
            int yApp = (int) (panelHeight - (y0 + this.yCenter * zoom));
            g.fillRect(xApp, yApp, 2, 2);
            break;
        case 1:try {
            // Draw the three segments
            for (int index = 0; index < 3; index++) {
                int xApp0 = (int) (x0 + subCurves[index].xCenter * zoom);
                int yApp0 = (int) (panelHeight - (y0 + subCurves[index].yCenter * zoom));
                int xApp1 = (int) (x0 + subCurves[index + 1].xCenter * zoom);
                int yApp1 = (int) (panelHeight - (y0 + subCurves[index + 1].yCenter * zoom));
                g.drawLine(xApp0, yApp0, xApp1, yApp1);
            }

//            // Test: paint the four points
//            for (int index = 0; index <= 3; index++) {
//                switch (index) {
//                case 0:
//                    g.setColor(Color.red);
//                    break;
//                case 1:
//                    g.setColor(Color.orange);
//                    break;
//                case 2:
//                    g.setColor(Color.yellow);
//                    break;
//                case 3:
//                    g.setColor(Color.green);
//                    break;
//                }
//                int xApp0 = (int) (x0 + subCurves[index].xCenter * zoom);
//                int yApp0 = (int) (panelHeight - (y0 + subCurves[index].yCenter * zoom));
//
//                int width = 10;
//                g.fillOval(xApp0 - width / 2, yApp0 - width / 2, width, width);
//            }
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
            paintLink(subCurves[0], true,
                    subCurves[1], true,
                    g, x0, y0, zoom);
            paintLink(subCurves[1], false,
                    subCurves[2], true,
                    g, x0, y0, zoom);
            paintLink(subCurves[2], false,
                    subCurves[3], false,
                    g, x0, y0, zoom);
            break;
        }

        paintBorders(g, x0, y0, zoom);
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

                subCurves[index].xStart = 0.5 / (this.xStart - this.xCenter) + subCurves[index].xCenter;
                subCurves[index].yStart = 0.5 / (this.yStart - this.yCenter) + subCurves[index].yCenter;
                subCurves[index].xEnd = 0.5 / (this.xEnd - this.xCenter) + subCurves[index].xCenter;
                subCurves[index].yEnd = 0.5 / (this.yEnd - this.yCenter) + subCurves[index].yCenter;

                double oldXCenter = subCurves[index].xCenter;
                double oldYCenter = subCurves[index].yCenter;
                double oldDXStart = subCurves[index].xStart - oldXCenter;
                double oldDYStart = subCurves[index].yStart - oldYCenter;
                double oldDXEnd = subCurves[index].xEnd - oldXCenter;
                double oldDYEnd = subCurves[index].yEnd - oldYCenter;
                if (index == 0) {
                    // Rotate the coordinates of the start and end, counterclockwise, around the center of the sub-curve
                    subCurves[index].xStart = oldXCenter - oldDYStart;
                    subCurves[index].yStart = oldXCenter + oldDXStart;
                    subCurves[index].xEnd = oldXCenter - oldDYEnd;
                    subCurves[index].yEnd = oldXCenter + oldDXEnd;
                }
                if (index == 3) {
                    // Rotate the coordinates of the start and end, clockwise, around the center of the sub-curve
                    subCurves[index].xStart = oldXCenter + oldDYStart;
                    subCurves[index].yStart = oldXCenter - oldDXStart;
                    subCurves[index].xEnd = oldXCenter + oldDYEnd;
                    subCurves[index].yEnd = oldXCenter - oldDXEnd;
                }
            }

            // Both curves A and D are turned. Their rotation level is changed as well as their path direction.
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
        if (depth >= 1) {
            this.depth--;
            if (depth > 0) {
                for (Curve child : subCurves) {
                    child.merge();
                }
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
     * @param useStart1 when true, use the start of the first curve; otherwise
     * use its end
     * @param subCurve0
     * @param useStart2
     * @param g
     * @param x0
     * @param y0
     * @param zoom
     */
    private void paintLink(Curve curve1, boolean useStart1,
            Curve curve2, boolean useStart2,
            Graphics g, double x0, double y0, double zoom) {

        double x1, y1, x2, y2;

        if (useStart1) {
            x1 = curve1.getXStart();
            y1 = curve1.getYStart();
        } else {
            x1 = curve1.getXEnd();
            y1 = curve1.getYEnd();
        }

        if (useStart2) {
            x2 = curve2.getXStart();
            y2 = curve2.getYStart();
        } else {
            x2 = curve2.getXEnd();
            y2 = curve2.getYEnd();
        }
        g.setColor(Color.blue);

        int panelHeight = g.getClipBounds().height;
        g.drawLine((int) (x0 + x1 * zoom),
                (int) (panelHeight - (y0 + y1 * zoom)),
                (int) (x0 + x2 * zoom),
                (int) (panelHeight - (y0 + y2 * zoom)));
    }

//    private double getDistance(double x0, double y0, double x1, double y1) {
//        return Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0));
//    }
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

    private double getXEnd() {
        if (depth == 0) {
            return this.xCenter;
        } else {
            return this.subCurves[3].getXStart();
        }
    }

    private double getYEnd() {
        if (depth == 0) {
            return this.yCenter;
        } else {
            return this.subCurves[3].getYStart();
        }
    }

    private void paintBorders(Graphics g, double x0, double y0, double zoom) {

        double margin = 0.1 * size;
        double xMin = xCenter - (size + margin) / 2;
        double xMax = xCenter + (size + margin) / 2;
        double yMin = yCenter - (size + margin) / 2;
        double yMax = yCenter + (size + margin) / 2;

        g.setColor(Color.gray);
    }

}
