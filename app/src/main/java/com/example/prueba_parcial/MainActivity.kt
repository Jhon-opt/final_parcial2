package com.example.prueba_parcial

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.Manifest

import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import api.ApiService
import api.ApiServiceFactory
import com.example.prueba_parcial.ui.theme.Prueba_parcialTheme
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.IOException
private lateinit var Btnvolvermenu : Button
class MainActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var buttonPhoto: Button
    private lateinit var imageView: ImageView
    private lateinit var editTextName: EditText
    private lateinit var spinnerType: Spinner
    private lateinit var editTextAge: EditText
    private lateinit var spinnerBreed: Spinner
    private var selectedImagePath: String? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permiso otorgado, puedes realizar la acción que requiere permisos
            selectImage()
        } else {
            // Permiso denegado, manejar este caso apropiadamente
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.subir_datos)
        Btnvolvermenu = findViewById(R.id.buttonCancel)
        buttonPhoto = findViewById(R.id.buttonPhoto)
        editTextName = findViewById(R.id.editTextName)
        spinnerType = findViewById(R.id.spinnerType)
        imageView = findViewById(R.id.imageView)
        editTextAge = findViewById(R.id.editTextAge)
        spinnerBreed = findViewById(R.id.spinnerBreed)

        buttonPhoto.setOnClickListener {
            checkPermissionAndSelectImage()
        }

        val saveButton = findViewById<Button>(R.id.buttonSave)
        saveButton.setOnClickListener {
            // Obtener los datos de la mascota
            val type = spinnerType.selectedItem.toString()
            val name = editTextName.text.toString()
            val age = editTextAge.text.toString().toIntOrNull() ?: 0
            val breed = spinnerBreed.selectedItem.toString()

            // Obtener la URI de la imagen
            val imagePath = getImagePath()

            if (imagePath != null) {
                // Subir la mascota con la imagen
                uploadPet(type, name, age, breed, imagePath)
            } else {
                // Manejar el caso en que no se haya seleccionado una imagen
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermissionAndSelectImage() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permiso ya otorgado, seleccionar imagen
            selectImage()
        } else {
            // Solicitar permiso en tiempo de ejecución
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                imageView.setImageBitmap(bitmap)
                selectedImagePath = getRealPathFromURI(filePath!!)
                // Utilizar imagePath para subir la imagen
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun getRealPathFromURI(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        return if (cursor == null) {
            uri.path!!
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            val result = cursor.getString(idx)
            cursor.close()
            result
        }
    }
    private fun getImagePath(): String? {
        return selectedImagePath
    }


    private fun uploadPet(type: String, name: String, age: Int, breed: String, imagePath: String) {
        val imageFile = File(imagePath)

        // Crear el cuerpo de la imagen como un MultipartBody.Part
        val requestFile = imageFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

        // Crear los RequestBody para los otros campos de datos
        val typeRequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), type)
        val nameRequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), name)
        val ageRequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), age.toString())
        val breedRequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), breed)

        // Enviar la petición con Retrofit
        val apiService = ApiServiceFactory.apiService
        val call = apiService.uploadPet(imagePart,typeRequestBody, nameRequestBody, ageRequestBody, breedRequestBody, )
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    // Manejar la respuesta exitosa
                    Toast.makeText(this@MainActivity, "Pet uploaded successfully", Toast.LENGTH_SHORT).show()
                } else {
                    // Manejar la respuesta de error
                    Toast.makeText(this@MainActivity, "Failed to upload pet", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Manejar fallo en la solicitud
                Toast.makeText(this@MainActivity, "An error occurred", Toast.LENGTH_SHORT).show()
                Toast.makeText(applicationContext,t.toString(),Toast.LENGTH_SHORT).show();
                Toast.makeText(this@MainActivity, t.toString(), Toast.LENGTH_SHORT).show()

            }
        })
    }



    override fun onResume() {
        super.onResume()

        Btnvolvermenu.setOnClickListener(){
            agregarp()
        }
    }


    private fun agregarp(){
        val intent = Intent(this, Menu::class.java)
        startActivity(intent)
    }

}
