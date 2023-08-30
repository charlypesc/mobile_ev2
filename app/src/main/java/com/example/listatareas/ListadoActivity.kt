package com.example.listatareas

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.listatareas.db.AppDataBase
import com.example.listatareas.db.Tarea
import com.example.listatareas.ui.theme.ListaTareasTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListadoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            val tareaDao       = AppDataBase.getInstance(this@ListadoActivity).tareaDao()
            val cantRegistros  = tareaDao.contar()

            if(cantRegistros < 1){
                tareaDao.insertar(Tarea(0, "Leche", false))
                tareaDao.insertar(Tarea(0, "Choclos", false))
            }
        }
        setContent {
            val contexto = LocalContext.current
            Column(
                modifier = Modifier
                .padding(top= 20.dp)
                .padding( horizontal = 20.dp)


            ){
                Row(){
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Volver",
                        modifier = Modifier
                            .size(30.dp)
                            .clickable {
                                val inten = Intent(contexto, MainActivity::class.java)
                                contexto.startActivity(inten)
                            },
                            tint = Color.Blue
                    )
                    Text(text= "Listado de compras",
                        modifier = Modifier.padding(4.dp)
                        )

                }

                Spacer(modifier = Modifier.height(16.dp))

                ListaTareasUI()

            }


        }
    }
    @Composable
    fun ListaTareasUI(){
        val contexto = LocalContext.current
        val (tareas, setTareas)= remember{ mutableStateOf(emptyList<Tarea>()) }
        LaunchedEffect(tareas){
            withContext(Dispatchers.IO){
                val dao = AppDataBase.getInstance(contexto).tareaDao()
                setTareas(dao.getAll())
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ){
            items(tareas){tarea ->
                TareaItemUI(tarea){
                    setTareas(emptyList<Tarea>())
                }
            }
        }
    }

}
@Composable
fun TareaItemUI(tarea:Tarea, onSave:() -> Unit = {}){
    val contexto = LocalContext.current
    val alcanceCorrutina = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 20.dp)
    ){
        if(tarea.realizada){
            Icon(
                Icons.Filled.CheckCircle,
                contentDescription = "Tarea realizada",
                modifier = Modifier.clickable {
                    alcanceCorrutina.launch(Dispatchers.IO ){
                        val dao = AppDataBase.getInstance( contexto).tareaDao()
                        tarea.realizada=false
                        dao.actualizar(tarea)
                        onSave()
                    }
                }, tint = Color.Green
            )
        }else{
            Icon(
                Icons.Filled.Add,
                contentDescription = "Tarea pendiente",
                modifier = Modifier
                    .clickable {
                    alcanceCorrutina.launch(Dispatchers.IO ){
                        val dao = AppDataBase.getInstance( contexto).tareaDao()
                        tarea.realizada=true
                        dao.actualizar(tarea)
                        onSave()
                    }
                }
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = tarea.tarea,
            modifier = Modifier.weight(2f)
        )

        Icon(
            Icons.Filled.Delete,
            contentDescription = "Eliminar tarea",
            modifier = Modifier.clickable {
                alcanceCorrutina.launch(Dispatchers.IO ){
                    val dao = AppDataBase.getInstance( contexto).tareaDao()
                    dao.eliminar(tarea)
                    onSave()
                }
            }, tint = Color.Red
        )

    }
}


