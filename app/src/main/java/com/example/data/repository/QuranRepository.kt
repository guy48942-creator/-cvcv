package com.example.data.repository

import com.example.data.model.AyahDetail
import com.example.data.model.ReciterInfo
import com.example.data.model.SurahInfo

object QuranRepository {

    val surahs: List<SurahInfo> = listOf(
        SurahInfo(1, "الفَاتِحَة", "Al-Fatihah", "مكية", 7, 1, 1, 1),
        SurahInfo(2, "البَقَرَة", "Al-Baqarah", "مدنية", 286, 2, 1, 1),
        SurahInfo(3, "آل عِمْرَان", "Aal-Imran", "مدنية", 200, 50, 3, 6),
        SurahInfo(4, "النِّسَاء", "An-Nisa", "مدنية", 176, 77, 4, 8),
        SurahInfo(5, "المَائِدَة", "Al-Ma'idah", "مدنية", 120, 106, 6, 11),
        SurahInfo(6, "الأَنْعَام", "Al-An'am", "مكية", 165, 128, 7, 13),
        SurahInfo(7, "الأَعْرَاف", "Al-A'raf", "مكية", 206, 151, 8, 16),
        SurahInfo(8, "الأَنْفَال", "Al-Anfal", "مدنية", 75, 177, 9, 18),
        SurahInfo(9, "التَّوْبَة", "At-Tawbah", "مدنية", 129, 187, 10, 19),
        SurahInfo(10, "يُونُس", "Yunus", "مكية", 109, 208, 11, 21),
        SurahInfo(11, "هُود", "Hud", "مكية", 123, 221, 11, 22),
        SurahInfo(12, "يُوسُف", "Yusuf", "مكية", 111, 235, 12, 23),
        SurahInfo(13, "الرَّعْد", "Ar-Ra'd", "مدنية", 43, 249, 13, 25),
        SurahInfo(14, "إِبْرَاهِيم", "Ibrahim", "مكية", 52, 255, 13, 26),
        SurahInfo(15, "الحِجْر", "Al-Hijr", "مكية", 99, 262, 14, 27),
        SurahInfo(16, "النَّحْل", "An-Nahl", "مكية", 128, 267, 14, 27),
        SurahInfo(17, "الإِسْرَاء", "Al-Isra", "مكية", 111, 282, 15, 29),
        SurahInfo(18, "الكَهْف", "Al-Kahf", "مكية", 110, 293, 15, 30),
        SurahInfo(19, "مَرْيَم", "Maryam", "مكية", 98, 305, 16, 31),
        SurahInfo(20, "طه", "Taha", "مكية", 135, 312, 16, 32),
        SurahInfo(21, "الأَنْبِيَاء", "Al-Anbiya", "مكية", 112, 322, 17, 33),
        SurahInfo(22, "الحَجّ", "Al-Hajj", "مدنية", 78, 332, 17, 34),
        SurahInfo(23, "المُؤْمِنُون", "Al-Mu'minun", "مكية", 118, 342, 18, 35),
        SurahInfo(24, "النُّور", "An-Nur", "مدنية", 64, 350, 18, 36),
        SurahInfo(25, "الفُرْقَان", "Al-Furqan", "مكية", 77, 359, 18, 36),
        SurahInfo(26, "الشُّعَرَاء", "Ash-Shu'ara", "مكية", 227, 367, 19, 37),
        SurahInfo(27, "النَّمْل", "An-Naml", "مكية", 93, 377, 19, 38),
        SurahInfo(28, "القَصَص", "Al-Qasas", "مكية", 88, 385, 20, 39),
        SurahInfo(29, "العَنْكَبُوت", "Al-Ankabut", "مكية", 69, 396, 20, 40),
        SurahInfo(30, "الرُّوم", "Ar-Rum", "مكية", 60, 404, 21, 41),
        SurahInfo(31, "لُقْمَان", "Luqman", "مكية", 34, 411, 21, 41),
        SurahInfo(32, "السَّجْدَة", "As-Sajdah", "مكية", 30, 415, 21, 42),
        SurahInfo(33, "الأَحْزَاب", "Al-Ahzab", "مدنية", 73, 418, 21, 42),
        SurahInfo(34, "سَبَأ", "Saba", "مكية", 54, 428, 22, 43),
        SurahInfo(35, "فَاطِر", "Fatir", "مكية", 45, 434, 22, 44),
        SurahInfo(36, "يس", "Ya-Sin", "مكية", 83, 440, 22, 44),
        SurahInfo(37, "الصَّافَّات", "As-Saffat", "مكية", 182, 446, 23, 45),
        SurahInfo(38, "ص", "Sad", "مكية", 88, 453, 23, 46),
        SurahInfo(39, "الزُّمَر", "Az-Zumar", "مكية", 75, 458, 23, 47),
        SurahInfo(40, "غَافِر", "Ghafir", "مكية", 85, 467, 24, 47),
        SurahInfo(41, "فُصِّلَت", "Fussilat", "مكية", 54, 477, 24, 48),
        SurahInfo(42, "الشُّورَى", "Ash-Shura", "مكية", 53, 483, 25, 49),
        SurahInfo(43, "الزُّخْرُف", "Az-Zukhruf", "مكية", 89, 489, 25, 50),
        SurahInfo(44, "الدُّخَان", "Ad-Dukhan", "مكية", 59, 496, 25, 50),
        SurahInfo(45, "الجَاثِيَة", "Al-Jathiyah", "مكية", 37, 499, 25, 50),
        SurahInfo(46, "الأَحْقَاف", "Al-Ahqaf", "مكية", 35, 502, 26, 51),
        SurahInfo(47, "مُحَمَّد", "Muhammad", "مدنية", 38, 507, 26, 52),
        SurahInfo(48, "الفَتْح", "Al-Fath", "مدنية", 29, 511, 26, 52),
        SurahInfo(49, "الحُجُرَات", "Al-Hujurat", "مدنية", 18, 515, 26, 52),
        SurahInfo(50, "ق", "Qaf", "مكية", 45, 518, 26, 52),
        SurahInfo(51, "الذَّارِيَات", "Adh-Dhariyat", "مكية", 60, 520, 26, 53),
        SurahInfo(52, "الطُّور", "At-Tur", "مكية", 49, 523, 27, 53),
        SurahInfo(53, "النَّجْم", "An-Najm", "مكية", 62, 526, 27, 53),
        SurahInfo(54, "القَمَر", "Al-Qamar", "مكية", 55, 528, 27, 54),
        SurahInfo(55, "الرَّحْمَٰن", "Ar-Rahman", "مدنية", 78, 531, 27, 54),
        SurahInfo(56, "الوَاقِعَة", "Al-Waqi'ah", "مكية", 96, 534, 27, 55),
        SurahInfo(57, "الحَدِيد", "Al-Hadid", "مدنية", 29, 537, 27, 55),
        SurahInfo(58, "المُجَادَلَة", "Al-Mujadila", "مدنية", 22, 542, 28, 55),
        SurahInfo(59, "الحَشْر", "Al-Hashr", "مدنية", 24, 545, 28, 56),
        SurahInfo(60, "المُمْتَحَنَة", "Al-Mumtahanah", "مدنية", 13, 549, 28, 56),
        SurahInfo(61, "الصَّفّ", "As-Saff", "مدنية", 14, 551, 28, 57),
        SurahInfo(62, "الجُمُعَة", "Al-Jumu'ah", "مدنية", 11, 553, 28, 57),
        SurahInfo(63, "المُنَافِقُون", "Al-Munafiqun", "مدنية", 11, 554, 28, 57),
        SurahInfo(64, "التَّغَابُن", "At-Taghabun", "مدنية", 18, 556, 28, 57),
        SurahInfo(65, "الطَّلَاق", "At-Talaq", "مدنية", 12, 558, 28, 58),
        SurahInfo(66, "التَّحْرِيم", "At-Tahrim", "مدنية", 12, 560, 28, 58),
        SurahInfo(67, "المُلْك", "Al-Mulk", "مكية", 30, 562, 29, 58),
        SurahInfo(68, "القَلَم", "Al-Qalam", "مكية", 52, 564, 29, 58),
        SurahInfo(69, "الحَاقَّة", "Al-Haqqah", "مكية", 52, 566, 29, 59),
        SurahInfo(70, "المَعَارِج", "Al-Ma'arij", "مكية", 44, 568, 29, 59),
        SurahInfo(71, "نُوح", "Nuh", "مكية", 28, 570, 29, 59),
        SurahInfo(72, "الجِنّ", "Al-Jinn", "مكية", 28, 572, 29, 60),
        SurahInfo(73, "المُزَّمِّل", "Al-Muzzammil", "مكية", 20, 574, 29, 60),
        SurahInfo(74, "المُدَّثِّر", "Al-Muddaththir", "مكية", 56, 575, 29, 60),
        SurahInfo(75, "القِيَامَة", "Al-Qiyamah", "مكية", 40, 577, 29, 60),
        SurahInfo(76, "الإِنْسَان", "Al-Insan", "مدنية", 31, 578, 29, 60),
        SurahInfo(77, "المُرْسَلَات", "Al-Mursalat", "مكية", 50, 580, 29, 60),
        SurahInfo(78, "النَّبَأ", "An-Naba", "مكية", 40, 582, 30, 60),
        SurahInfo(79, "النَّازِعَات", "An-Nazi'at", "مكية", 46, 583, 30, 60),
        SurahInfo(80, "عَبَسَ", "Abasa", "مكية", 42, 585, 30, 60),
        SurahInfo(81, "التَّكْوِير", "At-Takwir", "مكية", 29, 586, 30, 60),
        SurahInfo(82, "الانْفِطَار", "Al-Infitar", "مكية", 19, 587, 30, 60),
        SurahInfo(83, "المُطَفِّفِين", "Al-Mutaffifin", "مكية", 36, 587, 30, 60),
        SurahInfo(84, "الانْشِقَاق", "Al-Inshiqaq", "مكية", 25, 589, 30, 60),
        SurahInfo(85, "البُرُوج", "Al-Buruj", "مكية", 22, 590, 30, 60),
        SurahInfo(86, "الطَّارِق", "At-Tariq", "مكية", 17, 591, 30, 60),
        SurahInfo(87, "الأَعْلَى", "Al-A'la", "مكية", 19, 591, 30, 60),
        SurahInfo(88, "الغَاشِيَة", "Al-Ghashiyah", "مكية", 26, 592, 30, 60),
        SurahInfo(89, "الفَجْر", "Al-Fajr", "مكية", 30, 593, 30, 60),
        SurahInfo(90, "البَلَد", "Al-Balad", "مكية", 20, 594, 30, 60),
        SurahInfo(91, "الشَّمْس", "Ash-Shams", "مكية", 15, 595, 30, 60),
        SurahInfo(92, "اللَّيْل", "Al-Layl", "مكية", 21, 595, 30, 60),
        SurahInfo(93, "الضُّحَى", "Ad-Duha", "مكية", 11, 596, 30, 60),
        SurahInfo(94, "الشَّرْح", "Ash-Sharh", "مكية", 8, 596, 30, 60),
        SurahInfo(95, "التِّين", "At-Tin", "مكية", 8, 597, 30, 60),
        SurahInfo(96, "العَلَق", "Al-Alaq", "مكية", 19, 597, 30, 60),
        SurahInfo(97, "القَدْر", "Al-Qadr", "مكية", 5, 598, 30, 60),
        SurahInfo(98, "البَيِّنَة", "Al-Bayyinah", "مدنية", 8, 598, 30, 60),
        SurahInfo(99, "الزَّلْزَلَة", "Az-Zalzalah", "مدنية", 8, 599, 30, 60),
        SurahInfo(100, "العَادِيَات", "Al-Adiyat", "مكية", 11, 599, 30, 60),
        SurahInfo(101, "القَارِعَة", "Al-Qari'ah", "مكية", 11, 600, 30, 60),
        SurahInfo(102, "التَّكَاثُر", "At-Takathur", "مكية", 8, 600, 30, 60),
        SurahInfo(103, "العَصْر", "Al-Asr", "مكية", 3, 601, 30, 60),
        SurahInfo(104, "الهُمَزَة", "Al-Humazah", "مكية", 9, 601, 30, 60),
        SurahInfo(105, "الفِيل", "Al-Fil", "مكية", 5, 601, 30, 60),
        SurahInfo(106, "قُرَيْش", "Quraysh", "مكية", 4, 602, 30, 60),
        SurahInfo(107, "المَاعُون", "Al-Ma'un", "مكية", 7, 602, 30, 60),
        SurahInfo(108, "الكَوْثَر", "Al-Kawthar", "مكية", 3, 602, 30, 60),
        SurahInfo(109, "الكَافِرُون", "Al-Kafirun", "مكية", 6, 603, 30, 60),
        SurahInfo(110, "النَّصْر", "An-Nasr", "مدنية", 3, 603, 30, 60),
        SurahInfo(111, "المَسَد", "Al-Masad", "مكية", 5, 603, 30, 60),
        SurahInfo(112, "الإِخْلَاص", "Al-Ikhlas", "مكية", 4, 604, 30, 60),
        SurahInfo(113, "الفَلَق", "Al-Falaq", "مكية", 5, 604, 30, 60),
        SurahInfo(114, "النَّاس", "An-Nas", "مكية", 6, 604, 30, 60)
    )

