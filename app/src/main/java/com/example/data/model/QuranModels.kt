package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SurahInfo(
    val number: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val revelationType: String, // مكية / مدنية
    val totalVerses: Int,
    val pageNumber: Int,
    val juzNumber: Int,
    val hizbNumber: Int
)

@Serializable
data class AyahDetail(
    val surahNumber: Int,
    val numberInSurah: Int,
    val textArabic: String,
    val tafsirMuyassar: String,
    val irabSummary: String,
    val translationEn: String,
    val translationFr: String? = null
)

@Serializable
data class ReciterInfo(
    val id: String,
    val nameArabic: String,
    val nameEnglish: String,
    val rewaya: String,
    val serverUrl: String,
    val serverUrlHq: String? = null,
    val everyAyahFolderHq: String? = null,
    val photoUrl: String? = null,
    val isHqAvailable: Boolean = true,
    val qualityBadge: String = "320kbps HQ",
    val totalSurahsAvailable: Int = 114
)

enum class AudioEffectPreset(val titleArabic: String, val descriptionArabic: String) {
    NORMAL("استوديو فائق النقاء (Master Hi-Fi)", "نقاء الصوت الأصلي المباشر بدون أي تلوين"),
    VOCAL_CLARITY("وضوح مخارج الحروف والتجويد", "إبراز نبرة الصوت وصفاء الحروف وحروف الحلق"),
    KHUSHU_REVERB("ترتيل خاشع وعمق رحابي", "صدى رحابي هادئ ودفء نغمي يبعث السكينة"),
    BASS_RESONANCE("تضخيم وقور وفخامة صوتية", "عمق صوتي دافئ يناسب مكبرات الصوت والسماعات"),
    LOUDSPEAKER_BOOST("معزز الصوت للمكبرات الخارجية", "رفع ذكي للمستوى الصوتي ونقاء فائق دون تشويش")
}

enum class AudioQualityPreset(val titleArabic: String, val bitrate: String, val subtitleArabic: String = "") {
    HQ_320("استوديو فائق (320kbps)", "320kbps", "أعلى نقاء صوتي ووضوح للتسجيلات"),
    SAVER_128("موفّر للمساحة (128kbps)", "128kbps", "استهلاك اقتصادي للبيانات وسرعة تحميل")
}
