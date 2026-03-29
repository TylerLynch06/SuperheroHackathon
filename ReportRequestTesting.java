import java.util.Set;

public class ReportRequestTesting {
    public static void main(String[] args) {
        
        //start handler
        CrimeReportHandler reportHandler = new CrimeReportHandler();


        //report an incident
        CrimeReport incident = new CrimeReport(
            51.51321,
            -0.097745,
            "theft"
        );
        reportHandler.reportCrime(incident);

        //get Set of all reported crimes within last 30mins
        Set<CrimeReport> crimeReports = reportHandler.getRecentCrimeReports();
        for (CrimeReport cr: crimeReports) {
            System.out.println(cr.getType());
            System.out.println(cr.getLatitude());
            System.out.println(cr.getLongitude());
            System.out.println(DateTimeTools.dateToString(cr.getTimestamp()));
        }
        
        
        AssistanceRequestHandler requestHandler = new AssistanceRequestHandler();
        
        AssistanceRequest incident2 = new AssistanceRequest(
            51.51321,
            -0.097745,
            "i am stuck in john honey computer lab!"
        );
        requestHandler.requestAssistance(incident2);

        
        
        Set<AssistanceRequest> assistanceRequests = requestHandler.getRecentAssistanceRequests();

        for (AssistanceRequest ar: assistanceRequests) {
            System.out.println("Description: " + ar.getDescription());
            System.out.println("Happened at: " + DateTimeTools.dateToString(ar.getTimestamp()));
        }
        
    }
}