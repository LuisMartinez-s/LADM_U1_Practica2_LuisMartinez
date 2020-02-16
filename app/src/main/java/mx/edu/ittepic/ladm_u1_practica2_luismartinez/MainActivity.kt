package mx.edu.ittepic.ladm_u1_practica2_luismartinez

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

            guardar.setOnClickListener {
                if(editFrase.text.isEmpty() || editNombreArchivo.text.isEmpty()){
                    mensaje("Error, los campos de texto no deben estar vacios.")
                    return@setOnClickListener
                }
                if(radioInterna.isChecked){
                    guardarArchivoInterno()
                }//setOnClick
                if(radioExterna.isChecked){
                    guardarArchivoExterno()
                }
            }
            abrir.setOnClickListener {
                if(editNombreArchivo.text.isEmpty()){
                    mensaje("Error, especifique el nombre del archivo")
                    return@setOnClickListener
                }
                if(radioInterna.isChecked){
                    leerArchivoInterno()
                }
                if(radioExterna.isChecked){
                    leerArchivoSD()
                }
            }//setOnClick

    }

    private fun guardarArchivoExterno() {
        var nombre=editNombreArchivo.text.toString()+".txt"
        if(noSD()){
            mensaje("NO HAY MEMORIA EXTERNA")
            return
        }
        try {
            permisos()
            var rutaSD = Environment.getExternalStorageDirectory()
            var datosArchivo = File(rutaSD.absolutePath,nombre) //file (ruta,nombre)
            var flujoSalida = OutputStreamWriter( FileOutputStream(datosArchivo))
            var data = editFrase.text.toString()

            flujoSalida.write(data) //escribir
            flujoSalida.flush() //forzar escritura
            flujoSalida.close()
            mensaje("El archivo se ha creado correctamente en la memoria SD")
            editFrase.setText("")
            editNombreArchivo.setText("")
        }catch( error: IOException){
            mensaje(error.message.toString())
        }
    }
    fun leerArchivoSD(){
        permisos()
        var nombre=editNombreArchivo.text.toString()+".txt"
        if(noSD()){
            mensaje("NO HAY MEMORIA EXTERNA")
            return
        }
        try {
            var rutaSD = Environment.getExternalStorageDirectory()
            var datosArchivo = File(rutaSD.absolutePath,nombre) //file (ruta,nombre)
            var flujoEntrada = BufferedReader(InputStreamReader(FileInputStream(datosArchivo))) //BufferedReader = leer por linea -- esto es acceso a la memoria interna

            var data = flujoEntrada.readLine()

            //mostrar contenido en campos de texto
           editFrase.setText(data)


        }catch (error : IOException){
            mensaje(error.message.toString())
        }

    }


    fun noSD():Boolean{
        var estado = Environment.getExternalStorageState()
        if(estado != Environment.MEDIA_MOUNTED)
        {
            return true
        }
        return false
    }
    fun permisos(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE),0)  //solicita el permiso, REQUESTCODE = 0 PARA DAR POR ENTENDIDO QUE SI SE PUDO REALIZAR LA ACCION
        }else{
            //mensaje("LOS PERMISOS YA FUERON OTORGADOS")
        }
    }
    private fun mensaje(m: String) {
        AlertDialog.Builder(this)
            .setTitle("Atención")
            .setMessage(m)
            .setPositiveButton("Aceptar"){d, i->}
            .show()
    }
//--------------MEMORIA INTERNA----------------------------------------
    private fun guardarArchivoInterno() {
        var nombre=editNombreArchivo.text.toString()
        try{ var flujoSalida = OutputStreamWriter(
            openFileOutput(nombre+".txt",Context.MODE_PRIVATE)
        )
            var data=editFrase.text.toString()
            flujoSalida.write(data) //escribir
            flujoSalida.flush() //esto forza la escritura
            flujoSalida.close() //cerrar
            mensaje("Archivo creado correctamente")
            editNombreArchivo.setText("")
            editFrase.setText("")
        }catch (error:IOException){
            mensaje(error.message.toString())
        }
    }


    private fun leerArchivoInterno() {
        try{
            var nombre=editNombreArchivo.text.toString()
            var flujoEntrada= BufferedReader(InputStreamReader(openFileInput(nombre+".txt")))
            var data=flujoEntrada.readLine()

            editFrase.setText(data)

        }catch (error:IOException){
            mensaje(error.message.toString())
        }
    }
}

//-------------------FIN MEMORIA INTERNA----------------------------------------
