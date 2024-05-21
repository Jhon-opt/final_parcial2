package Modelo

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.prueba_parcial.PetDetailActivity

class PetAdapter(context: Context, private val pets: List<Pet>) : ArrayAdapter<Pet>(context, 0, pets) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val pet = getItem(position)
        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false)

        val nameTextView = view.findViewById<TextView>(android.R.id.text1)
        val typeTextView = view.findViewById<TextView>(android.R.id.text2)

        nameTextView.text = pet?.name
        typeTextView.text = pet?.type

        view.setOnClickListener {
            val intent = Intent(context, PetDetailActivity::class.java).apply {
                putExtra("name", pet?.name)
                putExtra("type", pet?.type)
                putExtra("age", pet?.age)
                putExtra("breed", pet?.breed)
                putExtra("image", pet?.image)
            }
            context.startActivity(intent)
        }

        return view
    }
}
