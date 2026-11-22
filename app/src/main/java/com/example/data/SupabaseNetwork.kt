package com.example.data

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import io.github.jan.supabase.storage.Storage
import kotlinx.serialization.json.Json

object SupabaseNetwork {
    const val SUPABASE_URL = "https://abzxeetfxnejlurqixun.supabase.co"
    const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImFienhlZXRmeG5lamx1cnFpeHVuIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODcxNTQ5MzIsImV4cCI6MjEwMjczMDkzMn0.7w9NKOvO8yE5sXrjc1uUwLuzvL5uHMFgdm1Uxw1xAUA"
    const val SUPABASE_API_KEY = "sb_secret_HnLCB2TN5Hu4eSMNckMMrA_Y1T7Izwv"
    const val SUPABASE_PUBLISHABLE_KEY = "sb_publishable_QpdteMf5IsZd3PZNB1-8nQ_qaL6oeG8"
    const val SUPABASE_ROLE_SERVES = "sb_publishable_QpdteMf5IsZd3PZNB1-8nQ_qaL6oeG8"

    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        encodeDefaults = true
        explicitNulls = false
    }

    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        defaultSerializer = KotlinXSerializer(json)
        install(Postgrest)
        install(Auth)
        install(Storage)
    }
}
