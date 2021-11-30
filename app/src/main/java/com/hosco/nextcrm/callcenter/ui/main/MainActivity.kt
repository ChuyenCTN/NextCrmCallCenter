package com.hosco.nextcrm.callcenter.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.app_bar_main.view.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
        binding.appBarMain.toolbar

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_note, R.id.nav_contact, R.id.nav_history, R.id.nav_setting
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        setToolBarTitle(getString(R.string.txt_title_all_notes))
        binding.navView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_note -> {
                    imgAddNote.visibility = View.VISIBLE
                    imgFillter.visibility = View.VISIBLE
                    it.title.let { setToolBarTitle(getString(R.string.txt_title_all_notes)) }
                    true
                }
                R.id.nav_contact -> {
                    imgAddNote.visibility = View.GONE
                    imgFillter.visibility = View.GONE
                    it.title.let { setToolBarTitle(it as String) }
                    true
                }
                R.id.nav_history -> {
                    imgAddNote.visibility = View.GONE
                    imgFillter.visibility = View.GONE
                    it.title.let { setToolBarTitle(it as String) }
                    true
                }
                R.id.nav_setting -> {
                    imgAddNote.visibility = View.GONE
                    imgFillter.visibility = View.GONE
                    it.title.let { setToolBarTitle(it as String) }
                    true
                }
                else -> {
                    false
                }
            }
        }

    }

    fun setToolBarTitle(title: String) {
        binding.appBarMain.tvTitle.text = title
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}