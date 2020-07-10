package com.vmcplus.ethernetprinting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.Socket

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                val text = editText.text.toString()
                printReceipt(text)
            }
        }
    }

    private suspend fun printReceipt(text: String) {
        withContext(Dispatchers.IO) {
            try {
                val socket: Socket = Socket()
                socket.connect(InetSocketAddress("192.168.1.21", 9100), 5000)
                socket.getOutputStream().apply {
                    write(text.toByteArray())
                    write(byteArrayOf(10, 10, 10, 10))
                    write(byteArrayOf(0x1D, 0x56, 66, 0x00))
                    flush()
                    close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}