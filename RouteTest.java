public class RouteTest {
    
    public static void main(String[] args) {
        double startLon = -0.1276, startLat = 51.5074;
        double endLon = -2.2426,   endLat = 53.4808;

        Router router = new Router();

        Route route = router.createRoute(startLon, startLat, endLon, endLat);
        System.out.println("Starting print");
        route.printPoints();
    }
}
