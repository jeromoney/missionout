package com.beaterboofs.missionout

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.navigation_activity.*


class MainActivity : AppCompatActivity(),
    SignInFragment.OnFragmentInteractionListener,
    OverviewFragment.OnFragmentInteractionListener{

    override fun onFragmentInteraction(uri: Uri) {
        //TODO -- enable interaction listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navigation_activity)

        // Set up viewmodel for toolbar


        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment? ?: return

        val navController = host.navController

        navController.addOnDestinationChangedListener{
            controller, destination, arguments ->
            toolbar.title = "Dickbutt"
            toolbar.subtitle = "hello world"
        }
    }

}
