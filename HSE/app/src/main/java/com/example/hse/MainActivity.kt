package com.example.hse

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import io.github.rybalkinsd.kohttp.ext.httpGet
import io.github.rybalkinsd.kohttp.ext.asString
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {


    var res:String? = null
    var result: TextView ?= null
    var message: String?= "222"
    var editText:EditText ?=null //findViewById<EditText>(R.id.number)
    var selectedId:Int = 0
    
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var currency:Array<Array<String>> = Array(30, { Array(2, {"0"}) })
        val myService: ExecutorService = Executors.newFixedThreadPool(2)
        var call = myService.submit(Callable<String> {
            val client = "https://api.exchangeratesapi.io/latest".httpGet()
            return@Callable client.asString()
        })
        var y:String? = call.get()
        if (y != null) {
            y = y.removeSurrounding(
                "{",
                "}"
            )
            y = y.substringBefore('}')
            y = y.substringAfter('{')
        }
        var x = y?.split(",")
        var i = 0;
        for (group in currency) {
             if (x!=null) {
                 var s = x[i].substringAfter('"')
                 s = s.substringBefore('"')
                 currency[i][0] = "From EUR into $s"
                 s = x[i].substringAfter(':')
                 currency[i][1] = s
             }
            i++
        }
        var bufArray: Array<String> = Array(currency.size, {" "})
        i = 0;
        for ( group in currency) {
            bufArray[i] = group[0]
            i++
        }
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            bufArray
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        var showButton = findViewById<Button>(R.id.button)
        result = findViewById(R.id.Res)
        editText = findViewById(R.id.number)
        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent:AdapterView<*>, view: View, position: Int, id: Long){
                selectedId = spinner.selectedItemId.toInt()
            }
            override fun onNothingSelected(parent: AdapterView<*>){
            }
        }
        showButton.setOnClickListener{
            var text:String?= editText?.text.toString()
            var x:Double?= text?.toDouble()
            if (x != null) {
                x *= (currency[selectedId][1]).toDouble()
            }
            result?.text = x.toString()
        }
    }
}