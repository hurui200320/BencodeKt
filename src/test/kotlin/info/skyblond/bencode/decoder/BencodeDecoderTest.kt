package info.skyblond.bencode.decoder

import info.skyblond.bencode.BEntry
import info.skyblond.bencode.BEntryType
import org.junit.jupiter.api.Test
import java.io.StringReader
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BencodeDecoderTest {
    @Test
    fun test() {
        val str =
            "d4:dict\n  \rd10:map in map7:value!!e4:listl16:String in a listi111el31:LOL, string in a list in a listed11:map in list7:value!!ee6:numberi456e6:string17:This is a string!e"
        val reader = StringReader(str)
        val decoder = BencodeDecoder(reader)
        // d
        assertTrue(decoder.hasNext())
        assertEquals(BEntryType.MapStart, decoder.nextType())
        decoder.startMap()
        //     4:dict
        assertEquals(BEntry.BString, decoder.nextType())
        assertEquals("dict", decoder.readString())
        //     d
        assertEquals(BEntryType.MapStart, decoder.nextType())
        decoder.startMap()
        //         10:map in map
        assertEquals(BEntry.BString, decoder.nextType())
        assertEquals("map in map", decoder.readString())
        //         7:value!!
        assertEquals(BEntry.BString, decoder.nextType())
        assertEquals("value!!", decoder.readString())
        //     e
        assertEquals(BEntryType.EntityEnd, decoder.nextType())
        decoder.endEntity()
        //     4:list
        assertEquals(BEntry.BString, decoder.nextType())
        assertEquals("list", decoder.readString())
        //     l
        assertEquals(BEntryType.ListStart, decoder.nextType())
        decoder.startList()
        //         16:String in a list
        assertEquals(BEntry.BString, decoder.nextType())
        assertEquals("String in a list", decoder.readString())
        //         i111e
        assertEquals(BEntry.BInteger, decoder.nextType())
        assertEquals("111", decoder.readInteger().toString(10))
        //         l
        assertEquals(BEntryType.ListStart, decoder.nextType())
        decoder.startList()
        //             31:LOL, string in a list in a list
        assertEquals(BEntry.BString, decoder.nextType())
        assertEquals("LOL, string in a list in a list", decoder.readString())
        //         e
        assertEquals(BEntryType.EntityEnd, decoder.nextType())
        decoder.endEntity()
        //         d
        assertEquals(BEntryType.MapStart, decoder.nextType())
        decoder.startMap()
        //             11:map in list
        assertEquals(BEntry.BString, decoder.nextType())
        assertEquals("map in list", decoder.readString())
        //             7:value!!
        assertEquals(BEntry.BString, decoder.nextType())
        assertEquals("value!!", decoder.readString())
        //         e
        assertEquals(BEntryType.EntityEnd, decoder.nextType())
        decoder.endEntity()
        //     e
        assertEquals(BEntryType.EntityEnd, decoder.nextType())
        decoder.endEntity()
        //     6:number
        assertEquals(BEntry.BString, decoder.nextType())
        assertEquals("number", decoder.readString())
        //     i456e
        assertEquals(BEntry.BInteger, decoder.nextType())
        assertEquals("456", decoder.readInteger().toString(10))
        //     6:string
        assertEquals(BEntry.BString, decoder.nextType())
        assertEquals("string", decoder.readString())
        //     17:This is a string!
        assertEquals(BEntry.BString, decoder.nextType())
        assertEquals("This is a string!", decoder.readString())
        // e
        assertEquals(BEntryType.EntityEnd, decoder.nextType())
        decoder.endEntity()

        assertFalse(decoder.hasNext())
    }
}