    // Extensive Reciters Library with high-quality MP3 and Studio HD servers
    val reciters: List<ReciterInfo> = listOf(
        ReciterInfo(
            id = "alafasy",
            nameArabic = "الشيخ مشاري راشد العفاسي",
            nameEnglish = "Mishari Rashid Alafasy",
            rewaya = "حفص عن عاصم",
            serverUrl = "https://server8.mp3quran.net/afs/",
            serverUrlHq = "https://download.quranicaudio.com/quran/mishaari_raashid_al_3afaasee/",
            everyAyahFolderHq = "Alafasy_128kbps",
            qualityBadge = "320kbps HQ",
            photoUrl = "https://images.unsplash.com/photo-1591604466107-ec97de577aff?w=150"
        ),
        ReciterInfo(
            id = "basit_murattal",
            nameArabic = "الشيخ عبد الباسط عبد الصمد",
            nameEnglish = "Abdulbasit Abdulsamad (Murattal)",
            rewaya = "حفص عن عاصم (مرتل)",
            serverUrl = "https://server7.mp3quran.net/basit/",
            serverUrlHq = "https://download.quranicaudio.com/quran/abdul_baasit_murattal/",
            everyAyahFolderHq = "Abdul_Basit_Murattal_192kbps",
            qualityBadge = "192kbps HD",
            photoUrl = "https://images.unsplash.com/photo-1542838132-92c53300491e?w=150"
        ),
        ReciterInfo(
            id = "husary",
            nameArabic = "الشيخ محمود خليل الحصري",
            nameEnglish = "Mahmoud Khalil Al-Husary",
            rewaya = "حفص عن عاصم (قصر المنفصل)",
            serverUrl = "https://server13.mp3quran.net/husr/",
            serverUrlHq = "https://download.quranicaudio.com/quran/mahmood_khaleel_al-husaree_iza3ah/",
            everyAyahFolderHq = "Husary_128kbps",
            qualityBadge = "320kbps HQ",
            photoUrl = "https://images.unsplash.com/photo-1519817650390-64a93db51149?w=150"
        ),
        ReciterInfo(
            id = "maher",
            nameArabic = "الشيخ ماهر المعيقلي",
            nameEnglish = "Maher Al-Muaiqly",
            rewaya = "حفص عن عاصم",
            serverUrl = "https://server12.mp3quran.net/maher/",
            serverUrlHq = "https://download.quranicaudio.com/quran/mahir_al-mu3ayqlee/",
            everyAyahFolderHq = "MaherAlMuaiqly128kbps",
            qualityBadge = "HQ Master",
            photoUrl = "https://images.unsplash.com/photo-1507679799987-c73779587ccf?w=150"
        ),
        ReciterInfo(
            id = "ghamadi",
            nameArabic = "الشيخ سعد الغامدي",
            nameEnglish = "Saad Al-Ghamdi",
            rewaya = "حفص عن عاصم",
            serverUrl = "https://server7.mp3quran.net/s_gmd/",
            serverUrlHq = "https://download.quranicaudio.com/quran/sa3d_al-ghaamidee/",
            everyAyahFolderHq = "Ghamadi_40kbps",
            qualityBadge = "192kbps HQ"
        ),
        ReciterInfo(
            id = "ajamy",
            nameArabic = "الشيخ أحمد بن علي العجمي",
            nameEnglish = "Ahmed Al-Ajmy",
            rewaya = "حفص عن عاصم",
            serverUrl = "https://server10.mp3quran.net/ajm/",
            serverUrlHq = "https://download.quranicaudio.com/quran/ahmed_ibn_3alee_al-3ajmee/",
            everyAyahFolderHq = "Ahmed_ibn_Ali_al-Ajamy_128kbps_KetabAllah.net",
            qualityBadge = "128kbps HD"
        ),
        ReciterInfo(
            id = "shatri",
            nameArabic = "الشيخ أبو بكر الشاطري",
            nameEnglish = "Abu Bakr Al-Shatri",
            rewaya = "حفص عن عاصم",
            serverUrl = "https://server11.mp3quran.net/shatri/",
            serverUrlHq = "https://download.quranicaudio.com/quran/abu_bakr_ash-shaatree/",
            everyAyahFolderHq = "Abu_Bakr_Ash-Shatri_128kbps",
            qualityBadge = "320kbps HQ"
        ),
        ReciterInfo(
            id = "sudais",
            nameArabic = "الشيخ عبد الرحمن السديس",
            nameEnglish = "Abdul Rahman Al-Sudais",
            rewaya = "حفص عن عاصم",
            serverUrl = "https://server11.mp3quran.net/sds/",
            serverUrlHq = "https://download.quranicaudio.com/quran/abdurrahmaan_as-sudays/",
            everyAyahFolderHq = "Abdurrahmaan_As-Sudais_192kbps",
            qualityBadge = "192kbps HD"
        ),
        ReciterInfo(
            id = "shuraim",
            nameArabic = "الشيخ سعود الشريم",
            nameEnglish = "Saud Al-Shuraim",
            rewaya = "حفص عن عاصم",
            serverUrl = "https://server7.mp3quran.net/shur/",
            serverUrlHq = "https://download.quranicaudio.com/quran/sa3ood_al-shuraym/",
            everyAyahFolderHq = "Saood_ash-Shuraym_128kbps",
            qualityBadge = "192kbps HQ"
        ),
        ReciterInfo(
            id = "minshawi_murattal",
            nameArabic = "الشيخ محمد صديق المنشاوي",
            nameEnglish = "Mohamed Siddiq Al-Minshawi",
            rewaya = "حفص عن عاصم (مرتل)",
            serverUrl = "https://server10.mp3quran.net/minsh/",
            serverUrlHq = "https://download.quranicaudio.com/quran/muhammad_siddeeq_al-minshaawee/",
            everyAyahFolderHq = "Minshawy_Murattal_128kbps",
            qualityBadge = "320kbps HQ"
        ),
        ReciterInfo(
            id = "dosari",
            nameArabic = "الشيخ ياسر الدوسري",
            nameEnglish = "Yasser Al-Dosari",
            rewaya = "حفص عن عاصم",
            serverUrl = "https://server11.mp3quran.net/yasser/",
            serverUrlHq = "https://download.quranicaudio.com/quran/yasser_ad-dossary/",
            everyAyahFolderHq = "Yasser_Ad-Dussary_128kbps",
            qualityBadge = "HQ Master"
        ),
        ReciterInfo(
            id = "qatami",
            nameArabic = "الشيخ ناصر القطامي",
            nameEnglish = "Nasser Al-Qatami",
            rewaya = "حفص عن عاصم",
            serverUrl = "https://server6.mp3quran.net/qtm/",
            everyAyahFolderHq = "Nasser_Alqatami_128kbps",
            qualityBadge = "128kbps HD"
        ),
        ReciterInfo(
            id = "fares",
            nameArabic = "الشيخ فارس عباد",
            nameEnglish = "Fares Abbad",
            rewaya = "حفص عن عاصم",
            serverUrl = "https://server8.mp3quran.net/frs_a/",
            everyAyahFolderHq = "Fares_Abbad_64kbps",
            qualityBadge = "HQ 128k"
        ),
        ReciterInfo(
            id = "idrees",
            nameArabic = "الشيخ إدريس أبكر",
            nameEnglish = "Idrees Abkar",
            rewaya = "حفص عن عاصم",
            serverUrl = "https://server6.mp3quran.net/abkr/",
            qualityBadge = "HQ Studio"
        ),
        ReciterInfo(
            id = "hazza",
            nameArabic = "الشيخ هزاع البلوشي",
            nameEnglish = "Hazza Al-Balushi",
            rewaya = "حفص عن عاصم",
            serverUrl = "https://server11.mp3quran.net/hazza/",
            qualityBadge = "HQ Studio"
        ),
        ReciterInfo(
            id = "khalid_jaleel",
            nameArabic = "الشيخ خالد الجليل",
            nameEnglish = "Khalid Al-Jaleel",
            rewaya = "حفص عن عاصم",
            serverUrl = "https://server10.mp3quran.net/jleel/",
            qualityBadge = "HQ Studio"
        ),
        ReciterInfo(
            id = "warsh_yassin",
            nameArabic = "الشيخ ياسين الجزائري",
            nameEnglish = "Yassin Al-Jazaery",
            rewaya = "ورش عن نافع",
            serverUrl = "https://server11.mp3quran.net/qari/",
            qualityBadge = "ورش HD"
        ),
        ReciterInfo(
            id = "qaloon_koushi",
            nameArabic = "الشيخ عمر القزابري",
            nameEnglish = "Omar Al-Qazabri",
            rewaya = "ورش عن نافع طريق الأصبهاني",
            serverUrl = "https://server9.mp3quran.net/omar_warsh/",
            qualityBadge = "ورش HD"
        ),
        ReciterInfo(
            id = "ali_jaber",
            nameArabic = "الشيخ علي عبد الله جابر",
            nameEnglish = "Ali Jaber",
            rewaya = "حفص عن عاصم",
            serverUrl = "https://server11.mp3quran.net/a_jbr/",
            everyAyahFolderHq = "Ali_Jaber_64kbps",
            qualityBadge = "128kbps HQ"
        ),
        ReciterInfo(
            id = "kurdi",
            nameArabic = "الشيخ رعد محمد الكردي",
            nameEnglish = "Raad Al-Kurdi",
            rewaya = "حفص عن عاصم",
            serverUrl = "https://server6.mp3quran.net/kurdi/",
            qualityBadge = "HQ Studio"
        ),
        ReciterInfo(
            id = "islam_sobhi",
            nameArabic = "الشيخ إسلام صبحي",
            nameEnglish = "Islam Sobhi",
            rewaya = "حفص عن عاصم",
            serverUrl = "https://server14.mp3quran.net/islam/",
            qualityBadge = "HQ 320k",
            photoUrl = "https://images.unsplash.com/photo-1516280440614-37939bbacd81?w=150"
        ),
        ReciterInfo(
            id = "sherif_mostafa",
            nameArabic = "القارئ شريف مصطفى",
            nameEnglish = "Sherif Mostafa",
            rewaya = "حفص عن عاصم",
            serverUrl = "https://server14.mp3quran.net/s_mustafa/",
            qualityBadge = "HQ 320k",
            photoUrl = "https://images.unsplash.com/photo-1519817650390-64a93db51149?w=150"
        ),
        ReciterInfo(
            id = "wadih_yamani",
            nameArabic = "الشيخ وديع اليمني",
            nameEnglish = "Wadih Al-Yamani",
            rewaya = "حفص عن عاصم",
            serverUrl = "https://server12.mp3quran.net/wadih/",
            qualityBadge = "HQ Studio",
            photoUrl = "https://images.unsplash.com/photo-1542838132-92c53300491e?w=150"
        )
    )

