package info.skyblond.bencode.encoder

import info.skyblond.bencode.BEntry
import java.io.Writer
import java.math.BigInteger
import java.util.function.Supplier

class BencodeWriter(
    private val writer: Writer,
    private val mapper: List<BencodeEncoderMapper<*>> = emptyList()
): AutoCloseable {
    private val encoder = BencodeEncoder(writer)

    @Suppress("UNCHECKED_CAST")
    private fun mapToBEntry(obj: Any): BEntry = when (obj) {
        is BEntry -> obj
        is BencodeEncodable -> obj.encodeToBEntry()
        // for signed value, convert to long
        is Byte -> BEntry.BInteger(obj.toLong().toBigInteger())
        is Short -> BEntry.BInteger(obj.toLong().toBigInteger())
        is Int -> BEntry.BInteger(obj.toLong().toBigInteger())
        is Long -> BEntry.BInteger(obj.toBigInteger())
        // for unsigned value, convert to long so the value is same
        is UByte -> BEntry.BInteger(obj.toLong().toBigInteger())
        is UShort -> BEntry.BInteger(obj.toLong().toBigInteger())
        is UInt -> BEntry.BInteger(obj.toLong().toBigInteger())
        // for ULong, it's bigger than long, thus turn it into string
        is ULong -> BEntry.BInteger(BigInteger(obj.toString()))
        // for chat and string, just write string
        is Char -> BEntry.BString(obj + "")
        is String -> BEntry.BString(obj)
        // for list, transform it to sequence
        is List<*> -> BEntry.BList(obj.asSequence().map { mapToBEntry(it!!) })
        // for map, map the key to string and map value to BEntry
        is Map<*, *> -> BEntry.BMap(obj.mapKeys {
            if (it.key is String) it.key as String else it.key.toString()
        }.mapValues { { mapToBEntry(it.value!!) } })
        // for general object, search mapper and apply the transform
        else -> ((mapper.find { it.canHandle(obj) }
            ?: error("Mapper not found: ${obj.javaClass}"))
                as BencodeEncoderMapper<Any>)
            .invoke(obj)
            .let { if (it is BEntry) it else mapToBEntry(it) }
    }

    fun write(obj: Any): Unit = encoder.writeEntry(mapToBEntry(obj))

    fun flush() = writer.flush()

    override fun close() = writer.close()
}
