package com.beaterboofs.missionout.ui

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import com.beaterboofs.missionout.LoginViewModel
import com.beaterboofs.missionout.R
import kotlinx.android.synthetic.main.navigation_activity.*


class MainActivity : AppCompatActivity(),
    SignInFragment.OnLoginChangeListener,
    OverviewFragment.OnFragmentInteractionListener{

    lateinit var loginViewModel: LoginViewModel

    override fun onFragmentInteraction(uri: Uri) {
        //TODO -- enable interaction listener
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navigation_activity)

        // Set up viewmodel for toolbar
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java).apply {
            user.observe(this@MainActivity, Observer {
                toolbar.title = loginViewModel.user.value?.displayName
                toolbar.subtitle = loginViewModel.user.value?.email?.substringAfter("@")
            })
        }

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment? ?: return

        val navController = host.navController
        menu_sign_out.setOnClickListener {
            navController.navigate(R.id.signInFragment)
        }
    }

    override fun onLoginChange(isLoggedIn: Boolean) {
        if (isLoggedIn){
            toolbar.visibility = View.VISIBLE
        }
        else{
            toolbar.visibility = View.INVISIBLE
        }
    }

}
