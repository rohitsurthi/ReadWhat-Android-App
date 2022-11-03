package com.liteloapps.readwhat.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.liteloapps.readwhat.R
import com.liteloapps.readwhat.db.ArticleDao
import com.liteloapps.readwhat.db.ArticleDatabase
import com.liteloapps.readwhat.repository.NewsRepository
import com.liteloapps.readwhat.viewModel.NewsViewModel
import com.liteloapps.readwhat.viewModel.NewsViewModelProviderFactory

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: NewsViewModel

    lateinit var myDB : ArticleDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myDB = ArticleDatabase.getDatabase(this)

        val newsRepository = NewsRepository(myDB)
        val viewModelProviderFactory = NewsViewModelProviderFactory(application, newsRepository)

        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)
        /*
        * Navigation component set up!
        * */
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNavigationView : BottomNavigationView = findViewById(R.id.bottom_nav_view)
        bottomNavigationView.setupWithNavController(navController)

    }
}