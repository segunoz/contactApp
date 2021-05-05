package com.decagon.android.sq007

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import java.lang.Exception

class ContactsViewmodel : ViewModel() {

    private val contactsDB = FirebaseDatabase.getInstance().getReference(CONTACTS_NODE)
    private val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private val _result = MutableLiveData<Exception?>()
    val result: LiveData<Exception?> get() = _result

    private val _contact = MutableLiveData<ContactData>()
    val contact: LiveData<ContactData> get() = _contact

    fun addContacts(contacts: ContactData) {
        contacts.id = contactsDB.push().key

        contactsDB.child(user!!.uid).child(contacts.id!!).setValue(contacts).addOnCompleteListener {
            if (it.isSuccessful) {
                _result.value = null
            } else {
                _result.value = it.exception
            }
        }
    }

    private val childEventListener = object : ChildEventListener {
        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            TODO("Not yet implemented")
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            val contact = snapshot.getValue(ContactData::class.java)
            contact?.id = snapshot.key
            _contact.value = contact!!
        }

        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val contact = snapshot.getValue(ContactData::class.java)
            contact?.id = snapshot.key
            _contact.value = contact!!
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            val contact = snapshot.getValue(ContactData::class.java)
            contact?.id = snapshot.key
            contact?.isDeleted = true
            _contact.value = contact!!
        }
    }

    fun getRealtimeUpdate() {
        contactsDB.child(user!!.uid).addChildEventListener(childEventListener)
    }

    fun updateContacts(contact: ContactData) {
        contactsDB.child(user!!.uid).child(contact.id!!).setValue(contact)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    _result.value = null
                } else {
                    _result.value = it.exception
                }
            }
    }

    fun deleteContact(contact: ContactData) {
        contactsDB.child(user!!.uid).child(contact.id!!).setValue(null)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    _result.value = null
                } else {
                    _result.value = it.exception
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        contactsDB.removeEventListener(childEventListener)
    }
}
