package com.example.myRuns

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream


class UserProfile : AppCompatActivity() {
    private lateinit var imgUri: Uri
    private lateinit var tempUri: Uri
    private lateinit var cameraResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        val imgFile = File(getExternalFilesDir(null), "img.jpg")
        val imgTemp = File(getExternalFilesDir(null), "temp.jpg")
        imgUri = FileProvider.getUriForFile(this, "com.example.myRuns",imgFile)
        tempUri = FileProvider.getUriForFile(this, "com.example.myRuns",imgTemp)

        cameraResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult(), {
                if(imgTemp.exists()) {
                    val bitmap = getBitmap(this, tempUri)
                    val img: ImageView = findViewById(R.id.imageView)
                    img.setImageBitmap(bitmap)
                }
            })

        val change = findViewById(R.id.changeButton) as Button
        change.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.fragment_my_dialog, null)
            val camera = dialogLayout.findViewById<TextView>(R.id.camera)
            val gallery = dialogLayout.findViewById<TextView>(R.id.gallery)

            val alert = with(builder){
                setTitle("Pick Profile Picture")
                camera.setOnClickListener {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri)
                    cameraResult.launch(intent)

                }
                gallery.setOnClickListener{
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    startActivityForResult(intent, 200)

                }
                setView(dialogLayout)
                show()

            }


        }


        val save = findViewById(R.id.buttonSave) as Button
        save.setOnClickListener{
            val temp = File(getExternalFilesDir(null), "temp.jpg")
            if(temp.exists())
                temp.renameTo(File(getExternalFilesDir(null), "img.jpg"))

            val edit1: EditText = findViewById(R.id.Edit1)
            val name = edit1.text.toString()
            val edit2: EditText = findViewById(R.id.Edit2)
            val email = edit2.text.toString()
            val edit3: EditText = findViewById(R.id.Edit3)
            val phone = edit3.text.toString()
            val edit4: EditText = findViewById(R.id.Edit4)
            val year = edit4.text.toString()
            val edit5: EditText = findViewById(R.id.Edit5)
            val major = edit5.text.toString()
            val male: RadioButton = findViewById(R.id.male)
            val female: RadioButton = findViewById(R.id.female)


            val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.apply{
                putString("STRING_KEY1", name)
                putString("STRING_KEY2", email)
                putString("STRING_KEY3", phone)
                putString("STRING_KEY4", year)
                putString("STRING_KEY5", major)
                putBoolean("BOOL1",male.isChecked)
                putBoolean("BOOL2",female.isChecked)
            }.apply()

            Toast.makeText(this,"Saved",Toast.LENGTH_LONG).show()
            finish()
        }

        val cancel = findViewById(R.id.buttonCancel) as Button
        cancel.setOnClickListener{
            val temp = File(getExternalFilesDir(null), "temp.jpg")
            if (temp.exists()){
                print("debug: how")
                temp.delete()
            }
            finish()
        }

        if(imgTemp.exists()){
            val bitmap = getBitmap(this, tempUri)
            val img: ImageView = findViewById(R.id.imageView)
            img.setImageBitmap(bitmap)
        }else if(imgFile.exists()){
            val bitmap = getBitmap(this, imgUri)
            val img: ImageView = findViewById(R.id.imageView)
            img.setImageBitmap(bitmap)
        }

        loadData()
    }

    fun getBitmap(context: Context, imgUri: Uri): Bitmap {
        val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imgUri))
        val matrix = Matrix()
        val ret = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        return ret
    }

    fun exit() {
        val temp = File(getExternalFilesDir(null), "temp.jpg")
        if (temp.exists()){
            print("debug: how")
            temp.delete()
    }
        finish()
    }

    private fun loadData() {
        val edit1: EditText = findViewById(R.id.Edit1)
        val edit2: EditText = findViewById(R.id.Edit2)
        val edit3: EditText = findViewById(R.id.Edit3)
        val edit4: EditText = findViewById(R.id.Edit4)
        val edit5: EditText = findViewById(R.id.Edit5)
        val male: RadioButton = findViewById(R.id.male)
        val female: RadioButton = findViewById(R.id.female)

        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val name = sharedPreferences.getString("STRING_KEY1", null)
        val email = sharedPreferences.getString("STRING_KEY2", null)
        val phone = sharedPreferences.getString("STRING_KEY3", null)
        val year = sharedPreferences.getString("STRING_KEY4", null)
        val major = sharedPreferences.getString("STRING_KEY5", null)
        male.setChecked(sharedPreferences.getBoolean("BOOL1", false))
        female.setChecked(sharedPreferences.getBoolean("BOOL2", false))

        edit1.setText(name)
        edit2.setText(email)
        edit3.setText(phone)
        edit4.setText(year)
        edit5.setText(major)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 200){
            val uri = data?.data!!
            val bitmap = getBitmap(this, uri)
            val img: ImageView = findViewById(R.id.imageView)
            img.setImageBitmap(bitmap)

            val imgTemp = File(getExternalFilesDir(null), "temp.jpg")

            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(imgTemp)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.close()
            } catch (e: Exception) {
            }
        }
    }

}