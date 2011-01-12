import java.net.InetAddress

object XSLTD { 
  def main(args: Array[String]) {
    if (args.size < 2) {
      return
    }

    val host = InetAddress.getByName(args(0))
    val port: Int = args(1).toInt
    
    Daemon.listen(host, port)
  }
}