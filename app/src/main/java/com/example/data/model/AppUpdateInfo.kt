package com.example.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppUpdateInfo(
    @SerialName("id")
    val id: Long = 1L,
    @SerialName("min_version_code")
    val minVersionCode: Int = 1,
    @SerialName("latest_version_code")
    val latestVersionCode: Int = 1,
    @SerialName("latest_version_name")
    val latestVersionName: String = "1.0",
    @SerialName("is_force_update")
    val isForceUpdate: Boolean = false,
    @SerialName("title_ar")
    val titleAr: String = "تحديث جديد متوفر لتطبيق رحلات 🇩🇿",
    @SerialName("title_fr")
    val titleFr: String = "Nouvelle mise à jour disponible 🇩🇿",
    @SerialName("message_ar")
    val messageAr: String = "يتوفر إصدار جديد من تطبيق رحلات الجزائر (Ra7alat DZ). يرجى التحديث للاستفادة من أحدث الميزات والإصلاحات وضمان استمرار عمل حسابك وحجوزاتك.",
    @SerialName("message_fr")
    val messageFr: String = "Une nouvelle version de Ra7alat DZ est disponible. Veuillez mettre à jour pour profiter des dernières fonctionnalités et améliorations.",
    @SerialName("changelog_ar")
    val changelogAr: List<String> = listOf(
        "إضافة روزنامة تقويم تفاعلية لتحديد تواريخ الانطلاق والعودة بدقة",
        "الاستبعاد التلقائي للرحلات المنتهية من الاستكشاف وفرزها في المفضلة",
        "تحسينات في سرعة التزامن السحابي وحفظ صور الملف الشخصي",
        "إصلاحات عامة في الأداء والاتصال"
    ),
    @SerialName("changelog_fr")
    val changelogFr: List<String> = listOf(
        "Calendrier interactif pour les dates de départ et retour",
        "Filtrage automatique des voyages expirés",
        "Améliorations de synchronisation et de photos de profil",
        "Corrections de bugs et performances optimisées"
    ),
    @SerialName("download_url")
    val downloadUrl: String = "https://t.me/ra7alatdz"
) {
    val releaseNotesAr: List<String> get() = changelogAr
    val releaseNotesFr: List<String> get() = changelogFr
}
