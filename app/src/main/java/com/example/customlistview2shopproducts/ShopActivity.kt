package com.example.customlistview2shopproducts

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.IOException

class ShopActivity : AppCompatActivity(), Removable, Updatable {

    val product: Product? = null
    private lateinit var photoPickerLauncher: ActivityResultLauncher<Intent>
    var photoUri: Uri? = null
    var products: MutableList<Product> = mutableListOf()
    var listAdapter: ListAdapter? = null
    var item: Int? = null

    //private val GALLERY_REQUEST = 302
    var check = true

    private lateinit var toolbarMain: Toolbar
    private lateinit var editImageIV: ImageView
    private lateinit var productNameET: EditText
    private lateinit var productPriceET: EditText
    private lateinit var descriptionET: EditText
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
        descriptionET = findViewById(R.id.descriptionET)
        addBTN = findViewById(R.id.addBTN)
        listViewLV = findViewById(R.id.listViewLV)

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

        addBTN.setOnClickListener {
            if (productNameET.text.isEmpty() ||
                productPriceET.text.isEmpty() ||
                descriptionET.text.isEmpty() ||
                photoUri == null
            ) return@setOnClickListener
            val productName = productNameET.text.toString()
            val productPrice = productPriceET.text.toString()
            val productDescription = descriptionET.text.toString()
            val productImage = photoUri.toString()
            val product = Product(productName, productPrice, productImage, productDescription)
            products.add(product)
            listAdapter = ListAdapter(this@ShopActivity, products)
            listViewLV.adapter = listAdapter
            listAdapter?.notifyDataSetChanged()

            productNameET.text.clear()
            productPriceET.text.clear()
            descriptionET.text.clear()
            editImageIV.setImageResource(R.drawable.ic_product)
            photoUri = null

            listAdapter?.notifyDataSetChanged()
        }

        listViewLV.onItemClickListener =
            AdapterView.OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
                val product = listAdapter!!.getItem(position)
                item = position
                val dialog = MyAlertDialog()
                val args = Bundle()
                args.putSerializable("product", product)
                dialog.arguments = args
                dialog.show(supportFragmentManager, "custom")
            }

    }

    override fun onResume() {
        super.onResume()
        check = intent.extras?.getBoolean("newCheck") ?: true
        if (!check) {
            products = intent.getSerializableExtra("list") as MutableList<Product>
            listAdapter = ListAdapter(this, products)
            check = true
            listViewLV.adapter = listAdapter
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

    override fun remove(product: Product) {
        listAdapter?.remove(product)
    }

    override fun update(product: Product) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("product", product)
        intent.putExtra("products", this.products as ArrayList<Product>)
        intent.putExtra("position", item)
        intent.putExtra("check", check)
        startActivity(intent)
    }
}