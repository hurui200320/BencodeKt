package info.skyblond.bencode.encoder

import info.skyblond.bencode.BEntry
import java.io.Writer

/**
 * A basic bencode encoder which accept [BEntry].
 *
 * Thread safe as lock on [writer] when writing.
 *
 * @see [BencodeWriter] for general object.
 * */
class BencodeEncoder(
    private val writer: Writer,
) {
    // To ease the "Type checking has run into a recursive problem"
    private fun writeBInteger(entry: BEntry.BInteger) {
        writer.write("i")
        writer.write(entry.value.toString(10))
        writer.write("e")
    }

    private fun writeBString(entry: BEntry.BString) {
        writer.write(entry.value.length.toString())
        writer.write(":")
        writer.write(entry.value)
    }

    private fun writeBList(entry: BEntry.BList) {
        writer.write("l")
        entry.value.forEach { writeEntry(it) }
        writer.write("e")
    }

    private fun writeBMap(entry: BEntry.BMap) {
        writer.write("d")
        entry.value.keys.sorted().forEach { k ->
            writeEntry(BEntry.BString(k))
            writeEntry(entry.value[k]!!.get())
        }
        writer.write("e")
    }

    /**
     * Write a [entry] to the [writer].
     * */
    fun writeEntry(entry: BEntry) = synchronized(writer) {
        when (entry) {
            is BEntry.BInteger -> writeBInteger(entry)
            is BEntry.BString -> writeBString(entry)
            is BEntry.BList -> writeBList(entry)
            is BEntry.BMap -> writeBMap(entry)
        }
    }
}
