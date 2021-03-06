
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import static java.lang.Math.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JPanel;

/**
 * Simple graphical application to draw a Bezier curve.
 *
 * @author maikel
 */
public class GUI extends java.awt.Frame {

    ArrayList<Vector> points = new ArrayList<>();
    Set<PointView> views = new HashSet();
    Curve c;
    Vector dragPoint;
    final static private boolean CUBIC = false; // true if using cubic spline

    /**
     * Creates new form NewFrame
     */
    public GUI() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                paintMainPanel(this, g);
            }
        };
        jCheckBox1 = new javax.swing.JCheckBox();
        jButton1 = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(100, 100));
        setPreferredSize(new java.awt.Dimension(800, 800));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setMinimumSize(new java.awt.Dimension(800, 800));
        jPanel1.setName(""); // NOI18N
        jPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel1MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jPanel1MouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel1MouseClicked(evt);
            }
        });
        jPanel1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jPanel1MouseDragged(evt);
            }
        });

        jCheckBox1.setText("drag lock");
        jPanel1.add(jCheckBox1);

        jButton1.setText("reset");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1);

        add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Exit the Application
     */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit(0);
    }//GEN-LAST:event_exitForm

    private void jPanel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseClicked
        PointView hit = null;
        for (PointView view : views) {
            if (view.click(evt.getPoint())) {
                hit = view;
            }
        }
        if (hit == null) {
            if (evt.getButton() == MouseEvent.BUTTON1) {
                addPoint(evt.getPoint());
            }
        } else {
            if (evt.getButton() == MouseEvent.BUTTON3) {
                removePoint(hit);
            }
        }
        repaint();
    }//GEN-LAST:event_jPanel1MouseClicked

    private void jPanel1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MousePressed
        PointView hit = null;
        for (PointView view : views) {
            if (view.click(evt.getPoint())) {
                hit = view;
            }
        }
        if (hit != null) {
            dragPoint = hit.point;
        }
    }//GEN-LAST:event_jPanel1MousePressed

    private void jPanel1MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseDragged
        if (dragPoint != null) {
            if (this.jCheckBox1.isSelected()) {
                Vector beforeDragPoint = null;
                for (int i = 0; i < points.size(); i++) {
                    if (points.get(i) == dragPoint && i > 0) {
                        beforeDragPoint = points.get(i - 1);
                    }
                }

                if (beforeDragPoint != null) {
                    Vector dir = dragPoint.subtract(beforeDragPoint);
                    double ratio = dir.y() / dir.x();
                    int x = evt.getX();
                    int y = (int) (ratio * x);
                    System.out.println("x" + x);
                    System.out.println("y" + y);
                    System.out.println("ratio:" + ratio);
                    dragPoint.replace(new Vector(x, y));
                }
            } else {
                dragPoint.replace(new Vector(evt.getPoint()));
            }
            repaint();
        }
    }//GEN-LAST:event_jPanel1MouseDragged

    private void jPanel1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseReleased
        this.dragPoint = null;
    }//GEN-LAST:event_jPanel1MouseReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        points = new ArrayList<>();
        views = new HashSet();
        c = null;
        dragPoint = null;
        repaint();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void paintMainPanel(JPanel panel, Graphics g) {
        for (Vector p : points) {
            g.drawOval((int) p.x(), (int) p.y(), 1, 1);
        }
        if (c != null) {
            final int N = 1000;
            Vector from, to;
            for (int i = 0; i < N - 1; i++) {
                from = c.getPoint((double) i / N);
                to = c.getPoint((double) (i + 1) / N);
                g.drawLine((int) from.x(), (int) from.y(), (int) to.x(), (int) to.y());
            }
        }
    }

    private void addPoint(Point point) {
        Vector v = new Vector(point);
        points.add(v);
        if (points.size() > (CUBIC ? 3 : 2)) {
            c = CUBIC ? new CubicBezierCurve(points.toArray(new Vector[0]))
                    : new QuadraticBezierCurve(points.toArray(new Vector[0]));
        }
        views.add(new PointView(v));
    }

    private void removePoint(PointView view) {
        points.remove(view.point);
        views.remove(view);
        if (points.size() <= (CUBIC ? 3 : 2)) {
            c = null;
        }
    }

    /**
     * Interface that represents a curve.
     */
    public interface Curve {

        /**
         * Converts a given parameter into a point on the curve.
         *
         * @param t A parameter in the range 0 to 1.
         * @return A vector representing the point resulting from the
         * conversion.
         */
        public Vector getPoint(double t);

        /**
         * Returns the tangent of a given parameter with the curve.
         *
         * @param t A parameter in the range 0 to 1.
         * @return A vector representing the tangent at getPoint(t).
         */
        public Vector getTangent(double t);

        /**
         * Returns the normal of a given parameter with the curve.
         *
         * @param t A parameter in the range 0 to 1.
         * @return A vector representing the normal between t and the curve.
         */
        public Vector getNormalVector(double t);
    }

    /**
     * Implementation of Curve that models a Bezier curve.
     *
     * This class will use N control points to model Bezier segments. Each
     * segment is described by P[i], P[i+1], P[i+2], P[i+3] where i is a
     * multiple of 3. This means that segments are linked together. The last
     * control point does not need to be the same as the first control point.
     *
     * The available range [0,1] is split up equally between each segment. This
     * means that each sequence, independent of size, will be drawn with the
     * same precision. It also means that speed of the curve can vary.
     *
     * Points that do not form a full segment will be ignored. For example, if 5
     * points are specified, the first four will be used to form a segment and
     * the last will be ignored.
     */
    public static class CubicBezierCurve implements Curve {

        final private Vector[] P; // control points defining Bezier segments
        int N; // the number of segments

        /**
         * Constructs a Bezier curve from the specified control points.
         *
         * @param points control points
         */
        public CubicBezierCurve(Vector... points) {
            this.P = points;
            N = (points.length - 1) / 3;
        }

        @Override
        public Vector getPoint(double t) {
            t = t % 1; // normalize t to range [0,1]
            /*
             * For each segment, we need to find a value to fill into
             * the function. For this, we simply multiply by the number of
             * segments, and then normalize. This is a way to cover all segments.
             */
            double s = (t * N) % 1; // value to fill into the Bezier function
            /*
             * From t, we can derive in which segment the point should lie.
             * For this, we simply multiply by the number of segments and then
             * round half down.
             */
            int i = 3 * (int) (t * N); // the first point of the segment
            return getCubicBezierPnt(s, P[i], P[i + 1], P[i + 2], P[i + 3]);
        }

        @Override
        public Vector getTangent(double t) {
            t = t % 1; // normalize t to range [0,1]
            double s = (t * N) % 1; // value to fill into the Bezier function
            int i = 3 * (int) (t * N); // the first point of the segment
            return getCubicBezierTng(s, P[i], P[i + 1], P[i + 2], P[i + 3]);
        }

        @Override
        public Vector getNormalVector(double t) {
            Vector tangent = this.getTangent(t);
            // Rotate 90 degrees in negative direction (outward) in XOY plane.
            return new Vector(-tangent.y(), tangent.x(), 0);
        }

        /**
         * Calculates a point on a Bezier segment defined by P0, P1, P2, P3.
         *
         * @param t the parameter of the function (in range [0,1])
         * @param P0 first control point
         * @param P1 second control point
         * @param P2 third control point
         * @param P3 fourth control point
         * @return point on the curve for parameter t
         */
        public static Vector getCubicBezierPnt(double t, Vector P0, Vector P1,
                Vector P2, Vector P3) {
            return P0.scale(pow(1 - t, 3)).add(
                    P1.scale(3 * t * pow(1 - t, 2))).add(
                    P2.scale(3 * pow(t, 2) * (1 - t))).add(
                    P3.scale(pow(t, 3)));

        }

        /**
         * Calculates a tangent on a Bezier segment defined by P0, P1, P2, P3.
         *
         * @param t the parameter of the function (in range [0,1])
         * @param P0 first control point
         * @param P1 second control point
         * @param P2 third control point
         * @param P3 fourth control point
         * @return tangent of the curve on point for parameter t
         */
        public static Vector getCubicBezierTng(double t, Vector P0, Vector P1,
                Vector P2, Vector P3) {
            return P1.subtract(P0).scale(pow(1 - t, 2)).add(
                    P2.subtract(P1).scale(2 * t * (1 - t))).add(
                    P3.subtract(P2).scale(pow(t, 2))).scale(3);
        }
    }

    public static class QuadraticBezierCurve implements Curve {

        final private Vector[] P; // control points defining Bezier segments
        int N; // the number of segments

        /**
         * Constructs a Bezier curve from the specified control points.
         *
         * @param points control points
         */
        public QuadraticBezierCurve(Vector... points) {
            this.P = points;
            N = (points.length - 1) / 2;
        }

        @Override
        public Vector getPoint(double t) {
            t = t % 1; // normalize t to range [0,1]
            /*
             * For each segment, we need to find a value to fill into
             * the function. For this, we simply multiply by the number of
             * segments, and then normalize. This is a way to cover all segments.
             */
            double s = (t * N) % 1; // value to fill into the Bezier function
            /*
             * From t, we can derive in which segment the point should lie.
             * For this, we simply multiply by the number of segments and then
             * round half down.
             */
            int i = 2 * (int) (t * N); // the first point of the segment
            return getQuadraticBezierPnt(s, P[i], P[i + 1], P[i + 2]);
        }

        @Override
        public Vector getTangent(double t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Vector getNormalVector(double t) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public static Vector getQuadraticBezierPnt(double t, Vector P0,
                Vector P1, Vector P2) {
            return P0.scale(pow(1 - t, 2)).add(
                    P1.scale(2 * (1 - t) * t)).add(
                    P2.scale(pow(t, 2)));
        }
    }

    public static class Vector {

        private double[] coordinates;

        public Vector(double... coordinates) {
            this.coordinates = coordinates;
        }

        private Vector(Point point) {
            this.coordinates = new double[2];
            this.coordinates[0] = point.x;
            this.coordinates[1] = point.y;
        }

        public double x() {
            return coordinates[0];
        }

        public double y() {
            return coordinates[1];
        }

        public double z() {
            return coordinates[2];
        }

        public double getCoordinate(int i) {
            return coordinates[i];
        }

        private Vector scale(double factor) {
            double[] result = new double[coordinates.length];
            for (int i = 0; i < this.coordinates.length; i++) {
                result[i] = this.getCoordinate(i) * factor;
            }
            return new Vector(result);
        }

        private Vector add(Vector that) {
            double[] result = new double[coordinates.length];
            for (int i = 0; i < this.coordinates.length; i++) {
                result[i] = this.getCoordinate(i) + that.getCoordinate(i);
            }
            return new Vector(result);
        }

        private Vector subtract(Vector that) {
            return this.add(that.scale(-1));
        }

        @Override
        public String toString() {
            String result = "(";
            for (int i = 0; i < coordinates.length - 1; i++) {
                result = result.concat(coordinates[i] + ", ");
            }
            result = result.concat(coordinates[coordinates.length - 1] + ")");
            return result;
        }

        public void replace(Vector v) {
            this.coordinates = v.coordinates;
        }
    }

    public class PointView {

        Vector point;
        final double eps = 5;

        public PointView(Vector point) {
            this.point = point;
        }

        public boolean click(Point clickPoint) {
            int x = clickPoint.x;
            int y = clickPoint.y;
            return (abs(point.x() - x) < eps) && (abs(point.y() - y) < eps);
        }

        @Override
        public String toString() {
            return point.toString();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GUI().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
