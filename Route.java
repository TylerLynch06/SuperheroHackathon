import java.util.Arrays;

public class Route {
    private double[][] points;

    public Route(double[][] points) {
        this.points = points;
    }

    public double[][] getCoordinates() {
        return points;
    }

    public void printPoints() {
        for (double[] point : points) {
            System.out.println(Arrays.toString(point));
        }
    }
}
