package info.skyblond.bencode

import info.skyblond.bencode.decoder.BencodeReader
import info.skyblond.bencode.encoder.BencodeWriter
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import kotlin.random.Random
import kotlin.test.assertContentEquals

class BinaryTest {
    @Test
    fun test() {
        val bytes = Random.nextBytes(1024)
        val str = String(bytes, Charsets.ISO_8859_1)

        val outputBytes = ByteArrayOutputStream()
        val writer = BencodeWriter(outputBytes.writer(Charsets.ISO_8859_1))
        writer.write(BEntry.BString(str))
        writer.flush()
        println(outputBytes.toByteArray().contentToString())

        val inputBytes = outputBytes.toByteArray().inputStream()
        val reader = BencodeReader(inputBytes.reader(Charsets.ISO_8859_1))
        val strRead = reader.readString()
        assertContentEquals(bytes, strRead.toByteArray(Charsets.ISO_8859_1))
    }
}
