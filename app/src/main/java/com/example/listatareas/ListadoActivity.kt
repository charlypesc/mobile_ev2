package com.example.listatareas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
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
            ListaTareasUI()
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
            modifier = Modifier.fillMaxSize()
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
                }
            )
        }else{
            Icon(
                Icons.Filled.Add,
                contentDescription = "Tarea pendiente",
                modifier = Modifier.clickable {
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
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TareaItemUIPreview(){
    val tarea = Tarea(1, "azucar", false)
    TareaItemUI(tarea)
}

@Preview(showBackground = true)
@Composable
fun TareaItemUIPreview2(){
    val tarea = Tarea(2, "platanos", true)
    TareaItemUI(tarea)
}

