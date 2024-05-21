package api

import Modelo.Pet
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @Multipart
    @POST("pets")
    fun uploadPet(
        @Part image: MultipartBody.Part,
        @Part("type") type: RequestBody,
        @Part("name") name: RequestBody,
        @Part("age") age: RequestBody,
        @Part("breed") breed: RequestBody

    ): Call<ResponseBody>


    @GET("pets")
    fun getPets(): Call<List<Pet>>

    @GET("pets/filter")
    fun searchPets(@Query("name") name: String): Call<List<Pet>>
}