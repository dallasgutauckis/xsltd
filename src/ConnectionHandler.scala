import java.io._
import java.net.{ InetAddress, ServerSocket, Socket, SocketException }
import java.util.Random

case class ConnectionHandler(socket: Socket) extends Thread("ConnectionHandler") {
  val handler = new MemcacheCommandHandler
  val parser = new MemcacheParser(handler)

  def log(line: String) = Log.v("ConnectionHandler", line)

  val BufferSize = 10240

  def bufferedReader(s: Socket) = new BufferedReader(new InputStreamReader(s.getInputStream))
  def bufferedOutput(s: Socket) = new BufferedOutputStream(s.getOutputStream, BufferSize)

  override def run(): Unit = {
    val out = bufferedOutput(socket)
    val in = bufferedReader(socket)

    try {
      parser.handle(in, out)
    } catch {
      case e: Exception ⇒
        e.printStackTrace()
    }

    out.close()
    in.close()
    socket.close()
  }
}