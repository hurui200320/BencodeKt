package info.skyblond.bencode.encoder

import info.skyblond.bencode.BEntry

/**
 * Like serializable, this interface means an object
 * can be represented in a [BEntry]. Otherwise, you will need
 * a [BencodeEncoderMapper].
 * */
interface BencodeEncodable {
    fun encodeToBEntry(): BEntry
}
