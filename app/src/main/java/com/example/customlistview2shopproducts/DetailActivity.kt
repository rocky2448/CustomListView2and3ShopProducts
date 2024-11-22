package com.example.customlistview2shopproducts

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DetailActivity : AppCompatActivity() {

    private lateinit var photoPickerLauncher: ActivityResultLauncher<Intent>
    var photoUri: Uri? = null
    private lateinit var toolbarMain: Toolbar
    private lateinit var editImageIV: ImageView
    private lateinit var productNameET: EditText
    private lateinit var productPriceET: EditText
    private lateinit var descriptionET: EditText
    private lateinit var saveBTN: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        toolbarMain = findViewById(R.id.toolbarMain)
        editImageIV = findViewById(R.id.editImageIV)
        productNameET = findViewById(R.id.productNameET)
        productPriceET = findViewById(R.id.productPriceET)
        descriptionET = findViewById(R.id.descriptionET)
        saveBTN = findViewById(R.id.saveBTN)

        setSupportActionBar(toolbarMain)
        title = "Магазин продуктов"
        toolbarMain.subtitle = "by Rocky"

        photoPickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    photoUri = result.data?.data  // для загрузки изображения
                    editImageIV.setImageURI(photoUri)
                }
            }

        editImageIV.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            photoPickerLauncher.launch(photoPickerIntent)
        }

        val product: Product = intent.extras?.getSerializable("product") as Product
        val products = intent.getSerializableExtra("products")
        val item = intent.extras?.getInt("position")
        var check = intent.extras?.getBoolean("check")

        val name = product.name
        val price = product.price
        val description = product.description
        val image: Uri? = Uri.parse(product.image)
        productNameET.setText(name)
        productPriceET.setText(price)
        descriptionET.setText(description)
        editImageIV.setImageURI(image)

        saveBTN.setOnClickListener {
            if (productNameET.text.isEmpty() ||
                productPriceET.text.isEmpty() ||
                descriptionET.text.isEmpty() ||
                photoUri == null
            ) return@setOnClickListener
            val product: Product = Product(
                productNameET.text.toString(),
                productPriceET.text.toString(),
                photoUri.toString(),
                descriptionET.text.toString()
            )
            val list: MutableList<Product> = products as MutableList<Product>
            if (item != null) {
                swap(item, product, products)
            }
            check = false
            val intent = Intent(this, ShopActivity::class.java)
            intent.putExtra("list", list as ArrayList<Product>)
            intent.putExtra("newCheck", check)
            startActivity(intent)
        }
    }

    private fun swap(item: Int, product: Product, products: MutableList<Product>) {
        products.add(item + 1, product)
        products.removeAt(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.exitMenuMain -> finishAffinity()
        }
        return super.onOptionsItemSelected(item)
    }
}