package com.example.customlistviewshopproducts

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.IOException

class ShopActivity : AppCompatActivity() {

    var bitmap: Bitmap? = null
    var products: MutableList<Product> = mutableListOf()
    private val GALLERY_REQUEST = 302
    private lateinit var toolbarMain: Toolbar
    private lateinit var editImageIV: ImageView
    private lateinit var productNameET: EditText
    private lateinit var productPriceET: EditText
    private lateinit var addBTN: Button
    private lateinit var listViewLV: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_shop)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        toolbarMain = findViewById(R.id.toolbarMain)
        editImageIV = findViewById(R.id.editImageIV)
        productNameET = findViewById(R.id.productNameET)
        productPriceET = findViewById(R.id.productPriceET)
        addBTN = findViewById(R.id.addBTN)
        listViewLV = findViewById(R.id.listViewLV)

        setSupportActionBar(toolbarMain)
        title = "Магазин продуктов"
        toolbarMain.subtitle = "by Rocky"


        editImageIV.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, GALLERY_REQUEST)
        }

        addBTN.setOnClickListener {
            if (productNameET.text.isEmpty() || productPriceET.text.isEmpty()) return@setOnClickListener
            val productName = productNameET.text.toString()
            val productPrice = productPriceET.text.toString()
            val productImage = bitmap
            val product = Product(productName, productPrice, productImage)
            products.add(product)
            val listAdapter = ListAdapter(this@ShopActivity, products)
            listViewLV.adapter = listAdapter
            listAdapter.notifyDataSetChanged()
            productNameET.text.clear()
            productPriceET.text.clear()
            editImageIV.setImageResource(R.drawable.ic_product)
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        editImageIV = findViewById(R.id.editImageIV)
        when(resultCode) {
            GALLERY_REQUEST -> {
                if (resultCode === RESULT_OK) {
                    val selectedImage: Uri? = data?.data
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    editImageIV.setImageBitmap(bitmap)
                }
            }
        }
    }
}