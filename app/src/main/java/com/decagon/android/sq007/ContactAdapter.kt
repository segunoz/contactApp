package com.decagon.android.sq007

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(val c: Context, val contactList: ArrayList<ContactData>) : RecyclerView.Adapter<ContactAdapter.contactViewHolder>() {

    inner class contactViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView
        val phoneNumber: TextView
        val info: ImageView

        init {
            name = view.findViewById<TextView>(R.id.contact_name)
            phoneNumber = view.findViewById<TextView>(R.id.contact_phone_number)
            info = view.findViewById<ImageView>(R.id.info_icon)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): contactViewHolder {
        val inflater =
            LayoutInflater.from(parent.context).inflate(R.layout.contact_layout, parent, false)
        return contactViewHolder(inflater)
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    override fun onBindViewHolder(holder: contactViewHolder, position: Int) {
        val contactList = contactList[position]
        holder.name.text = contactList.contactName
        holder.phoneNumber.text = contactList.contactPhoneNumber
    }

    fun addContact(contact: ContactData) {
        if (!contactList.contains(contact)) {
            contactList.add(contact)
        } else {
            val index = contactList.indexOf(contact)
            if (contact.isDeleted) {
                contactList.removeAt(index)
            } else {
                contactList[index] = contact
            }
        }
        notifyDataSetChanged()
    }
}
