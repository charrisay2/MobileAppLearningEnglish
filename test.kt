import com.google.ai.client.generativeai.GenerativeModel
import com.example.BuildConfig

fun test() {
    val model = GenerativeModel("gemini-1.5-flash", BuildConfig.GEMINI_API_KEY)
}
