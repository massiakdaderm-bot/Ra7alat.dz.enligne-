package com.example.util

object TripEquipmentManager {
    fun getEquipmentForTrip(tripType: String, durationDays: Int, isAr: Boolean): List<String> {
        val list = mutableListOf<String>()
        if (isAr) {
            list.add("بطاقة الهوية الوطنية أو جواز السفر (إجباري) 🪪")
            list.add("حقيبة سفر خفيفة ملائمة للتنقل 🎒")
            list.add("حذاء مريح مخصص للمشي والتجوال 👟")
            list.add("شاحن هاتف محمول وبنك طاقة (PowerBank) 🔋")
            list.add("حقيبة إسعافات أولية وأدوية شخصية 💊")

            when {
                tripType.contains("صحراوية") -> {
                    list.add("شاش صحراوي أو غطاء رأس واقٍ من الشمس والرمال 🧣")
                    list.add("نظارات شمسية وواقي شمس فعال 🕶️")
                    list.add("ملابس قطنية نهاراً وملابس صوفية دافئة جداً لليل الصحراء 🧥")
                    list.add("قارورة ماء حرارية لحفظ البرودة 💧")
                }
                tripType.contains("جبلية") -> {
                    list.add("حذاء جبلي متين ضد الانزلاق 🥾")
                    list.add("عصا تسلق للممرات الوعرة 🦯")
                    list.add("سترة مضادة للمطر والرياح (K-Way) 🌧️")
                }
                tripType.contains("شاطئية") -> {
                    list.add("ملابس سباحة ومناشف بحرية 🩱")
                    list.add("واقي شمس مقاوم للماء وقبعة بحر 👒")
                }
                tripType.contains("تخييم") -> {
                    list.add("كيس نوم دافئ (Sleeping Bag) ⛺")
                    list.add("مصباح يدوي أو مصباح رأس (Frontale) 🔦")
                    list.add("طارد الحشرات والبعوض 🦟")
                }
            }
        } else {
            list.add("Carte Nationale d'Identité ou Passeport (Obligatoire) 🪪")
            list.add("Sac à dos léger et ergonomique 🎒")
            list.add("Chaussures de marche confortables 👟")
            list.add("Chargeur de téléphone et batterie externe (PowerBank) 🔋")
            list.add("Trousse de premiers secours et médicaments 💊")

            when {
                tripType.contains("صحراوية") || tripType.contains("Sahara") -> {
                    list.add("Chèche saharien et lunettes de soleil 🧣")
                    list.add("Crème solaire haute protection 🕶️")
                    list.add("Vêtements chauds pour la nuit fraîche du désert 🧥")
                    list.add("Gourde isotherme pour l'eau 💧")
                }
                tripType.contains("جبلية") || tripType.contains("Montagne") -> {
                    list.add("Chaussures de randonnée antidérapantes 🥾")
                    list.add("Bâtons de randonnée 🦯")
                    list.add("Veste imperméable coupe-vent 🌧️")
                }
                tripType.contains("شاطئية") || tripType.contains("Plage") -> {
                    list.add("Maillot et serviette de bain 🩱")
                    list.add("Protection solaire et chapeau 👒")
                }
                tripType.contains("تخييم") || tripType.contains("Camping") -> {
                    list.add("Sac de couchage chaud (Sleeping Bag) ⛺")
                    list.add("Lampe frontale ou torche 🔦")
                    list.add("Répulsif anti-moustiques 🦟")
                }
            }
        }
        return list
    }
}
