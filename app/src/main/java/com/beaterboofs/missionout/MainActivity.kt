package com.beaterboofs.missionout

import android.accounts.Account
import android.accounts.AccountManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment


class MainActivity : AppCompatActivity(),
    SignInFragment.OnFragmentInteractionListener,
    OverviewFragment.OnFragmentInteractionListener{
    override fun onFragmentInteraction(uri: Uri) {
        //TODO -- enable interaction listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navigation_activity)

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment? ?: return

        val navController = host.navController


    }

}
