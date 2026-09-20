package com.example

import com.example.data.repository.QuranRepository
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun verifyQuranRepositoryData() {
    assertEquals(114, QuranRepository.surahs.size)
    assertTrue(QuranRepository.reciters.size >= 20)

    val alafasy = QuranRepository.reciters[0]
    val audioUrl = QuranRepository.getSurahAudioUrl(alafasy, 1)
    assertTrue(audioUrl.startsWith("http"))
    assertTrue(audioUrl.endsWith("001.mp3"))

    val ayahs = QuranRepository.getAyahsForSurah(17)
    assertTrue(ayahs.isNotEmpty())
    assertNotNull(ayahs[0].tafsirMuyassar)
    assertNotNull(ayahs[0].irabSummary)
  }
}
