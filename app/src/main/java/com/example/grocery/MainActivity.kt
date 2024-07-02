package com.example.grocery

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(), GroceryRVAdapter.GroceryItemClickInterface {

    lateinit var itemsRv:RecyclerView
    lateinit var addFAB:FloatingActionButton
    lateinit var list:List<GroceryItems>
    lateinit var groceryRVAdapter: GroceryRVAdapter
    lateinit var groceryViewModel:GroceryViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        itemsRv=findViewById(R.id.idRVItems)
        addFAB=findViewById(R.id.idFABAdd)
        list =ArrayList<GroceryItems>()
        groceryRVAdapter = GroceryRVAdapter(list,this)
        itemsRv.layoutManager=LinearLayoutManager(this)
        itemsRv.adapter=groceryRVAdapter
        val groceryRepository = GroceryRepository(GroceryDatabase(this))
        val factory =GroceryViewModelFactory(groceryRepository)
        groceryViewModel=ViewModelProvider(this,factory)
            .get(GroceryViewModel::class.java)
        groceryViewModel.getAllGroceryItems().observe(this,Observer{
            groceryRVAdapter.list=it
            groceryRVAdapter.notifyDataSetChanged()
        })

        addFAB.setOnClickListener{
            openDialog()
        }
    }
    fun openDialog()
    {
        val dialog=Dialog(this)
        dialog.setContentView(R.layout.grocery_add_dialog)
        val cancelBtn=dialog.findViewById<Button>(R.id.idBtnCancel)
        val addBtn = dialog.findViewById<Button>(R.id.idBtnAdd)
        val itemEdt=dialog.findViewById<EditText>(R.id.idEditItemName)
        val itemPriceEdt=dialog.findViewById<EditText>(R.id.idEditItemprice)
        val itemQuantityEdt=dialog.findViewById<EditText>(R.id.idEditItemQuantity)
        cancelBtn.setOnClickListener{
            dialog.dismiss()
        }
        addBtn.setOnClickListener{
            val itemName : String=itemEdt.text.toString()
            val itemPrice : String=itemPriceEdt.text.toString()
            val itemQuantity : String=itemQuantityEdt.text.toString()
            if (itemName.isNotEmpty() && itemQuantity.isNotEmpty() && itemPrice.isNotEmpty()) {
                val qty = itemQuantity.toIntOrNull()
                val pr = itemPrice.toIntOrNull()

                if (qty != null && pr != null) {
                    val items = GroceryItems(itemName = itemName, itemQuantity = qty, itemPrice = pr)
                    groceryViewModel.insert(items)
                    Toast.makeText(applicationContext, "Item inserted", Toast.LENGTH_SHORT).show()
                    groceryRVAdapter.notifyDataSetChanged()
                    dialog.dismiss()
                } else {
                    Toast.makeText(applicationContext, "Please enter valid numbers for price and quantity", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(applicationContext, "Please enter all the data", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    override fun OnItemClick(groceryItems: GroceryItems) {
        groceryViewModel.delete(groceryItems)
        groceryRVAdapter.notifyDataSetChanged()
        Toast.makeText(applicationContext, "Item Deleted.", Toast.LENGTH_SHORT).show()
    }
}