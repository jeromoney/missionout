package com.beaterboofs.missionout

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.main_activity.*


class MainActivity : AppCompatActivity(), SignInFragment.OnFragmentInteractionListener, OverviewFragment.OnFragmentInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {
        //TODO -- enable interaction listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navigation_activity)

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.overviewFragment) as NavHostFragment? ?: return

        val navController = host.navController



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
        // val fragment = SignInFragment.newInstance()

    }

}
