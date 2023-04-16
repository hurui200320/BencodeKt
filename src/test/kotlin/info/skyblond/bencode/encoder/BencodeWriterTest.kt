package info.skyblond.bencode.encoder

import com.dampcake.bencode.Bencode
import org.junit.jupiter.api.Test
import java.io.StringWriter
import java.util.concurrent.ConcurrentSkipListMap
import kotlin.test.assertEquals


class BencodeWriterTest {

    data class TestClass(
        val x: Int,
        val y: UShort,
        val z: Long,
        val l: List<String>,
        val m: Map<Int, Int>
    )

    @Test
    fun testObject() {
        val a = TestClass(
            x = 1,
            y = 2u,
            z = 120387895234L,
            l = listOf("haha", "lol"),
            m = mapOf(
                222 to 333
            )
        )
        val writer = StringWriter()
        val encoder = BencodeWriter(writer, listOf(
            BencodeEncoderMapper<TestClass> {
                mapOf(
                    "x" to it.x, "y" to it.y, "z" to it.z, "l" to it.l,
                    "m" to it.m,
                )
            }
        ))
        encoder.write(a)
        println(a.toString())
        println(writer.toString())
    }

    @Test
    fun testBencodeLib() {
        val map = object : HashMap<Any, Any>() {
            init {
                put("string", "value")
                put("number", 123456)
                put("list", object : ArrayList<Any>() {
                    init {
                        add("list-item-1")
                        add("list-item-2")
                    }
                })
                put("dict", object : ConcurrentSkipListMap<Any, Any>() {
                    init {
                        put(123, "test")
                        put(456, "thing")
                    }
                })
            }
        }


        val libEncoded = Bencode().encode(map).decodeToString()
        println(libEncoded)
        val writer = StringWriter()
        val myEncoded = BencodeWriter(writer).let {
            it.write(map)
            writer.toString()
        }
        println(myEncoded)
        assertEquals(libEncoded, myEncoded)
    }
}
