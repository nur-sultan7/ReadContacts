package com.nursultan.readcontacts

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.Contacts
import android.util.Log
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val contactsPermission = ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
        if (contactsPermission)
            requestContacts()
        else
            Log.d("MainActivity", "Read contacts permission is denied!")
    }

    private fun requestContacts() {
        val cursor = contentResolver?.query(
            Contacts.CONTENT_URI,
            null, null, null, null
        )
        while (cursor?.moveToNext() == true) with(cursor)
        {
            val id = this.getInt(this.getColumnIndexOrThrow(Contacts._ID))
            val name = this.getString(
                this.getColumnIndexOrThrow(Contacts.DISPLAY_NAME)
            )
            val phoneNum = this.getString(
                this.getColumnIndexOrThrow(Contacts.HAS_PHONE_NUMBER)
            )
            var phoneNumber: String? = null
            val phoneNumCursor = contentResolver?.query(
                Phone.CONTENT_URI, null,
                Phone._ID + "=" + id, null,
                null
            )
            while (phoneNumCursor?.moveToNext() == true) with(phoneNumCursor)
            {
                phoneNumber = this.getString(this.getColumnIndexOrThrow(Phone.NUMBER))
            }
            phoneNumCursor?.close()
            Log.d("MainActivity", "name: $name phone number: $phoneNumber")
        }
        cursor?.close()
    }
}