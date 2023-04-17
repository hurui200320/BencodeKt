package info.skyblond.bencode.decoder

import info.skyblond.bencode.BEntry
import info.skyblond.bencode.BEntryType
import java.io.Reader
import java.math.BigInteger

/**
 * A basic bencode decoder which return [BEntry].
 *
 * Thread safe as lock on [reader] when reading.
 *
 * @see [BencodeReader] for general object.
 * */
class BencodeDecoder(
    private val reader: Reader
) {
    @Volatile
    private var c: Int = -1

    init {
        hasNext()
    }

    /**
     * Return true if reader has chars to offer.
     * */
    fun hasNext(): Boolean {
        if (c != -1) return true
        do { // skip whitespace, like \n \r, etc.
            c = reader.read()
        } while (c.toChar().isWhitespace() && c != -1)
        return c != -1
    }

    private fun loadNextChar() {
        c = -1
        hasNext()
    }

    /**
     * Decide the next entry type based on the first char.
     *
     * + [BEntry.BInteger]: The next one is an integer.
     * + [BEntry.BString]: The next one is a string.
     * + [BEntry.BList]: The next one is a list.
     * + [BEntry.BMap]: The next one is a map.
     * + [BEntryType.EntityEnd]: This is the end of last list/map.
     * */
    fun nextType(): BEntryType = when (c.toChar()) {
        'i' -> BEntry.BInteger
        in ('1'..'9') -> BEntry.BString
        'l' -> BEntry.BList
        'd' -> BEntry.BMap
        'e' -> BEntryType.EntityEnd
        else -> error("Illegal char: $c")
    }

    /**
     * Read int and move to next entry.
     * */
    fun readInteger(): BigInteger {
        check(nextType() == BEntry.BInteger) { "Next entry is ${nextType()}, but request as integer" }
        val sb = StringBuilder()
        var c = reader.read()
        while (c != 'e'.code) {
            sb.append(c.toChar())
            c = reader.read()
        }
        loadNextChar()
        return BigInteger(sb.toString())
    }

    /**
     * Read string and move to next entry.
     * */
    fun readString(): String {
        check(nextType() == BEntry.BString) { "Next entry is ${nextType()}, but request as string" }
        val lengthSb = StringBuilder()
        lengthSb.append(c.toChar())
        var c = reader.read()
        while (c != ':'.code) {
            c.toChar().let {
                require(it in ('0'..'9')) { "Expecting number, but got '${it}'" }
                lengthSb.append(it)
            }
            c = reader.read()
        }
        val length = lengthSb.toString().toInt()
        val chars = CharArray(length)
        reader.read(chars).also {
            check(length == it) { "Not enough char. Expected: $length, actual read: $it" }
        }
        loadNextChar()
        return chars.concatToString()
    }

    /**
     * Start reading list and move to next entry.
     *
     * In the list, each item can be any of [BEntry].
     *
     * This is nothing to return, just skip the 'l' chars.
     * */
    fun startList() {
        check(nextType() == BEntry.BList) { "Next one is ${nextType()}, but requested as list start" }
        loadNextChar()
    }

    /**
     * Start reading map and move to next entry.
     *
     * In the map, each item has two entry.
     * The first one is always [BEntry.BString] as the key,
     * the second one is value, and can be any of [BEntry].
     *
     * This is nothing to return, just skip the 'd' chars.
     * */
    fun startMap() {
        check(nextType() == BEntry.BMap) { "Next one is ${nextType()}, but requested as map start" }
        loadNextChar()
    }

    /**
     * End the last map/list.
     *
     * This is nothing to return, just skip the 'e' chars.
     * */
    fun endEntity() {
        check(nextType() == BEntryType.EntityEnd) { "Next one is ${nextType()}, but requested as entity end" }
        loadNextChar()
    }
}
