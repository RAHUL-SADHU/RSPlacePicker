package com.app.rspicker.utils

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import com.google.android.libraries.places.internal.e
import com.google.android.libraries.places.internal.i
import android.media.MediaScannerConnection
import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
import android.view.View


object Utils {

    fun logInfo(tag:String,message:String){
        Log.i("###","$tag -> $message")
    }

    fun logError(tag:String,message: String){
        Log.e("###", "$tag -> $message")
    }

    fun showDialog(supportFragmentManager: FragmentManager, dialog: DialogFragment) {

        val ft = supportFragmentManager.beginTransaction()
        val prev = supportFragmentManager.findFragmentByTag(dialog.javaClass.name)
        if (prev != null) {
            ft.remove(prev).commitNow()
        }
        // ft.addToBackStack(null)
        dialog.show(ft, dialog.javaClass.name)

    }

    fun bitmapToFile(applicationContext: Context, bitmap: Bitmap): Uri {
        // Get the context wrapper
        val wrapper = ContextWrapper(applicationContext)

        // Initialize a new file instance to save bitmap object
        var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            // Compress the bitmap and save in jpg format
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Return the saved bitmap uri
        return Uri.parse(file.absolutePath)
    }

    fun saveSubmitTicketScreenshot(context: Context, view: View) {
        val now = Date()
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now)

        try {
            // image naming and path  to include sd card  appending name you choose for file
            val mPath = Environment.getExternalStorageDirectory().toString() + "/Pictures/" + now + ".jpg"
            val bitmap = loadBitmapFromView(view)
            // create bitmap screen capture
            /*View v1;
            v1 = view;

            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createScaledBitmap(v1.getDrawingCache(), 400, 800, false);
            v1.setDrawingCacheEnabled(false);*/

            val imageFile = File(mPath)

            val outputStream = FileOutputStream(imageFile)
            val quality = 100
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.flush()
            outputStream.close()

            //To Refresh the Gallery.
            MediaScannerConnection.scanFile(context,
                    arrayOf(imageFile.toString()), null
            ) { path, uri ->
                logError("ExternalStorage", "Scanned $path:")
                logError("ExternalStorage", "-> uri=$uri")
            }

            //openScreenshot(imageFile);
        } catch (e: Throwable) {
            // Several error may come out with file handling or OOM
            logError("Screen shot Error", e.message.toString())
            e.printStackTrace()
        }

    }

    private fun loadBitmapFromView(v: View): Bitmap {
        val b = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
       // c.drawColor(-0x1)
        //v.setBackgroundResource(R.drawable.main_bg);
        //v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
        v.draw(c)
        return b
    }


}