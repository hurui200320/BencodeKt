package info.skyblond.bencode.decoder

import info.skyblond.bencode.BEntry
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BencodeReaderTest {
    @Test
    fun test() {
        val str =
            "d4:dictd10:map in map7:value!!e4:listl16:String in a listi111el31:LOL, string in a list in a listed11:map in list7:value!!ee6:numberi456e6:string17:This is a string!e"
        val reader = BencodeReader(str.reader())
        assertTrue(reader.hasNext())
        assertEquals(BEntry.BMap, reader.nextType())
        val result = reader.readMap().also { println(it) }
        val map = mapOf(
            "number" to 456.toBigInteger(),
            "string" to "This is a string!",
            "list" to listOf(
                "String in a list",
                111.toBigInteger(),
                listOf("LOL, string in a list in a list"),
                mapOf("map in list" to "value!!")
            ),
            "dict" to mapOf("map in map" to "value!!")
        ).also { println(it) }
        assertEquals(map, result)
        assertFalse(reader.hasNext())
    }
}
