import java.text.DateFormat
import java.util.Date

object Log {
  val IS_DEBUGGING = false;
  
  def v(tag: String, line: String) = {
    if (IS_DEBUGGING) {
      var d = new Date
      
      var tmpLine = line
      if (tmpLine.length > 256)
      {
        tmpLine = tmpLine.substring(0, 256) + "..."
      }
      
      tmpLine = tmpLine.replace("\r", "")
      tmpLine = tmpLine.replace("\n", "")
      
      println(tag + "/" + ( d.getTime / 1000 ) + ": " + tmpLine) 
    }
  }
}