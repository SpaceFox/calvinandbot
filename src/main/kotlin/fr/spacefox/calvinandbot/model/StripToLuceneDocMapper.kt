package fr.spacefox.calvinandbot.model

import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.IntPoint
import org.apache.lucene.document.LongPoint
import org.apache.lucene.document.StoredField
import org.apache.lucene.document.TextField
import java.time.LocalDate
import java.util.Arrays
import java.util.stream.Collectors

object StripToLuceneDocMapper {

    fun toDoc(strip: Strip): Document {
        val doc = Document()
        doc.add(IntPoint(StripDocumentModel.idSearch, strip.id))
        doc.add(StoredField(StripDocumentModel.id, strip.id))
        doc.add(StoredField(StripDocumentModel.author, strip.author))
        doc.add(StoredField(StripDocumentModel.image, strip.image))
        doc.add(StoredField(StripDocumentModel.url, strip.url))
        doc.add(StoredField(StripDocumentModel.title, strip.title))
        doc.add(StoredField(StripDocumentModel.transcript, strip.transcript))
        doc.add(TextField(StripDocumentModel.rawTranscript, strip.rawTranscript, Field.Store.YES))
        doc.add(LongPoint(StripDocumentModel.publishDateSearch, strip.publishDate.toEpochDay()))
        doc.add(StoredField(StripDocumentModel.publishDate, strip.publishDate.toEpochDay()))
        for (tag in strip.tags) {
            doc.add(TextField(StripDocumentModel.tag, tag, Field.Store.YES))
        }
        return doc
    }

    fun toStrip(doc: Document): Strip {
        val tags = Arrays
            .stream(doc.getValues(StripDocumentModel.tag))
            .collect(Collectors.toSet())
        return Strip(
            id = doc.getField(StripDocumentModel.id).numericValue().toInt(),
            author = doc.get(StripDocumentModel.author),
            image = doc.get(StripDocumentModel.image),
            url = doc.get(StripDocumentModel.url),
            title = doc.get(StripDocumentModel.title),
            transcript = doc.get(StripDocumentModel.transcript),
            rawTranscript = doc.get(StripDocumentModel.rawTranscript),
            publishDate = LocalDate.ofEpochDay(doc.getField(StripDocumentModel.publishDate).numericValue().toLong()),
            tags = tags
        )
    }
}
