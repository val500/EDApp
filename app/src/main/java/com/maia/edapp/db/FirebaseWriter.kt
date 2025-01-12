package com.maia.edapp.db


import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.maia.edapp.models.User
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class FirebaseWriter {
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "Database"
    fun addUser(user: User) {
        val dbUser = hashMapOf(
            "breakfast" to mutableListOf<String>(),
            "lunch" to mutableListOf<String>(),
            "dinner" to mutableListOf<String>(),
            "sn1" to mutableListOf(),
            "sn2" to mutableListOf(),
            "sn3" to mutableListOf()
        )
        db.collection("users")
            .document(user.email)
            .set(dbUser)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

    }
    fun addWater(user: User) {
        val dbUser = db.collection("users").document(user.email)
        dbUser
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val cal = Calendar.getInstance()
                    val format = SimpleDateFormat("YYYYMMdd")
                    val curDate = format.format(cal.time)
                    if (document.data?.get(curDate) != null) {
                        val dateMap = document.data?.get(curDate) as MutableMap<String, MutableList<String>?>
                        var curWater = dateMap["numWater"]?.get(0)?.toInt()
                        curWater = curWater!!.plus(1)
                        dateMap["numWater"] = mutableListOf(curWater.toString())
                        dbUser.update(curDate, dateMap)
                            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
                            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
                    } else {
                        val newDateMap = HashMap<String, MutableList<String>>()
                        val curWater = 1
                        val numWater = mutableListOf(curWater.toString())
                        newDateMap["numWater"] = numWater
                        listOf("breakfast", "lunch", "dinner", "sn1", "sn2", "sn3").forEach{s ->
                            newDateMap[s] = mutableListOf()
                        }
                        dbUser.update(curDate, newDateMap)
                            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
                            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }
    fun getWater(user: User, func: (Int) -> Unit) {
        val dbUser = db.collection("users").document(user.email)
        var dateMap = mutableMapOf<String, MutableList<String>?>()
        dbUser
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val cal = Calendar.getInstance()
                    val format = SimpleDateFormat("YYYYMMdd")
                    val curDate = format.format(cal.time)
                    if (document.data?.get(curDate) != null) {
                        dateMap = document.data?.get(curDate) as MutableMap<String, MutableList<String>?>
                        val numWater = dateMap.get("numWater")!!.get(0)!!.toInt()
                        func(numWater)
                    } else {
                        Log.d(TAG, "No info recorded on this date")
                        func(0)
                    }
                } else {
                    Log.d(TAG, "No such document")

                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }
    fun addMeal(food: String, user: User, meal: String) {
        val dbUser = db.collection("users").document(user.email)
        dbUser
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val cal = Calendar.getInstance()
                    val format = SimpleDateFormat("YYYYMMdd")
                    val curDate = format.format(cal.time)
                    if (document.data?.get(curDate) != null) {
                        val dateMap = document.data?.get(curDate) as MutableMap<String, MutableList<String>?>
                        val mealList = dateMap[meal]
                        mealList?.add(food)
                        dateMap[meal] = mealList
                        dbUser.update(curDate, dateMap)
                            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
                            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
                    } else {
                        val newDateMap = HashMap<String, MutableList<String>>()
                        val mealList = mutableListOf<String>()
                        mealList.add(food)
                        newDateMap[meal] = mealList
                        listOf("breakfast", "lunch", "dinner", "sn1", "sn2", "sn3", "numWater").forEach{s ->
                            if (s == "numWater") {
                                newDateMap[s] = mutableListOf("0")
                            }
                            if (s != meal) {
                                newDateMap[s] = mutableListOf()
                            }
                        }
                        dbUser.update(curDate, newDateMap)
                            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
                            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
                    }

                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    fun getMeals(user: User, date: String, func: (MutableMap<String, MutableList<String>?>) -> Unit) {
        val dbUser = db.collection("users").document(user.email)
        var dateMap = mutableMapOf<String, MutableList<String>?>()
        dbUser
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    if (document.data?.get(date) != null) {
                        dateMap = document.data?.get(date) as MutableMap<String, MutableList<String>?>
                        func(dateMap)
                    } else {
                        Log.d(TAG, "No info recorded on this date")
                    }
                } else {
                    Log.d(TAG, "No such document")

                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }
}