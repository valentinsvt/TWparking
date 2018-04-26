package com.lzm.svt.twparking

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.lzm.svt.twparking.contracts.FragmentContracts
import com.lzm.svt.twparking.contracts.WireframeContracts
import com.lzm.svt.twparking.modules.charges.ChargesListFragment
import com.lzm.svt.twparking.modules.charges.dummy.DummyContent
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(),
        NavigationView.OnNavigationItemSelectedListener,
        FragmentContracts.OnFragmentInteractionListener,
        ChargesListFragment.OnListFragmentInteractionListener,
        WireframeContracts.WireframeDelegate {

    override fun onChargePressed(item: DummyContent.DummyItem?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val sharedPref = this.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE)
                ?: return
        val token = sharedPref.getString(getString(R.string.pref_token_key), "NA")
        val userId = sharedPref.getInt(getString(R.string.pref_userId_key), -1)
        val userName = sharedPref.getString(getString(R.string.pref_userName_key), "NA")
        val userMail = sharedPref.getString(getString(R.string.pref_userEmail_key), "NA")

        println("--------------------------------------------------------------")
        println("$userId, $userName, $userMail")
        println("--------------------------------------------------------------")

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        MainWireframe.getCurrentInstance(this).open(this, item)
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onFragmentInteraction(uri: Uri) {}

    override fun showFragment(fragment: Fragment) {
        replaceFragmentSafely(fragment, "test", false, R.id.fragment_container)
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}

fun AppCompatActivity.replaceFragmentSafely(fragment: Fragment, tag: String, allowStateLoss: Boolean = false, @IdRes containerViewId: Int) {
    val ft = supportFragmentManager
            .beginTransaction()
            .replace(containerViewId, fragment, tag)
    if (!supportFragmentManager.isStateSaved) {
        ft.commit()
    } else if (allowStateLoss) {
        ft.commitAllowingStateLoss()
    }
}

