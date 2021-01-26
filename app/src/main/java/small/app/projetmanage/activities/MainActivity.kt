package small.app.projetmanage.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import small.app.projetmanage.R
import small.app.projetmanage.firebase.Firestore

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        nav_view.setNavigationItemSelectedListener(this)
        setupDefaultActionBar()
        Firestore.loginUser.observe(this, {
            if (it != null) updateNavigationUserDetails()
        })
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            doubleBackToExit()
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_profile -> {
                Toast.makeText(applicationContext, "My Profile", Toast.LENGTH_LONG).show()
                val navController = findNavController(R.id.fragment_nav)
                navController.navigate(R.id.profileFragment)

                //startActivity(Intent(this@MainActivity, ProfilActivity::class.java))
            }
            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                navigateFirstTabWithClearStack()
                //setupNavDrawer(drawer_layout,nav_view)
                drawer_layout.closeDrawer(GravityCompat.START)
            }

        }
        return true
    }


    fun navigateFirstTabWithClearStack() {
        val navController = findNavController(R.id.fragment_nav)
        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_nav) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.nav)
        graph.startDestination = R.id.introFragment

        navController.graph = graph


    }

    fun updateNavigationUserDetails() {
        // The instance of the header view of the navigation view.

        Glide.with(this).load(this)
            .load(Firestore.loginUser.value!!.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(iv_user_image)

        tv_username.text = Firestore.loginUser.value!!.name
    }


}