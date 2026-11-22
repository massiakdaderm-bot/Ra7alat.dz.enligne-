package com.example.data

import com.example.data.model.Trip
import com.example.data.model.TripReview

object SampleData {

    val sampleTrips: List<Trip> = emptyList()

    val sampleReviews = listOf(
        TripReview(
            id = "rev_1",
            tripId = "1",
            userId = "user_1",
            userName = "محمد الأمين من البليدة",
            userAvatar = "",
            rating = 5,
            comment = "تنظيم احترافي جداً وإقامة مريحة في تاغيت. الأجواء الصحراوية كانت ساحرة وخدمة الوكالة ممتازة! أنصح الجميع بتجربتها.",
            createdAt = "2026-08-20"
        ),
        TripReview(
            id = "rev_2",
            tripId = "1",
            userId = "user_2",
            userName = "فاطمة الزهراء من وهران",
            userAvatar = "",
            rating = 5,
            comment = "سهرة الشاي حول النار والجولات في الكثبان كانت خيالية. شكراً لوكالة الأطلس على حسن الاستقبال والالتزام بالمواعيد.",
            createdAt = "2026-08-22"
        ),
        TripReview(
            id = "rev_3",
            tripId = "2",
            userId = "user_3",
            userName = "كريم من العاصمة",
            userAvatar = "",
            rating = 5,
            comment = "مسار المشي في تيكجدة ممتع ومنعش جداً. المرشدين كانوا في قمة اللباقة والحرص على سلامة الجميع.",
            createdAt = "2026-08-15"
        )
    )

    val sampleBookings = listOf(
        com.example.data.model.Booking(
            id = 1,
            tripId = 1,
            tripTitle = "voyage",
            destinationWilaya = "الجزائر العاصمة (الجزائر العاصمة)",
            destinationPlace = "الجزائر العاصمة",
            departureDate = "2026-09-06",
            organizerName = "ra7alat",
            organizerPhone = "0673085612",
            userEmail = "akekehdwiw@gmail.com",
            userName = "akekehdwiw",
            userPhone = "0673085612",
            seatsCount = 1,
            totalPriceDzd = 18000.0,
            bookingReference = "DZ-611220",
            bookingDate = "2026-09-06",
            status = "UPCOMING",
            specialNotes = "تذكرة رحلة DZ-611220"
        )
    )
}
