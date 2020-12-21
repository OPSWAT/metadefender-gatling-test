
import java.io.{File, FileNotFoundException}


class LocalFiles(dir:String) {
    private def recursiveListFiles(f: File): Array[File] = {
        val these = f.listFiles
        these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
    }
    val feeder = Iterator.continually(
        recursiveListFiles(new File(dir)).filter(!_.isDirectory()).map(f => Map("filename" -> f.getName, "filepath" -> f.getPath))
    ).flatten

}
