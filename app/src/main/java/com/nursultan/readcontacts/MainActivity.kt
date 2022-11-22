package com.nursultan.readcontacts

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.Contacts
import android.util.Log
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission()
    }

    private fun checkPermission() {
        val contactsPermission = ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
        if (contactsPermission) {
            requestContacts()
        } else {
            requestPermission()
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_CONTACTS),
            READ_CONTACTS_RC
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == READ_CONTACTS_RC && grantResults.isNotEmpty()) {
            val permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (permissionGranted) {
                requestContacts()
            } else {
                Log.d("MainActivity", "Contacts read permission denied!")
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun requestContacts() {
        val cursor = contentResolver?.query(
            Contacts.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        while (cursor?.moveToNext() == true) with(cursor)
        {
            val id = this.getInt(this.getColumnIndexOrThrow(Contacts._ID))
            val name = this.getString(
                this.getColumnIndexOrThrow(Contacts.DISPLAY_NAME)
            )
            val hasPhoneNum = this.getInt(
                this.getColumnIndexOrThrow(Contacts.HAS_PHONE_NUMBER)
            ) > HAS_NO_PHONE_NUMBER
            var phoneNumber: String? = null
            val phoneNumCursor = contentResolver?.query(
                Phone.CONTENT_URI,
                null,
                "${Phone.CONTACT_ID}=$id",
                null,
                null
            )
            while (phoneNumCursor?.moveToNext() == true) with(phoneNumCursor)
            {
                phoneNumber = this.getString(this.getColumnIndexOrThrow(Phone.NUMBER))
            }
            phoneNumCursor?.close()
            val contact = Contact(
                id,
                name,
                phoneNumber,
                hasPhoneNum
            )

            Log.d("MainActivity", contact.toString())
        }
        cursor?.close()
    }

    companion object {
        const val READ_CONTACTS_RC = 100
        const val HAS_NO_PHONE_NUMBER = 0
    }
}