package fr.spacefox.calvinandbot.model

import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.IntPoint
import org.apache.lucene.document.LongPoint
import org.apache.lucene.document.StoredField
import org.apache.lucene.document.TextField
import org.apache.lucene.index.IndexableField
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.reflect.KClass
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class StripToLuceneDocMapperTest {

    @Test
    fun toDoc() {
        // Given
        val strip = Strip(
            id = 1,
            author = "Author",
            image = "https://path.to/image.jpg",
            url = "https://path.to/strip",
            title = "Title",
            transcript = "Transcript",
            rawTranscript = "Raw Transcript",
            publishDate = LocalDate.of(1990, 1, 2),
            tags = setOf("Tag 1", "Tag 2")
        )
        val dateAsEpochDay: Long = 7306

        // When
        val actual = StripToLuceneDocMapper.toDoc(strip)

        // Then
        assertNotNull(actual)
        assertField(actual.getField(StripDocumentModel.idSearch), IntPoint::class, IndexableField::numericValue, 1)
        assertField(actual.getField(StripDocumentModel.id), StoredField::class, IndexableField::numericValue, 1)
        assertField(
            actual.getField(StripDocumentModel.author),
            StoredField::class,
            IndexableField::stringValue,
            "Author"
        )
        assertField(
            actual.getField(StripDocumentModel.image),
            StoredField::class,
            IndexableField::stringValue,
            "https://path.to/image.jpg"
        )
        assertField(
            actual.getField(StripDocumentModel.url),
            StoredField::class,
            IndexableField::stringValue,
            "https://path.to/strip"
        )
        assertField(actual.getField(StripDocumentModel.title), StoredField::class, IndexableField::stringValue, "Title")
        assertField(
            actual.getField(StripDocumentModel.transcript),
            StoredField::class,
            IndexableField::stringValue,
            "Transcript"
        )
        assertField(
            actual.getField(StripDocumentModel.rawTranscript),
            TextField::class,
            IndexableField::stringValue,
            "Raw Transcript"
        )
        assertField(
            actual.getField(StripDocumentModel.publishDateSearch),
            LongPoint::class,
            IndexableField::numericValue,
            dateAsEpochDay
        )
        assertEquals(TextField::class, actual.getField(StripDocumentModel.tag)::class)
        val actualTagValues = actual.getValues(StripDocumentModel.tag)
        assertEquals(2, actualTagValues.size)
        assertContains(actualTagValues, "Tag 1")
        assertContains(actualTagValues, "Tag 2")
    }

    private fun <T> assertField(
        field: IndexableField,
        kClass: KClass<*>,
        getValue: (field: IndexableField) -> T,
        value: T
    ) {
        assertEquals(field::class, kClass)
        assertEquals(value, getValue.invoke(field))
    }

    @Test
    fun toStrip() {
        // Given
        val dateAsEpochDay: Long = 7306
        val doc = Document()
        doc.add(IntPoint(StripDocumentModel.idSearch, 1))
        doc.add(StoredField(StripDocumentModel.id, 1))
        doc.add(StoredField(StripDocumentModel.author, "Author"))
        doc.add(StoredField(StripDocumentModel.image, "https://path.to/image.jpg"))
        doc.add(StoredField(StripDocumentModel.url, "https://path.to/strip"))
        doc.add(StoredField(StripDocumentModel.title, "Title"))
        doc.add(StoredField(StripDocumentModel.transcript, "Transcript"))
        doc.add(TextField(StripDocumentModel.rawTranscript, "Raw Transcript", Field.Store.YES))
        doc.add(LongPoint(StripDocumentModel.publishDateSearch, dateAsEpochDay))
        doc.add(StoredField(StripDocumentModel.publishDate, dateAsEpochDay))
        for (tag in arrayOf("Tag 1", "Tag 2")) {
            doc.add(TextField(StripDocumentModel.tag, tag, Field.Store.YES))
        }

        // When
        val actual = StripToLuceneDocMapper.toStrip(doc)

        // Then
        assertNotNull(actual)
        assertEquals(1, actual.id)
        assertEquals("Author", actual.author)
        assertEquals("https://path.to/image.jpg", actual.image)
        assertEquals("https://path.to/strip", actual.url)
        assertEquals("Title", actual.title)
        assertEquals("Transcript", actual.transcript)
        assertEquals("Raw Transcript", actual.rawTranscript)
        assertEquals(LocalDate.of(1990, 1, 2), actual.publishDate)
        assertNotNull(actual.tags)
        assertEquals(2, actual.tags.size)
        assertContains(actual.tags, "Tag 1")
        assertContains(actual.tags, "Tag 2")
    }
}
