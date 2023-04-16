package info.skyblond.bencode

import java.math.BigInteger
import java.util.function.Supplier


sealed interface BEntryType {
    object ListStart : BEntryType {
        override fun toString(): String = "BEntryType.ListStart"
    }

    object MapStart : BEntryType {
        override fun toString(): String = "BEntryType.MapStart"
    }

    object EntityEnd : BEntryType {
        override fun toString(): String = "BEntryType.EntityEnd"
    }
}

/**
 * Describing the 4 basic entries of bencode:
 * + BInteger: BigInteger as value
 * + BString: plain Java string
 * + BList: immutable sequence of Entries (use sequence in case you have a lot of data)
 * + BMap: immutable map, the key is Java string, the value is entry.
 * */
sealed interface BEntry {
    /**
     * The integer in Bencode. Represented by a [BigInteger].
     * */
    data class BInteger(val value: BigInteger) : BEntry {
        companion object : BEntryType {
            override fun toString(): String = "BEntryType.BInteger"
        }
    }

    /**
     * The string in Bencode. Represented by a [String].
     * */
    data class BString(val value: String) : BEntry {
        companion object : BEntryType {
            override fun toString(): String = "BEntryType.BString"
        }
    }

    /**
     * The list in Bencode. Represented by a [Sequence].
     *
     * In this manner, you can use an already generated [List], or
     * generate your data on demand (not caching them in memory).
     * */
    data class BList(val value: Sequence<BEntry>) : BEntry{
        companion object : BEntryType {
            override fun toString(): String = "BEntryType.BList"
        }
    }

    /**
     * The dictionary in Bencode. Represented by a [Map].
     *
     * This Map use [Supplier] as value, so the actual data is generated
     * on demand when writing.
     * */
    data class BMap(val value: Map<String, Supplier<BEntry>>) : BEntry{
        companion object : BEntryType {
            override fun toString(): String = "BEntryType.BMap"
        }
    }
}
