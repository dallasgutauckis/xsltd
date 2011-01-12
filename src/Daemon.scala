import java.net.{InetAddress,ServerSocket,Socket,SocketException}
import java.io._

object Daemon
{
  def listen(address: InetAddress, port: Int)
  {
    try {
      // Begin listening on host:port
      val listener = new ServerSocket(port, 1000, address)
      listener.setReceiveBufferSize(1024)
      
      while (true) {
        // Continually accept new connections and send them to a new thread
        ConnectionHandler(listener.accept()).start()
      }
      
      listener.close()
    }
    catch {
      case e: IOException =>
        System.err.println("Daemon failed to listen on " + address.getHostAddress() + ":" + port)
    }
  }
}