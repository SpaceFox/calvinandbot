package fr.spacefox.calvinandbot.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class StripBodyToModelMapperTest {

    @Test
    fun stripBodyToModel() {
    }

    @Test
    fun cleanTranscript() {
        Assertions.assertEquals(
            "— Hobbes: Z\n" +
                "— Calvin: Yaahh!\n" +
                "— Calvin: I keep forgetting that five of his six ends are pointy when he lies like that.",
            StripBodyToModelMapper.cleanTranscript(
                "Hobbes: Z Calvin: Yaahh! Calvin: I keep forgetting that five of his six ends are pointy when he lies like that. "
            )
        )
        Assertions.assertEquals(
            "— Calvin: So long, Pop! I'm off to check my tiger trap! I rigged a tuna fish sandwigh yesterday, so I'm sure to have a tiger by now!\n" +
                "— Dad: They like tuna fish, huh?\n" +
                "— Calvin: Tigers will do anything for a tuna fish sandwich!\n" +
                "— Hobbes: We're kind of stupid that way.",
            StripBodyToModelMapper.cleanTranscript("Calvin: So long, Pop! I&#39;m off to check my tiger trap! I rigged a tuna fish sandwigh yesterday, so I&#39;m sure to have a tiger by now! Dad: They like tuna fish, huh? Calvin: Tigers will do anything for a tuna fish sandwich! Hobbes: We&#39;re kind of stupid that way.")
        )
        Assertions.assertEquals(
            "— Calvin: Hee hee hee hee  But for my own example, I'd never believe one little kid could have so much brains! I'm a genius, Hobbes. There's simply no other word for it. Who else would think to arm a toboggan! It's just genius! See Susie Derkins down there? She's building a snow man and doesn't even know we're up here! We'll zip down and pelt her silly with snowballs! You steer and I'll throw! See, the snowballs will gain even more force from our own velocity! Genis, huh! Ha ha! We'll be a mile away before she can even pick her head out of the snow! There she is! Steer closer so I can get her! Lean! Lean! Augh! Steer! You're too close!  Mayday!!  Another genius thwarted by an incapable assistant.\n" +
                "— Susie: Hey Calvin, look up.",
            StripBodyToModelMapper.cleanTranscript("Calvin: Hee hee hee hee  But for my own example, I&#39;d never believe one little kid could have so much brains! I&#39;m a genius, Hobbes. There&#39;s simply no other word for it. Who else would think to arm a toboggan! It&#39;s just genius! See Susie Derkins down there? She&#39;s building a snow man and doesn&#39;t even know we&#39;re up here! We&#39;ll zip down and pelt her silly with snowballs! You steer and I&#39;ll throw! See, the snowballs will gain even more force from our own velocity! Genis, huh! Ha ha! We&#39;ll be a mile away before she can even pick her head out of the snow! There she is! Steer closer so I can get her! Lean! Lean! Augh! Steer! You&#39;re too close!  Mayday!!  Another genius thwarted by an incapable assistant. Susie: Hey Calvin, look up. ")
        )
//        assertEquals(
//            "— Susie: Calvin, pass this note to Jessica. IT's a secret note, so don't read it.\n" +
//                    "— Written on paper: Calvin you stinkhead: I told you not to read this. Susie.",
//            StripBodyToModelMapper.cleanTranscript("Susie: Calvin, pass this note to Jessica. IT&#39;s a secret note, so don&#39;t read it. Written on paper: Calvin you stinkhead: I told you not to read this. Susie.")
//        )
//        assertEquals(
//            "— Hobbes: We're lost again.\n" +
//                    "— Calvin: Ha! We're brave explorers! The word \"lost\" isn't even in our vocabulary!\n" +
//                    "— Hobbes: How about the word \"Mommy\"?\n" +
//                    "— Calvin and Hobbes: Mommmyyy!!!",
//            StripBodyToModelMapper.cleanTranscript("Hobbes: We&#39;re lost again. Calvin: Ha! We&#39;re brave explorers! The word \"lost\" isn&#39;t even in our vocabulary! Hobbes: How about the word \"Mommy\"? Calvin and Hobbes: Mommmyyy!!!")
//        )
    }

    @Test
    fun toTags() {
        val actual = StripBodyToModelMapper.toTags("tigers,sleeping &amp; waking up,wounds &amp; injuries")
        val expected = setOf("tigers", "sleeping & waking up", "wounds & injuries")
        Assertions.assertEquals(expected.size, actual.size)
        assertTrue(actual.containsAll(expected))
        assertTrue(expected.containsAll(actual))
    }
}
