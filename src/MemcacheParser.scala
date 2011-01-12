import java.io._

class MemcacheParser(handler: MemcacheCommandHandler) {
  val COMMAND_GET = "get";
  val COMMAND_SET = "set";
  val COMMAND_REPLACE = "replace";
  val COMMAND_CAS = "cas";
  val COMMAND_DELETE = "delete";

  val validCommands = Array(COMMAND_GET, COMMAND_SET, COMMAND_DELETE);

  def readFor(reader: BufferedReader)(bytes: Int) = SocketReader.readFor("MemcacheParser")(reader, bytes)
  def readTo(reader: BufferedReader)(to: Array[Int]) = SocketReader.readTo("MemcacheParser")(reader, to)
  def log(line: String) = Log.v("MemcacheParser", line)

  // We'll handle the commands right here
  def handle(in: BufferedReader, out: BufferedOutputStream) {
    var line = ""
    var command = ""
    val readFor = this.readFor(in) _
    val readTo = this.readTo(in) _
    
    val readToBytes = new Array[Int](2)
    readToBytes(0) = 32
    readToBytes(1) = -1

    do {
      log("do")
      // We need to do all of this continually until we get a closed socket

      command = readTo(readToBytes)

      if (!command.endsWith((-1).toChar.toString)) {
        log(command)

        command = command.trim

        command match {
          case COMMAND_GET ⇒ commandTypeHandler_RETRIEVAL(command, in, out)
          case COMMAND_SET | COMMAND_REPLACE | COMMAND_CAS ⇒ commandTypeHandler_STORAGE(command, in, out)
          case _ ⇒ //log("Unknown type discovered: '" + command + "'")
        }
        log("end do")
      }
      
    } while (!command.endsWith((-1).toChar.toString))
  }

  def commandTypeHandler_RETRIEVAL(command: String, in: BufferedReader, out: BufferedOutputStream) {
    
    val writeLine = SocketReader.writeLine("MemcacheParser")(out) _
    
    var ascii = 0;
    var key = "";
    do {
      ascii = in.read()
      key += ascii.toChar
    } while (ascii != 13)

    key = key.trim

    val getResponse = (handler.get(key))
    var response = ""
    if (!getResponse.equals("")) {
      response = response + "VALUE " + key + " 0 " + getResponse.length + "\r\n" + getResponse + "\r\nEND"
    } else {
      response = response + "END"
    }

    log("Responding: '" + response + "'")

    writeLine(response)
  }

  def commandTypeHandler_STORAGE(command: String, in: BufferedReader, out: BufferedOutputStream) {
    val readFor = this.readFor(in) _
    val readTo = this.readTo(in) _
    val writeLine = SocketReader.writeLine("MemcacheParser")(out) _

    val readLineBytes = new Array[Int](1)
    readLineBytes(0) = 13

    var ascii = 0
    val line = readTo(readLineBytes)

    val parts = line.split(" ", 5)
    val partSize = parts.size

    if (partSize < 4) {
      writeLine("ERROR")
    }

    log(line)

    val key = parts(0)
    val flags = parts(1).toInt;
    val expiration = parts(2).toInt;
    val bytes = parts(3).trim.toInt;

    // discard! it's extra newline stuff (13|10)
    readFor(1)

    // And, what to store...
    val input = readFor(bytes)
    log(input)

    if (command == "set") {
      val response = (if (handler.set(key, input, expiration)) "STORED" else "NOT_STORED")
      writeLine(response)
    }
  }
}