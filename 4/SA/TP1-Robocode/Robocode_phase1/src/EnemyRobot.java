import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class EnemyRobot {
    private double x;
    private double y;
    private String name;
    private  double compensation;

    public EnemyRobot(double x, double y, String name, double extra) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.compensation = extra;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCompensation() {
        return compensation;
    }

    public void setCompensation(double compensation) {
        this.compensation = compensation;
    }

    public List<Point2D> getVertexPoints(double size){
        List<Point2D> pos = new ArrayList<>();
        pos.add(new Point2D.Double(x - size, y - size));
        pos.add(new Point2D.Double(x - size, y));
        pos.add(new Point2D.Double(x - size, y + size));
        pos.add(new Point2D.Double(x, y + size));
        pos.add(new Point2D.Double(x + size, y + size));
        pos.add(new Point2D.Double(x + size, y));
        pos.add(new Point2D.Double(x + size, y - size));
        pos.add(new Point2D.Double(x, y - size));
        return pos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnemyRobot that = (EnemyRobot) o;
        return name.equals(that.name);
    }

    @Override
    public String toString() {
        return "EnemyRobot{" +
                "x=" + x +
                ", y=" + y +
                ", name='" + name + '\'' +
                ", compensation=" + compensation +
                '}';
    }
}