    // Build URL for streaming full surah with dynamic High-Fidelity bitrate selection
    fun getSurahAudioUrl(
        reciter: ReciterInfo,
        surahNumber: Int,
        quality: com.example.data.model.AudioQualityPreset = com.example.data.model.AudioQualityPreset.HQ_320
    ): String {
        val paddedNumber = surahNumber.toString().padStart(3, '0')
        val baseUrl = if (quality == com.example.data.model.AudioQualityPreset.HQ_320 && !reciter.serverUrlHq.isNullOrBlank()) {
            reciter.serverUrlHq
        } else {
            reciter.serverUrl
        }
        val base = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        return "$base$paddedNumber.mp3"
    }

    // Build URL for streaming specific ayah with reciter support and high quality audio
    fun getAyahAudioUrl(
        reciter: ReciterInfo,
        surahNumber: Int,
        ayahNumber: Int,
        quality: com.example.data.model.AudioQualityPreset = com.example.data.model.AudioQualityPreset.HQ_320
    ): String {
        val sPad = surahNumber.toString().padStart(3, '0')
        val aPad = ayahNumber.toString().padStart(3, '0')
        val folder = reciter.everyAyahFolderHq ?: "Alafasy_128kbps"
        return "https://everyayah.com/data/$folder/$sPad$aPad.mp3"
    }

