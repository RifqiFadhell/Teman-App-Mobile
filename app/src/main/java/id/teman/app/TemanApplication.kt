package id.teman.app

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TemanApplication : Application() {

    override fun onCreate() {
        FirebaseApp.initializeApp(this)
        super.onCreate()
    }
}