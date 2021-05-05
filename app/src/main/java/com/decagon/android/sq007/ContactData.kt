package com.decagon.android.sq007

import com.google.firebase.database.Exclude

data class ContactData(@get:Exclude var id: String? = null, var contactName: String? = null, var contactPhoneNumber: String? = null, @get:Exclude var isDeleted: Boolean = false) {
    override fun equals(other: Any?): Boolean {
        return if (other is ContactData) {
            other.id == id
        } else false
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (contactName?.hashCode() ?: 0)
        result = 31 * result + (contactPhoneNumber?.hashCode() ?: 0)
        result = 31 * result + isDeleted.hashCode()
        return result
    }
}