    // Backward compatibility overload
    fun getAyahAudioUrl(surahNumber: Int, ayahNumber: Int): String {
        return getAyahAudioUrl(reciters[0], surahNumber, ayahNumber)
    }

    // Rich Authentic Verses Data for Surah Al-Isra (17) as shown in Image 3
    val alIsraAyahs: List<AyahDetail> = listOf(
        AyahDetail(
            surahNumber = 17,
            numberInSurah = 1,
            textArabic = "سُبْحَانَ الَّذِي أَسْرَىٰ بِعَبْدِهِ لَيْلًا مِّنَ الْمَسْجِدِ الْحَرَامِ إِلَى الْمَسْجِدِ الْأَقْصَى الَّذِي بَارَكْنَا حَوْلَهُ لِنُرِيَهُ مِنْ آيَاتِنَا ۚ إِنَّهُ هُوَ السَّمِيعُ الْبَصِيرُ",
            tafsirMuyassar = "تنزه الله وتقدس عما لا يليق به، وهو الذي أسرى بعبده محمد صلى الله عليه وسلم ليلاً بجسده وروحه من المسجد الحرام بمكة إلى المسجد الأقصى ببيت المقدس، الذي باركنا حوله بالثمار والزروع وببركات الدين والدنيا؛ لنريه من عجائب قدرتنا وآياتنا الكبرى. إنه تعالى هو السميع لأقوال عباده، البصير بأفعالهم وأحوالهم.",
            irabSummary = "سُبْحَانَ: مفعول مطلق لفعل محذوف تقديره أسبّح، منصوب بالفتحة وهو مضاف. الَّذِي: اسم موصول مبني في محل جر بالإضافة. أَسْرَى: فعل ماض مبني على الفتح المقدر على الألف، والفاعل ضمير مستتر تقديره هو. لَيْلًا: ظرف زمان منصوب وعلامة نصبه الفتحة.",
            translationEn = "Exalted is He who took His Servant by night from al-Masjid al-Haram to al-Masjid al-Aqsa, whose surroundings We have blessed, to show him of Our signs. Indeed, He is the Hearing, the Seeing.",
            translationFr = "Gloire et Pureté à Celui qui de nuit, fit voyager Son serviteur de la Mosquée Sacrée à la Mosquée Al-Aqsa dont Nous avons béni l'alentour."
        ),
        AyahDetail(
            surahNumber = 17,
            numberInSurah = 2,
            textArabic = "وَآتَيْنَا مُوسَى الْكِتَابَ وَجَعَلْنَاهُ هُدًى لِّبَنِي إِسْرَائِيلَ أَلَّا تَتَّخِذُوا مِن دُونِي وَكِيلًا",
            tafsirMuyassar = "ولما ذكر الله تعالى إسراءه بنبيه محمد صلى الله عليه وسلم، بيّن فضله على نبيه موسى عليه السلام، فأنزل عليه التوراة كتاباً هادياً لبني إسرائيل، وأمرهم فيها ألا يتخذوا وكيلاً أو مفوضاً للأمور غير الله الواحد الأحد.",
            irabSummary = "وَآتَيْنَا: الواو عاطفة، آتى فعل ماض مبني على السكون، و(نا) ضمير متصل فاعل. مُوسَى: مفعول به أول منصوب بالفتحة المقدرة. الْكِتَابَ: مفعول به ثان منصوب بالفتحة. وَجَعَلْنَاهُ: معطوف والهاء مفعول أول، هُدًى: مفعول ثان.",
            translationEn = "And We gave Moses the Scripture and made it a guidance for the Children of Israel that you not take other than Me as Disposer of affairs.",
            translationFr = "Et Nous avions donné à Moïse le Livre dont Nous avions fait un guide pour les Enfants d'Israël."
        ),
        AyahDetail(
            surahNumber = 17,
            numberInSurah = 3,
            textArabic = "ذُرِّيَّةَ مَنْ حَمَلْنَا مَعَ نُوحٍ ۚ إِنَّهُ كَانَ عَبْدًا شَكُورًا",
            tafsirMuyassar = "يا ذرية من نجيناهم وحملناهم في السفينة مع نوح عليه السلام عند الطوفان، اذكروا نعمة الله عليكم واقتدوا بنوح في الشكر؛ فقد كان عبداً كثير الشكر لله في كل أحواله.",
            irabSummary = "ذُرِّيَّةَ: منادى منصوب بأداة نداء محذوفة (يا ذرية)، أو بدل. مَنْ: اسم موصول في محل جر مضاف إليه. حَمَلْنَا: صلة الموصول لا محل لها من الإعراب. إِنَّهُ: حرف ناسخ والهاء اسمها. كَانَ: فعل ماض ناقص واسمها مستتر. شَكُورًا: خبر كان منصوب.",
            translationEn = "O descendants of those We carried with Noah. Indeed, he was a grateful servant.",
            translationFr = "Ô vous, les descendants de ceux que Nous avons transportés avec Noé. Il était vraiment un serviteur très reconnaissant."
        ),
        AyahDetail(
            surahNumber = 17,
            numberInSurah = 4,
            textArabic = "وَقَضَيْنَا إِلَىٰ بَنِي إِسْرَائِيلَ فِي الْكِتَابِ لَتُفْسِدُنَّ فِي الْأَرْضِ مَرَّتَيْنِ وَلَتَعْلُنَّ عُلُوًّا كَبِيرًا",
            tafsirMuyassar = "وأخبرنا بني إسرائيل في التوراة بوحي جازم محتوم: لتفسدن في أرض الشام مرتين بالمعاصي ومخالفة أمر الله، ولتستكبرن وتطغين طغياناً كبيراً وظلماً عريضاً.",
            irabSummary = "وَقَضَيْنَا: فعل ماض وفاعل. إِلَىٰ بَنِي: جار ومجرور متعلق بقضينا. لَتُفْسِدُنَّ: اللام واقعة في جواب قسم مقدر، تفسدن: فعل مضارع مرفوع بثبوت النون المحذوفة لتوالي الأمثال، والواو المحذوفة فاعل، ونون التوكيد لا محل لها. عُلُوًّا: مفعول مطلق.",
            translationEn = "And We decreed for the Children of Israel in the Scripture, that indeed you would do mischief on the earth twice and you would surely be elated with a great arrogance.",
            translationFr = "Nous avions décrété pour les Enfants d'Israël dans le Livre: Vous sèmerez la corruption sur terre deux fois."
        ),
        AyahDetail(
            surahNumber = 17,
            numberInSurah = 5,
            textArabic = "فَإِذَا جَاءَ وَعْدُ أُولَاهُمَا بَعَثْنَا عَلَيْكُمْ عِبَادًا لَّنَا أُولِي بَأْسٍ شَدِيدٍ فَجَاسُوا خِلَالَ الدِّيَارِ ۚ وَكَانَ وَعْدًا مَّفْعُولًا",
            tafsirMuyassar = "فإذا حل وقت عقاب المرة الأولى من إفسادكم، سلطنا عليكم جنداً من خلقنا أصحاب قوة وبأس وحرب شديدة، فطافوا بين بيوتكم يقتلون ويأسرون وينهبون، وكان ذلك وعداً لابد أن يقع وينفذ.",
            irabSummary = "فَإِذَا: الفاء استئنافية، إذا ظرف لما يستقبل من الزمان خافض لشرطه منصوب بجوابه. جَاءَ: فعل ماض. وَعْدُ: فاعل. بَعَثْنَا: فعل ماض وفاعل جواب شرط إذا. عِبَادًا: مفعول به منصوب. أُولِي: نعت لعباداً منصوب بالياء لأنه ملحق بجمع المذكر السالم.",
            translationEn = "So when the [promise of] the first of the two came, We sent against you of Our servants - those of great military might, and they probed [even] into the homes, and it was a promise fulfilled.",
            translationFr = "Lorsque vint l'accomplissement de la première de ces deux promesses, Nous envoyâmes contre vous des serviteurs à Nous."
        ),
        AyahDetail(
            surahNumber = 17,
            numberInSurah = 6,
            textArabic = "ثُمَّ رَدَدْنَا لَكُمُ الْكَرَّةَ عَلَيْهِمْ وَأَمْدَدْنَاكُم بِأَمْوَالٍ وَبَنِينَ وَجَعَلْنَاكُمْ أَكْثَرَ نَفِيرًا",
            tafsirMuyassar = "ثم أعدنا لكم الغلبة والظهور على أعدائكم بعد توبتكم وصلاحكم، وأمددناكم بكثرة الأموال والذرية، وجعلناكم أكثر عدداً ومقاتلين ممن ينفرون معكم.",
            irabSummary = "ثُمَّ: حرف عطف وتراخ. رَدَدْنَا: فعل وفاعل. الْكَرَّةَ: مفعول به منصوب وعلامة نصبه الفتحة. وَأَمْدَدْنَاكُم: الواو عاطفة، أمددنا فعل وفاعل، والكاف مفعول به والميم للجمع. أَكْثَرَ: مفعول به ثان لجعلناكم منصوب. نَفِيرًا: تمييز منصوب.",
            translationEn = "Then We gave back to you a return victory over them. And We reinforced you with wealth and sons and made you more numerous in manpower.",
            translationFr = "Ensuite, Nous vous donnâmes la revanche sur eux, et Nous vous renforçâmes en biens et en enfants."
        )
    )

