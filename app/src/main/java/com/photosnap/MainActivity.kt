package com.photosnap

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.photosnap.data.database.ReverseDbHelper
import com.photosnap.util.ManagePermissions

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val PermissionsRequestCode = 123
    private lateinit var managePermissions: ManagePermissions

    private lateinit var bottomNavigationView: BottomNavigationView

    interface callBackCropyImage{
        fun takeUri(uri: Uri)
    }

    companion object {
        var permissionsList = listOf<String>(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
        )
        lateinit var mycallBack: callBackCropyImage

        fun setListener(myBack: callBackCropyImage){
            mycallBack = myBack
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController: NavController = findNavController(R.id.nav_host_fragment)

        supportActionBar?.hide()

        ReverseDbHelper.initDatabaseInstance(this)

        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigatin_view)
        bottomNavigationView.setupWithNavController(navController)

        // Initialize a new instance of ManagePermissions class
        managePermissions = ManagePermissions(this,permissionsList,PermissionsRequestCode)

        managePermissions.checkPermissions()
    }


    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var imageUri: Uri? = data?.data
        Log.v("Hello",imageUri.toString())
        if(imageUri!=null)
            mycallBack.takeUri(imageUri!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        ReverseDbHelper.closeDatabase()
    }


}