package info.skyblond.bencode.encoder

import info.skyblond.bencode.BEntry
import org.junit.jupiter.api.Test
import java.io.StringWriter
import java.util.function.Supplier

class BencodeEncoderTest {
    @Test
    fun test() {
        val obj = BEntry.BMap(mapOf(
            "number" to { BEntry.BInteger(456.toBigInteger()) },
            "string" to { BEntry.BString("This is a string!") },
            "list" to {
                BEntry.BList(sequence {
                    yield(BEntry.BString("String in a list"))
                    yield(BEntry.BInteger(111.toBigInteger()))
                    yield(
                        BEntry.BList(
                            listOf(
                                BEntry.BString("LOL, string in a list in a list")
                            ).asSequence()
                        )
                    )
                    yield(
                        BEntry.BMap(
                            mapOf(
                                "map in list" to { BEntry.BString("value!!") }
                            )
                        )
                    )
                })
            },
            "dict" to {
                BEntry.BMap(
                    mapOf(
                        "map in map" to { BEntry.BString("value!!") }
                    )
                )
            }
        ))

        val writer = StringWriter()
        val encoder = BencodeEncoder(writer)
        encoder.writeEntry(obj)
        println(writer.toString())
    }
}