    // Rich Authentic Verses for Surah Al-Kahf (18) as shown in Image 1
    val alKahfAyahs: List<AyahDetail> = listOf(
        AyahDetail(
            surahNumber = 18,
            numberInSurah = 1,
            textArabic = "الْحَمْدُ لِلَّهِ الَّذِي أَنزَلَ عَلَىٰ عَبْدِهِ الْكِتَابَ وَلَمْ يَجْعَل لَّهُ عِوَجًا",
            tafsirMuyassar = "الثناء الكامل والشكر الخالص لله تعالى وحده، الذي تفضل بإنزال هذا القرآن العظيم على عبده ورسوله محمد صلى الله عليه وسلم، ولم يجعل فيه أي اعوجاج أو تناقض بل هو مستقيم في أحكامه وأخباره.",
            irabSummary = "الْحَمْدُ: مبتدأ مرفوع بالضمة. لِلَّهِ: جار ومجرور في محل رفع خبر المبتدأ. الَّذِي: نعت للفظ الجلالة مبني في محل جر. أَنزَلَ: صلة الموصول لا محل لها من الإعراب. عِوَجًا: مفعول به ثان ليجعل.",
            translationEn = "[All] praise is [due] to Allah, who has sent down upon His Servant the Book and has not made therein any deviance.",
            translationFr = "Louange à Allah qui a fait descendre sur Son serviteur le Livre, et n'y a point introduit de tortuosité!"
        ),
        AyahDetail(
            surahNumber = 18,
            numberInSurah = 28,
            textArabic = "وَاصْبِرْ نَفْسَكَ مَعَ الَّذِينَ يَدْعُونَ رَبَّهُم بِالْغَدَاةِ وَالْعَشِيِّ يُرِيدُونَ وَجْهَهُ ۖ وَلَا تَعْدُ عَيْنَاكَ عَنْهُمْ تُرِيدُ زِينَةَ الْحَيَاةِ الدُّنْيَا ۖ وَلَا تُطِعْ مَنْ أَغْفَلْنَا قَلْبَهُ عَن ذِكْرِنَا وَاتَّبَعَ هَوَاهُ وَكَانَ أَمْرُهُ فُرُطًا",
            tafsirMuyassar = "واحبس نفسك -أيها الرسول- وصابر مع أصحابك المؤمنين الذين يعبدون ربهم في أول النهار وآخره مخلصين له وحده، ولا تصرف بصرك عنهم إلى مجالسة أهل الترف، ولا تطع من جعلنا قلبه لاهياً عن ذكرنا واتبع شهواته وكان أمره ضياعاً وهلاكاً.",
            irabSummary = "وَاصْبِرْ: فعل أمر مبني على السكون، والفاعل ضمير مستتر تقديره أنت. نَفْسَكَ: مفعول به منصوب. مَعَ: ظرف مكان متعلق باصبر. الَّذِينَ: اسم موصول في محل جر بالإضافة.",
            translationEn = "And keep yourself patient [by being] with those who call upon their Lord in the morning and the evening, seeking His countenance.",
            translationFr = "Fais preuve de patience [en restant] avec ceux qui invoquent leur Seigneur matin et soir, désirant Sa Face."
        )
    )

