package com.beaterboofs.missionout

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.beaterboofs.missionout.ui.main.GoogleSignInActivity
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        // get list of accounts
        val am: AccountManager = AccountManager.get(this)
        val accounts: Array<out Account> = am.getAccountsByType("com.google")



        if (savedInstanceState == null) {
            // why is this needed??
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.container, SigninFragment.newInstance())
//                .commitNow()

        }
    }

    override fun onStart() {
        super.onStart()


        // direct user based on sign in status
        val user = FirebaseAuth.getInstance().currentUser



        val intent: Intent
        //if (user == null) {
            // if user has not signed in, direct them to the sign in page
            intent = Intent(this, GoogleSignInActivity::class.java).apply {
                putExtra("user", "nothing")
            }

//        else {
//            // user has already signed in,
//            intent = Intent(this, MissionActivity::class.java).apply {
//                putExtra("user", "nothing")
//            }
//        }
        startActivity(intent)
    }

}
