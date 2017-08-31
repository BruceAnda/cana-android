package cn.ac.ict.cana.newversion.utils

import java.io.BufferedOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.GZIPOutputStream

/**
 * Created by zhaoliang on 2017/8/21.
 */
object GzipUtil {

    /**
     * 使用Gzip压缩文件
     */
    fun compressForZip(unZipFile: String, zipFile: String) {
        val inputStream = FileInputStream(unZipFile)
        val outputStream = BufferedOutputStream(GZIPOutputStream(FileOutputStream(zipFile)))
        var len: Int
        var b = ByteArray(1024)
        len = inputStream.read(b)
        while (len != -1) {
            outputStream.write(b)
            len = inputStream.read(b)
        }
        inputStream.close()
        outputStream.close()
    }
}