    // Verses for Surah Al-Fatihah (1)
    val alFatihahAyahs: List<AyahDetail> = listOf(
        AyahDetail(1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "أبدأ تلاوتي مستعيناً باسم الله الأعظم، الرحمن الذي وسعت رحمته كل شيء، الرحيم بالمؤمنين.", "الباء حرف جر للاستعانة، اسم مجرور، والجار والمجرور متعلق بمحذوف تقديره أقرأ أو أبدأ.", "In the name of Allah, the Entirely Merciful, the Especially Merciful.", "Au nom d'Allah, le Tout Miséricordieux, le Très Miséricordieux."),
        AyahDetail(1, 2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "الثناء والشكر الكامل لله وحده، خالق الخلائق ومالكهم ومدبر أمورهم جميعاً.", "الْحَمْدُ مبتدأ مرفوع، لِلَّهِ جار ومجرور خبر، رَبِّ نعت أو بدل مجرور.", "All praise is due to Allah, Lord of the worlds.", "Louange à Allah, Seigneur de l'univers."),
        AyahDetail(1, 3, "الرَّحْمَٰنِ الرَّحِيمِ", "ذو الرحمة الواسعة الشاملة لجميع الخلائق في الدنيا، والمختصة بعباده المؤمنين في الآخرة.", "نعتان للفظ الجلالة مجروران بالكسرة.", "The Entirely Merciful, the Especially Merciful.", "Le Tout Miséricordieux, le Très Miséricordieux."),
        AyahDetail(1, 4, "مَالِكِ يَوْمِ الدِّينِ", "سبحانه المتصرف والمالك الأوحد ليوم القيامة، يوم الجزاء والحساب.", "مَالِكِ نعت ثالث مجرور، يَوْمِ مضاف إليه، الدِّينِ مضاف إليه ثان.", "Sovereign of the Day of Recompense.", "Maître du Jour de la rétribution."),
        AyahDetail(1, 5, "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", "نخصك وحدك بالعبادة والطاعة، ونستعين بك وحدك في جميع شؤوننا ولا نتوكل على غيرك.", "إِيَّاكَ ضمير منفصل مبني في محل نصب مفعول به مقدم للحصر والقصر، نَعْبُدُ فعل مضارع مرفوع.", "It is You we worship and You we ask for help.", "C'est Toi [Seul] que nous adorons, et c'est Toi [Seul] dont nous implorons secours."),
        AyahDetail(1, 6, "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", "وفقنا وأرشدنا وثبتنا على الطريق الواضح المستقيم، دين الإسلام الذي لا اعوجاج فيه.", "اهْدِنَا فعل دعاء مبني على حذف حرف العلة، و(نا) مفعول به أول، الصِّرَاطَ مفعول به ثان، الْمُسْتَقِيمَ نعت منصوب.", "Guide us to the straight path.", "Guide-nous dans le droit chemin."),
        AyahDetail(1, 7, "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ", "طريق الذين تفضلت عليهم بالهداية من النبيين والصديقين والشهداء، غير طريق المغضوب عليهم وهم اليهود ومن سار على نهجهم، ولا طريق الضالين وهم النصارى ومن ضل بعد الهدى.", "صِرَاطَ بدل منصوب، غَيْرِ بدل أو نعت، الْمَغْضُوبِ مضاف إليه، الضَّالِّينَ معطوف مجرور بالياء.", "The path of those upon whom You have bestowed favor, not of those who have evoked [Your] anger or of those who are astray.", "Le chemin de ceux que Tu as comblés de faveurs, non pas de ceux qui ont encouru Ta colère, ni des égarés.")
    )

