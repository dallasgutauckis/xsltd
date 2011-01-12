import java.io._

object SocketReader {
  def log(line: String) = Log.v("SocketReader", line)
  
  def readFor(callee: String)(reader: BufferedReader, bytes: Int) = {
    var i = 0
    var sb = new StringBuilder(bytes)
    
    for (i ← 0 to bytes - 1) {
      sb.append(reader.read.toChar)
    }
    
    val input = sb.toString
    
    log(input)
    input
  }
  
  def readTo(callee: String)(reader: BufferedReader, to: Array[Int]) = {
    var ascii = 0
    var input = ""
    
    do
    {
      ascii = reader.read
      input += ascii.toChar
    } while (!to.contains(ascii))
    
    log(input)
    input
  }
  
  def writeLine(callee: String)(out: OutputStream)(line: String) = {
    log("Writing: " + line)
    out.write((line + "\r\n").getBytes("UTF-8"))
    out.flush
  }
}