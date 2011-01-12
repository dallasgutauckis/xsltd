// for XSLT transformations
import javax.xml.transform._;
import java.net._;
import java.io._;

/**
 * Object that handles all XSLT transformations
 * 
 * @author Dallas Gutauckis <dallas@gutauckis.com>
 * @since 2010-11-17 20:39:40 EDT
 */
object XSLTParser {
  private val MAX_EXPIRATION_DURATION = 2592000 // 30 days, as memcache protocol specifies

  private val transformationFactory = TransformerFactory.newInstance
  private var transformers = new collection.mutable.ListMap[String, Transformer]
  private var xmlStreams = new collection.mutable.ListMap[String, stream.StreamSource]

  def log(line: String) = Log.v("XSLTParser", line)
  
  def hasXslDocument(xslId: String) : Boolean = transformers.contains(xslId)
  def hasXmlDocument(xmlId: String) : Boolean = { log(xmlStreams.size.toString); xmlStreams.contains(xmlId) }
  
  /**
   * transform the given documents
   */
  def transform(xsltId: String, xmlId: String): String = {
    try {
      val tmpTransformer = transformers.get(xsltId)
      if (tmpTransformer.isEmpty) {
        //throw new XSLTDocumentMissingException;
        return ""
      }
      val transformer = tmpTransformer.get.asInstanceOf[Transformer]

      val tmpXmlStream = xmlStreams.get(xmlId)
      if (tmpXmlStream.isEmpty) {
        //throw new XMLDocumentMissingException
        return ""
      }
      val xmlStream = tmpXmlStream.get.asInstanceOf[stream.StreamSource]
      
      // Reset the stream in case we're reusing it
      xmlStream.getInputStream.reset
      
      val outputStream = new stream.StreamResult(new ByteArrayOutputStream)
      
      transformer.transform(xmlStream, outputStream)
      
      return outputStream.getOutputStream.toString
    } catch {
      case e: Exception ⇒ e.printStackTrace()
    }

    ""
  }

  def setXsltDocument(xsltId: String, xslt: String) : Boolean = {
    val transformer = transformationFactory.newTransformer(new stream.StreamSource(new StringBufferInputStream(xslt)))
    transformers.put(xsltId, transformer)
    true
  }

  def setXmlDocument(xmlId: String, xml: String, expiration: Long) : Boolean = {
    var expirationTimestamp = expiration
    if (expiration <= MAX_EXPIRATION_DURATION) {
      // Not sure yet how to handle expiration
      val d = new java.util.Date
      expirationTimestamp += d.getTime / 1000
    }

    val bais = new ByteArrayInputStream(xml.getBytes("UTF-8"))
    
    val xmlStream = new stream.StreamSource(bais)
    xmlStreams.put(xmlId, xmlStream)
    true
  }  
}