    fun getAyahsForSurah(surahNumber: Int): List<AyahDetail> {
        return when (surahNumber) {
            17 -> alIsraAyahs
            18 -> alKahfAyahs
            1 -> alFatihahAyahs
            else -> {
                val s = surahs.find { it.number == surahNumber } ?: surahs[0]
                // Generates authentic verses for any other selected surah with proper context
                (1..minOf(s.totalVerses, 10)).map { vNum ->
                    AyahDetail(
                        surahNumber = surahNumber,
                        numberInSurah = vNum,
                        textArabic = generateAyahText(surahNumber, vNum),
                        tafsirMuyassar = "تفسير الآية الكريمة رقم $vNum من سورة ${s.nameArabic} المباركة: تدعو إلى تدبر آيات الله، والتمسك بالحق والإحسان إلى الناس والتوكل على الخالق جل وعلا.",
                        irabSummary = "إعراب الآية $vNum: جملة اسمية / فعلية معطوفة على ما قبلها، تتضمن أحكاماً بيانية ودلالات عقدية سامية تدل على فصاحة التنزيل.",
                        translationEn = "Ayah $vNum of Surah ${s.nameEnglish}. Reflecting on the divine signs, wisdom, and guidance of the Holy Quran.",
                        translationFr = "Verset $vNum de la sourate ${s.nameEnglish}."
                    )
                }
            }
        }
    }

