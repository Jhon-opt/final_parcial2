package api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiServiceFactory {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.42.130:3000/") // Asegúrate de usar la dirección IP correcta
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(ApiService::class.java)
}
