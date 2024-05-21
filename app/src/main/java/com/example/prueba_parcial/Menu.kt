package com.example.prueba_parcial

import Modelo.Pet
import Modelo.PetAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
import api.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Menu : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var petAdapter: PetAdapter
    private lateinit var searchView : SearchView
    private lateinit var apiService: ApiService
    private lateinit var btnregis: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu)
     btnregis= findViewById(R.id.BtnAgregar)
        listView = findViewById(R.id.MascotasListView)
        searchView = findViewById(R.id.searchView)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.42.130:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        fetchPets()


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchPets(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    searchPets(it)
                }
                return true
            }
        })
    }

    private fun fetchPets() {
        apiService.getPets().enqueue(object : Callback<List<Pet>> {
            override fun onResponse(call: Call<List<Pet>>, response: Response<List<Pet>>) {
                if (response.isSuccessful) {
                    val pets = response.body() ?: emptyList()
                    petAdapter = PetAdapter(this@Menu, pets)
                    listView.adapter = petAdapter
                } else {
                    Toast.makeText(this@Menu, "Failed to get pets", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Pet>>, t: Throwable) {
                Toast.makeText(this@Menu, "An error occurred", Toast.LENGTH_SHORT).show()
                Toast.makeText(applicationContext,t.toString(),Toast.LENGTH_SHORT).show();
                Toast.makeText(this@Menu, t.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }



    private fun searchPets(query: String) {
        apiService.searchPets(query).enqueue(object : Callback<List<Pet>> {
            override fun onResponse(call: Call<List<Pet>>, response: Response<List<Pet>>) {
                if (response.isSuccessful) {
                    val pets = response.body() ?: emptyList()
                    petAdapter = PetAdapter(this@Menu, pets)
                    listView.adapter = petAdapter
                } else {
                    Toast.makeText(this@Menu, "Failed to search pets", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Pet>>, t: Throwable) {
                Toast.makeText(this@Menu, "An error occurred", Toast.LENGTH_SHORT).show()
            }
        })
    }



    override fun onResume() {
        super.onResume()

        btnregis.setOnClickListener(){
            menuprincipal()
        }
    }


    private fun menuprincipal(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}