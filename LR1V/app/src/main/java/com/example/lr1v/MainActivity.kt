package com.example.lr1v

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var cbMargarita: CheckBox
    private lateinit var cbPepperoni: CheckBox
    private lateinit var cbFourCheese: CheckBox

    private lateinit var cbSmall: CheckBox
    private lateinit var cbMedium: CheckBox
    private lateinit var cbLarge: CheckBox

    private lateinit var cbMushrooms: CheckBox
    private lateinit var cbOlives: CheckBox
    private lateinit var cbExtraCheese: CheckBox
    private lateinit var cbBacon: CheckBox

    private lateinit var btnOk: Button
    private lateinit var tvResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etName = findViewById(R.id.etName)

        cbMargarita = findViewById(R.id.cbMargarita)
        cbPepperoni = findViewById(R.id.cbPepperoni)
        cbFourCheese = findViewById(R.id.cbFourCheese)

        cbSmall = findViewById(R.id.cbSmall)
        cbMedium = findViewById(R.id.cbMedium)
        cbLarge = findViewById(R.id.cbLarge)

        cbMushrooms = findViewById(R.id.cbMushrooms)
        cbOlives = findViewById(R.id.cbOlives)
        cbExtraCheese = findViewById(R.id.cbExtraCheese)
        cbBacon = findViewById(R.id.cbBacon)

        btnOk = findViewById(R.id.btnOk)
        tvResult = findViewById(R.id.tvResult)

        btnOk.setOnClickListener {
            showOrder()
        }
    }

    private fun showOrder() {
        val name = etName.text.toString().trim()

        val pizzaTypes = mutableListOf<String>()
        if (cbMargarita.isChecked) pizzaTypes.add("Маргарита")
        if (cbPepperoni.isChecked) pizzaTypes.add("Пепероні")
        if (cbFourCheese.isChecked) pizzaTypes.add("Чотири сири")

        val sizes = mutableListOf<String>()
        if (cbSmall.isChecked) sizes.add("мала")
        if (cbMedium.isChecked) sizes.add("середня")
        if (cbLarge.isChecked) sizes.add("велика")

        val extras = mutableListOf<String>()
        if (cbMushrooms.isChecked) extras.add("гриби")
        if (cbOlives.isChecked) extras.add("оливки")
        if (cbExtraCheese.isChecked) extras.add("додатковий сир")
        if (cbBacon.isChecked) extras.add("бекон")

        if (name.isEmpty() || pizzaTypes.isEmpty() || sizes.isEmpty()) {
            Toast.makeText(
                this,
                "Будь ласка, заповніть усі обов'язкові дані!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val extrasText = if (extras.isEmpty()) {
            "без додаткових інгредієнтів"
        } else {
            extras.joinToString(", ")
        }

        val resultText = """
            Клієнт: $name
            
            Тип піци: ${pizzaTypes.joinToString(", ")}
            Розмір: ${sizes.joinToString(", ")}
            Додаткові інгредієнти: $extrasText
        """.trimIndent()

        tvResult.text = resultText
    }
}