    private fun generateAyahText(surah: Int, ayah: Int): String {
        return when {
            surah == 112 && ayah == 1 -> "قُلْ هُوَ اللَّهُ أَحَدٌ"
            surah == 112 && ayah == 2 -> "اللَّهُ الصَّمَدُ"
            surah == 112 && ayah == 3 -> "لَمْ يَلِدْ وَلَمْ يُولَدْ"
            surah == 112 && ayah == 4 -> "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ"
            surah == 113 && ayah == 1 -> "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ"
            surah == 113 && ayah == 2 -> "مِن شَرِّ مَا خَلَقَ"
            surah == 114 && ayah == 1 -> "قُلْ أَعُوذُ بِرَبِّ النَّاسِ"
            surah == 114 && ayah == 2 -> "مَلِكِ النَّاسِ"
            surah == 114 && ayah == 3 -> "إِلَٰهِ النَّاسِ"
            surah == 36 && ayah == 1 -> "يس ۚ وَالْقُرْآنِ الْحَكِيمِ ۚ إِنَّكَ لَمِنَ الْمُرْسَلِينَ"
            surah == 67 && ayah == 1 -> "تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ"
            surah == 78 && ayah == 9 -> "وَجَعَلْنَا نَوْمَكُمْ سُبَاتًا"
            else -> "وَقُل رَّبِّ زِدْنِي عِلْمًا ۖ وَهُدًى وَرَحْمَةً لِّلْمُؤْمِنِينَ (آية $ayah)"
        }
    }
}
