class MemcacheCommandHandler() {
  val DOCUMENT_TYPE_XSL = "xsl"
  val DOCUMENT_TYPE_XML = "xml"
  val DELIMITER = ":"
    
  val COMMAND_GET_XML = DOCUMENT_TYPE_XML
  val COMMAND_GET_XSL = DOCUMENT_TYPE_XSL
  val COMMAND_GET_PARSE = "parse"

  def log(line: String) = Log.v("MemcacheCommandHandler", line)
  
  def set(key: String, value: String, expiration: Long): Boolean = {
    return key.substring(0, 3) match {
      case DOCUMENT_TYPE_XSL ⇒ XSLTParser.setXsltDocument(key.substring(4), value)
      // XML expires after expiration seconds
      case DOCUMENT_TYPE_XML ⇒ XSLTParser.setXmlDocument(key.substring(4), value, expiration)
      case _ ⇒ false
    }
  }

  def get(key: String): String = {
    var parts = key.split(DELIMITER, 2)
    val command = parts(0)
    
    command match {
      case COMMAND_GET_XSL => hasXslDocument(parts(1))
      case COMMAND_GET_XML => hasXmlDocument(parts(1))
      case COMMAND_GET_PARSE => parse(parts(1))
    }
  }
  
  private def hasXslDocument(hash: String) = (if (XSLTParser.hasXslDocument(hash)) "1" else "0")
  private def hasXmlDocument(hash: String) = (if (XSLTParser.hasXmlDocument(hash)) "1" else "0")
  
  private def parse(key: String): String =
  {
    var parts = key.split(DELIMITER, 2)
    if (parts.length != 2 || parts.forall(_.length != 32))
    {
      return ""
    }
    
    val xslId = parts(0)
    val xmlId = parts(1)
    
    // break up key into what the client wants (XSLId:XMLId)
    XSLTParser.transform(xslId, xmlId)
  }

  def replace(key: String, value: String, expiration: Long) = ""
  def cas(key: String, value: String, expiration: Long, previousHash: String) = ""
  def delete(key: String) = ""
}