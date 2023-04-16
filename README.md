# BencodeKt
Bencode for kotlin.

![master build](https://github.com/hurui200320/BencodeKt/actions/workflows/gradle-test.yml/badge.svg)
![release build](https://github.com/hurui200320/BencodeKt/actions/workflows/gradle-publish.yml/badge.svg)
![release version](https://shields.io/github/v/release/hurui200320/BencodeKt)
![license](https://shields.io/github/license/hurui200320/BencodeKt)

Bencode reader/writer for Kotlin(JVM and Android).

Requires JDK 1.8 or higher.

## Dependence

### Maven

First, add GitHub Package as maven repo. 
```xml
    <distributionManagement>
        <repository>
            <id>github</id>
            <name>GitHub hurui200320/BencodeKt</name>
            <url>https://maven.pkg.github.com/hurui200320/BencodeKt</url>
        </repository>
    </distributionManagement>
```

Then add the dependency.
```xml
    <dependencies>
        <dependency>
            <groupId>info.skyblond</groupId>
            <artifactId>bencodekt</artifactId>
            <version>latest</version>
        </dependency>
    </dependencies>
```

### Gradle

First, add GitHub Package as maven repo.
```kotlin
repositories {
    maven { url = uri("https://maven.pkg.github.com/hurui200320/BencodeKt") }
}
```

Then add the dependency.
```kotlin
    implementation("info.skyblond:bencodekt:+")
```

## Usage

### High level api

For a higher level api, you may use `BencodeReader` and `BencodeWriter`.

#### `BencodeWriter`

```kotlin
import java.io.StringWriter

val map = mapOf(
    "number" to 123456,
    "string" to "this is a string",
    "list" to listOf(
        "This is a string in list in list",
        234567
    ),
    "dict" to mapOf(
        "key" to "dict in dict"
    )
)

val writer = StringWriter()
BencodeWriter(writer).write(map)
println(writer.toString())
```

You will get: `d4:dictd3:key12:dict in dicte4:listl32:This is a string in list in listi234567ee6:numberi123456e6:string16:this is a stringe`

For custom object, you may represent the object into `BEntry`.

There are 4 types of `BEntry`:

+ `BInteger`: Representing an integer in bencode, use `BigInteger`
+ `BString`: Representing a string in bencode, use `String`
+ `BList`: Representing a list in bencode, use `Sequence`
+ `BMap`: Representing a map in bencode, the key is `String`, the value use `() -> BEntry`

The last two design is optimized for on-demand generating.
The last example can be represented by:

```kotlin
val map = BEntry.BMap(mapOf(
    "number" to { BEntry.BInteger(123456.toBigInteger()) },
    "string" to { BEntry.BString("this is a string") },
    "list" to {
        BEntry.BList(sequence {
            yield(BEntry.BString("This is a string in list in list"))
            yield(BEntry.BInteger(234567.toBigInteger()))
        })
    },
    "dict" to {
        BEntry.BMap(mapOf(
            "key" to { BEntry.BString("dict in dict") }
        ))
    }
))
```

You can implement the `BencodeEncodable` interface:

```kotlin
data class Point(
    val x: Int,
    val y: Int
) : BencodeEncodable {
    override fun encodeToBEntry(): BEntry = BEntry.BMap(mapOf(
        "x" to { BEntry.BInteger(this.x.toBigInteger()) },
        "y" to { BEntry.BInteger(this.y.toBigInteger()) }
    ))
}

val writer = StringWriter()
BencodeWriter(writer).write(Point(3,4))
println(writer.toString())
// output: d1:xi3e1:yi4ee
```

There won't be any `BigInteger` instance created until you write it.

If you can't implement the interface, you can also use the `BencodeEncoderMapper`:

```kotlin
data class Point(
    val x: Int,
    val y: Int
)

val mapper = BencodeEncoderMapper<Point> {
    mapOf(
        "x" to it.x,
        "y" to it.y
    )
}

val writer = StringWriter()
BencodeWriter(writer, listOf(mapper)).write(Point(3,4))
println(writer.toString())
// output: d1:xi3e1:yi4ee
```

You may notice there is no `BEntry` in the mapper. Because by default,
The writer can understand basic types like integers, list, map etc.
You can simply return in those form and let the writer do the rest.
Also, you can return other classed as long as you provide mapper for them.

#### `BencodeReader`

Reader is much simpler:

```kotlin
val str = "d4:dictd10:map in map7:value!!e4:listl16:String in a listi111el31:LOL, string in a list in a listed11:map in list7:value!!ee6:numberi456e6:string17:This is a string!e"
val reader = BencodeReader(str.reader())

while (reader.hasNext()) {
    when (reader.nextType()) {
        BEntry.BInteger -> println(reader.readInteger())
        BEntry.BString -> println(reader.readString())
        BEntry.BList -> println(reader.readList())
        BEntry.BMap -> println(reader.readMap())
        else -> error("Impossible type")
    }
}
```

### Low level api

If you want more control to the reading/writing process, you may use the low
level apis: `BencodeEncoder` and `BencodeDecoder`.

#### `BencodeEncoder`

```kotlin
val writer = StringWriter()
BencodeEncoder(writer).writeEntry(/* BEntry here */)
println(writer.toString())
```

#### `BencodeDecoder`

```kotlin
val str = "some bencode string"
val decoder = BencodeDecoder(str.reader())

while (decoder.hasNext()) {
    when (decoder.nextType()) {
        BEntry.BInteger -> println(decoder.readInteger())
        BEntry.BString ->  println(decoder.readString())
        BEntryType.ListStart -> {
            decoder.startList()
            println("Start list: ")
        }
        BEntryType.MapStart -> {
            decoder.startMap()
            println("Start map: ")
        }
        BEntryType.EntityEnd -> {
            decoder.endEntity()
            println("End: ")
        }
        else -> error("Impossible type")
    }
}
```

To read a list: Keep reading until you get the `EntityEnd`.

To read a map: Read a string as the key, read an entry as value,
repeat until you get the `Entity End`

Note: there might be list in list, or map in list, etc. You have
to considering the recursive.
