package com.example.listatareas

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.listatareas.db.AppDataBase
import com.example.listatareas.db.Tarea
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CrearActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Volver()
            IngresaNuevoProducto()
        }
    }
    @Preview
    @Composable
    fun Volver() {
        val contexto = LocalContext.current
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Button(onClick = {
                val inten = Intent(contexto, MainActivity::class.java)
                contexto.startActivity(inten)
            }){
                Text("Volver")
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun IngresaNuevoProducto() {
        var valorEntrada by remember { mutableStateOf("") }
        val contexto = LocalContext.current
        val alcanceCorrutina = rememberCoroutineScope()
        val productoAgregado = remember { mutableStateOf(false) }

        LaunchedEffect(productoAgregado.value) {
            if (productoAgregado.value) {
                delay(1200)
                productoAgregado.value = false
            }
        }


        Row(

            modifier = Modifier.padding(13.dp)
        ) {
            TextField(
                value = valorEntrada,
                onValueChange = { newValue ->
                    valorEntrada = newValue
                    if (newValue.isNotBlank()) {
                         newValue.toString()
                    }
                },
                label = { Text("Ingresa el producto") }
            )
            Icon(
                Icons.Filled.Add,

                contentDescription = "Tarea pendiente",
                modifier = Modifier
                    .padding(top=8.dp)
                    .size(40.dp)
                    .clickable {
                    alcanceCorrutina.launch(Dispatchers.IO ){
                        val dao = AppDataBase.getInstance( contexto).tareaDao()
                        dao.insertar(Tarea(0,valorEntrada, false ))
                        productoAgregado.value= true
                        valorEntrada=""
                    }
                }, tint = Color.Blue
            )
        }
        if(productoAgregado.value){
            Text(
                text = "Producto agregado!",
                modifier = Modifier
                    .padding(top=100.dp)
                    .padding(horizontal =10.dp),
                color = Color.Red
                )
        }
    }
}

