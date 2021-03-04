package small.app.projetmanage.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import small.app.projetmanage.R
import small.app.projetmanage.databinding.ActivityMainBinding
import small.app.projetmanage.firebase.Firestore
import small.app.projetmanage.utils.Constants
import small.app.projetmanage.utils.Constants.showImagePicker
import small.app.projetmanage.utils.Constants.updatePictureInFragment

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController


    fun setNavController(navController: NavController) {
        this.navController = navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
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


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == BaseActivity.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImagePicker(this)
            }
        } else {
            Toast.makeText(
                this,
                "Oops, you just denied the permission for storage. You must accept to pick a profil picture.",
                Toast.LENGTH_LONG
            ).show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("MainActivity", "onActivityResult")
        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onBackPressed() {
        Log.d("MainActivity", "OnBackPressed")
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            Log.d("MainActivity", "closeDrawer")

            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            Log.d("MainActivity", "doubleBackToExit")

            doubleBackToExit()
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_profile -> {
                drawer_layout.closeDrawer(GravityCompat.START)

                Toast.makeText(applicationContext, "My Profile", Toast.LENGTH_LONG).show()
                val navController = findNavController(R.id.fragment_nav)
                navController.navigate(R.id.profileFragment)

                //startActivity(Intent(this@MainActivity, ProfilActivity::class.java))
            }
            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()

                this.getSharedPreferences(
                    Constants.PROJEMANAGE_PREFRENCES,
                    AppCompatActivity.MODE_PRIVATE
                ).edit().putBoolean(Constants.FCM_TOKEN_UPDATED, false).apply()
                navigateFirstTabWithClearStack(R.id.introFragment)
                //setupNavDrawer(drawer_layout,nav_view)
                drawer_layout.closeDrawer(GravityCompat.START)
                //Do we need to clear the main fragment preferences ?
            }

        }
        return true
    }


    fun navigateFirstTabWithClearStack(id: Int) {
        val navController = findNavController(R.id.fragment_nav)
        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_nav) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.nav)
        graph.startDestination = id

        navController.graph = graph


    }

    private fun updateNavigationUserDetails() {
        // The instance of the header view of the navigation view.
        if (iv_user_image != null) {
            updatePictureInFragment(
                this,
                Firestore.loginUser.value!!.image,
                R.drawable.ic_user_place_holder,
                iv_user_image
            )

        }

        if (tv_username != null) tv_username.text = Firestore.loginUser.value!!.name
    }

    fun setActionBarTitle(title: String?) {
        supportActionBar!!.title = title
    }


}