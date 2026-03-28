import java.net.*;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.net.SocketException;
import java.io.IOException;

public class Connect {

    private NetworkInterface networkInt;
    private int portNum;
    private String networkAddr;
    private InetAddress ipAddr;
    private InetSocketAddress socketAddr;
    private MulticastSocket multicastSocket;

    public Connect() {
        portNum = 11007;
        networkAddr = "ff02::1";

        NetworkInterface network;

        try {
            //to make sure the network supports multicast before joining
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                network = interfaces.nextElement();
                if (network.supportsMulticast()) {
                    networkInt = network; 
                }
            }
        } catch (SocketException e) {
            System.out.println(e);
        }

        //joining network
        try {
            ipAddr = InetAddress.getByName(networkAddr);
            socketAddr = new InetSocketAddress(ipAddr, portNum);
            multicastSocket = new MulticastSocket(portNum);

            multicastSocket.setNetworkInterface(networkInt);
            multicastSocket.joinGroup(socketAddr, networkInt);
            multicastSocket.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, true);

        } catch (UnknownHostException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    //get methods to access private fields
    public InetSocketAddress getSocketAddr() {
        return socketAddr;
    }

    public MulticastSocket getMulticastSocket() {
        return multicastSocket;
    }

    public NetworkInterface getNetworkInt() {
        return networkInt;
    }
            
}