package com.example.prueba_parcial

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import com.example.prueba_parcial.R
import com.squareup.picasso.Picasso
private lateinit var btnrvol: Button
class PetDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_detail)
        btnrvol = findViewById(R.id.Btn_Volver_detalle)
        val petImageView: ImageView = findViewById(R.id.imageView2)
        val petNameTextView: TextView = findViewById(R.id.Txt_mostrar_nombre)
        val petTypeTextView: TextView = findViewById(R.id.Txt_mostrar_tipo)
        val petAgeTextView: TextView = findViewById(R.id.Txt_mostrar_edad)
        val petBreedTextView: TextView = findViewById(R.id.Txt_mostrar_raza)


        // Get the intent extras
        val name = intent.getStringExtra("name")
        val type = intent.getStringExtra("type")
        val age = intent.getIntExtra("age", -1)
        val breed = intent.getStringExtra("breed")
        val image = intent.getStringExtra("image")

        // Set the values to the views
        petNameTextView.text = name
        petTypeTextView.text = type
        petAgeTextView.text = age.toString()
        petBreedTextView.text = breed
        Picasso.get().load(image).into(petImageView)
    }


    override fun onResume() {
        super.onResume()

        btnrvol.setOnClickListener(){
            menuprincipal1()
        }
    }


    private fun menuprincipal1(){
        val intent = Intent(this, Menu::class.java)
        startActivity(intent)
    }
}
