package info.skyblond.bencode.encoder

interface BencodeEncoderMapper<T> {
    fun canHandle(obj: Any): Boolean
    fun invoke(obj: T): Any
}

inline fun <reified T> BencodeEncoderMapper(crossinline block: (T) -> Any) =
    object : BencodeEncoderMapper<T> {
        override fun canHandle(obj: Any): Boolean = T::class.java.isInstance(obj)

        override fun invoke(obj: T): Any = block(obj)
    }
