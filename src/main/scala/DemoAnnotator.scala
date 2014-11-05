package annotator

import java.io.File
import org.jdom2.Content
import org.jdom2.util.IteratorIterable
import scala.collection.immutable.Queue
import scala.collection.JavaConversions.iterableAsScalaIterable 

import scala.collection.immutable.IntMap
import org.jdom2.input.SAXBuilder
import org.jdom2.filter.ElementFilter
import org.jdom2.Element
import org.jdom2.Document
import org.jdom2.util.IteratorIterable

object DemoAnnotator {
  import Annotator._

  def main(args: Array[String]): Unit = {
    val filePath = args(0)
    val builder = new SAXBuilder()
    val dom = builder.build(new File(filePath)) 
    val annotator = LineAnnotator.addLineAnnotation(new Annotator(dom))

    val table = (annotator.getBIndexList(LineAnnotator.lineAnnoType).map {
      case (blockIndex, charIndex) =>
        val segment = annotator.getSegment(LineAnnotator.lineAnnoType)(blockIndex, charIndex)
        val blockBIndex = segment.firstKey
        val charBIndex = segment(blockBIndex).firstKey
        val blockLIndex = segment.lastKey
        val charLIndex = segment(segment.lastKey).lastKey

        val textMap = annotator.getTextMapInRange(
            blockBIndex, 
            charBIndex,
            blockLIndex,
            charLIndex
        )

        val lineText = textMap.values.mkString("")
        println(lineText)
        println(lineText.size)
        (blockIndex -> charIndex) -> (
          if (lineText.size < 20) Some(B)
          else if (lineText.size < 50) Some(I)
          else if (lineText.size < 70) Some(O)
          else if (lineText.size < 90) Some(U)
          else Some(L)
        )
    }).toMap

    val refAnnoType = AnnoType("demo", 'd')


    annotator.annotateAnnoType(LineAnnotator.lineAnnoType, refAnnoType, (blockIndex, charIndex) => {
      table(blockIndex -> charIndex)
    }).write("/home/thomas/out.svg")
  }

}
