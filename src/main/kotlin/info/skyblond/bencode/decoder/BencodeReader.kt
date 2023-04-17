package info.skyblond.bencode.decoder

import info.skyblond.bencode.BEntry
import info.skyblond.bencode.BEntryType
import java.io.Reader
import java.math.BigInteger
import java.util.function.BiConsumer
import java.util.function.Consumer

/**
 * Read bencode encoded char stream. Operated in a DOM-ish way.
 * */
class BencodeReader(
    reader: Reader
) {
    private val decoder = BencodeDecoder(reader)

    /**
     * If there is more char to process.
     * */
    fun hasNext(): Boolean = decoder.hasNext()

    /**
     * Return the next possible entry type.
     *
     * + [BEntry.BInteger]: The next one is an integer.
     * + [BEntry.BString]: The next one is a string.
     * + [BEntry.BList]: The next one is a list.
     * + [BEntry.BMap]: The next one is a map.
     * */
    fun nextType(): BEntryType {
        return when (decoder.nextType()) {
            BEntry.BInteger -> BEntry.BInteger
            BEntry.BString -> BEntry.BString
            BEntry.BList -> BEntry.BList
            BEntry.BMap -> BEntry.BMap
            else -> error("Impossible type: ${decoder.nextType()}")
        }
    }

    /**
     * Read an integer from the reader.
     * */
    fun readInteger(): BigInteger = decoder.readInteger()

    /**
     * Read a string from the reader.
     * */
    fun readString(): String = decoder.readString()

    /**
     * Read a list from the reader.
     *
     * The element of the list can be:
     * + [BigInteger]: integer
     * + [String]: string
     * + [List]: list
     * + [Map]: map, the key is string.
     * */
    fun readList(): List<*> {
        decoder.startList()
        val list = mutableListOf<Any>()
        while (decoder.hasNext()) {
            when (decoder.nextType()) {
                BEntryType.EntityEnd -> break // this is the end of the list
                BEntry.BInteger -> list.add(readInteger())
                BEntry.BString -> list.add(readString())
                BEntry.BList -> list.add(readList())
                BEntry.BMap -> list.add(readMap())
            }
        }
        decoder.endEntity()
        return list
    }

    /**
     * Iterate through a list from the reader.
     * Only iterate the top-level list.
     *
     * Useful when there is a huge list of small things.
     *
     * The element of the list can be:
     * + [BigInteger]: integer
     * + [String]: string
     * + [List]: list
     * + [Map]: map, the key is string.
     * */
    fun iterateList(
        callback: Consumer<Any>
    ) {
        decoder.startList()
        while (decoder.hasNext()) {
            when (decoder.nextType()) {
                BEntryType.EntityEnd -> break // this is the end of the list
                BEntry.BInteger -> callback.accept(readInteger())
                BEntry.BString -> callback.accept(readString())
                BEntry.BList -> callback.accept(readList())
                BEntry.BMap -> callback.accept(readMap())
            }
        }
        decoder.endEntity()
    }

    /**
     * Read a map from the reader.
     * The key is always string.
     *
     * The value can be:
     * + [BigInteger]: integer
     * + [String]: string
     * + [List]: list
     * + [Map]: map, the key is string.
     * */
    fun readMap(): Map<String, *> {
        decoder.startMap()
        val map = mutableMapOf<String, Any>()
        while (decoder.hasNext() && decoder.nextType() != BEntryType.EntityEnd) {
            val key = readString()
            require(decoder.hasNext()) { "Invalid map: only has key, no value" }
            when (val t = decoder.nextType()) {
                BEntry.BInteger -> map[key] = readInteger()
                BEntry.BString -> map[key] = readString()
                BEntry.BList -> map[key] = readList()
                BEntry.BMap -> map[key] = readMap()
                else -> error("Invalid next type: $t")
            }
        }
        decoder.endEntity()
        return map
    }

    /**
     * Iterate through a map from the reader.
     * Only iterate the top-level map.
     *
     * Useful when there is a huge map of small things.
     *
     * The key is always string.
     * The value can be:
     * + [BigInteger]: integer
     * + [String]: string
     * + [List]: list
     * + [Map]: map, the key is string.
     * */
    fun iterateMap(
        callback: BiConsumer<String, Any>
    ) {
        decoder.startMap()
        while (decoder.hasNext() && decoder.nextType() != BEntryType.EntityEnd) {
            val key = readString()
            require(decoder.hasNext()) { "Invalid map: only has key, no value" }
            when (val t = decoder.nextType()) {
                BEntry.BInteger -> callback.accept(key, readInteger())
                BEntry.BString -> callback.accept(key, readString())
                BEntry.BList -> callback.accept(key, readList())
                BEntry.BMap -> callback.accept(key, readMap())
                else -> error("Invalid next type: $t")
            }
        }
        decoder.endEntity()
    }
}
