package com.decagon.android.sq007

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.FirebaseDatabase

class ContactsActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var contactsList: ArrayList<ContactData>
    private lateinit var fob: FloatingActionButton
    lateinit var contactsAdapter: ContactAdapter
    lateinit var viewmodel: ContactsViewmodel
    lateinit var viewmodel2: ContactsViewmodel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.contacts_activity)

        fob = findViewById(R.id.fob)
        recyclerView = findViewById(R.id.contacts_recyclerview)
        contactsList = ArrayList()
        contactsAdapter = ContactAdapter(this, contactsList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = contactsAdapter

        val viewModel = ContactsViewmodel()

        viewModel.result.observe(
            this,
            Observer {
                val message = if (it == null) {
                    getString(R.string.added_contact)
                } else {
                    getString(R.string.error, it.message)
                }

                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        )

        this.viewmodel = ContactsViewmodel()

        viewmodel2 = ContactsViewmodel()

        viewmodel2.contact.observe(
            this,
            Observer {
                contactsAdapter.addContact(it)
            }
        )

        fob.setOnClickListener {
            addContact()
        }

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("message")

        myRef.setValue("Hello, World!")
        viewmodel2.getRealtimeUpdate()
        viewModel.getRealtimeUpdate()
        val itemTouchHelper = ItemTouchHelper(simpleCallBack)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun addContact() {
        val inflater =
            LayoutInflater.from(applicationContext).inflate(R.layout.add_contact, null, false)

        val addDialog = AlertDialog.Builder(this)
        addDialog.setView(inflater)
        addDialog.setPositiveButton("ADD") {

            dialog, _ ->
            val contactName = inflater.findViewById<EditText>(R.id.new_contact_name_edittext)
            val contactPhoneNumber =
                inflater.findViewById<EditText>(R.id.new_contact_phone_number_edittext)

            val name = contactName?.text.toString().trim()
            val phoneNumber = contactPhoneNumber?.text.toString().trim()
            val contact = ContactData()
            contact.contactName = name
            contact.contactPhoneNumber = phoneNumber
            viewmodel = ContactsViewmodel()
            viewmodel.addContacts(contact)
//            contactsList.add(ContactData("$name", "$phoneNumber"))
            contactsAdapter.notifyDataSetChanged()
            Toast.makeText(this, "CONTACT ADDED", Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }

        addDialog.setNegativeButton("CANCEL") { dialog, _ ->
            dialog.dismiss()
            Toast.makeText(this, "CANCELLED", Toast.LENGTH_LONG).show()
        }
        addDialog.create()
        addDialog.show()
    }

    private fun updateContact(contact: ContactData) {
        val inflater =
            LayoutInflater.from(applicationContext).inflate(R.layout.update_contact, null, false)
        val name = inflater.findViewById<EditText>(R.id.update_contact_name_edittext)
        val phoneNumber = inflater.findViewById<EditText>(R.id.update_contact_phone_number_edittext)

        name.setText(contact.contactName)
        phoneNumber.setText(contact.contactPhoneNumber)

        val addDialog = AlertDialog.Builder(this)
        addDialog.setView(inflater)
        addDialog.setPositiveButton("Update") { dialog, which ->
            val newName = name.text.toString().trim()
            val newPhoneNumber = phoneNumber.text.toString().trim()
            // val contact = ContactData()
            contact.contactName = newName
            contact.contactPhoneNumber = newPhoneNumber
            viewmodel.updateContacts(contact)
            Toast.makeText(this, "Contact Updated", Toast.LENGTH_LONG).show()
        }
        addDialog.create()
        addDialog.show()
    }

    private var simpleCallBack =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT)) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val currentContact = contactsAdapter.contactList[position]

                when (direction) {
                    ItemTouchHelper.RIGHT -> {
                        updateContact(currentContact)
                    }

                    ItemTouchHelper.LEFT -> {

                        AlertDialog.Builder(this@ContactsActivity).also {
                            it.setTitle("Are You Sure You Want To Delete This Contact?")
                            it.setPositiveButton("YES") { dialog, which ->
                                viewmodel.deleteContact(currentContact)
                                recyclerView.adapter?.notifyItemRemoved(position)
                                Toast.makeText(
                                    this@ContactsActivity,
                                    "Contact Deleted Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            it.setNegativeButton("NO") { dialog, which ->
                                dialog.cancel()
                            }
                        }.create().show()
                    }
                }
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }
//    private fun sendData(){
//        val name = findViewById<EditText>(R.id.new_contact_name_edittext)
//        val contactPhoneNumber = findViewById<EditText>(R.id.new_contact_phone_number_edittext)
//        val nameString = name.text.toString()
//        val number = contactPhoneNumber.text.toString()
//        val data = ContactData(nameString,number)
//
//
//    }
}
