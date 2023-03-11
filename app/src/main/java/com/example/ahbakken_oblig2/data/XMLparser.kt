package com.example.ahbakken_oblig2.data

import android.util.Xml
import com.example.ahbakken_oblig2.model.Party //for xml file
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

//namespace is null
private val ns: String? = null

class XMLparser {
    //initiate the parser
    @Throws(XmlPullParserException::class, java.io.IOException::class)
    fun parse(inputStream: InputStream): List<Party> {
        inputStream.use {
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(it, null)
            parser.nextTag()
            return readFeed(parser)
        }
    }
    //read the feed
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readFeed(parser: XmlPullParser): List<Party> {
        val entries = mutableListOf<Party>()

        parser.require(XmlPullParser.START_TAG, ns, "districtThree")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            // Starts by looking for the entry tag
            if (parser.name == "party") {
                entries.add(readEntry(parser))
            } else {
                skip(parser)
            }
        }
        return entries
    }
    //parses content of an alpaca party
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readEntry(parser: XmlPullParser): Party {
        parser.require(XmlPullParser.START_TAG, ns, "party")
        var id : Int? = null
        var votes : Int? = null
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "id" -> id = readAttribute(parser, parser.name).toIntOrNull()
                "votes" -> votes = readAttribute(parser, parser.name).toIntOrNull()
                else -> skip(parser)
            }
        }
        return Party(id, votes)
    }
    //Processes "id" and "votes" tags in the feed.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readAttribute(parser: XmlPullParser, tag : String): String {
        parser.require(XmlPullParser.START_TAG, ns, tag)
        val id = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, tag)
        return id
    }
    // For the tags "id" and "votes", extracts their text values.
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }
    //skip tags that we're not interested in
    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }
}