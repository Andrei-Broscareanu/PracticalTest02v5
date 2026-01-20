package ro.pub.cs.systems.eim.practicaltest02v5

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ro.pub.cs.systems.eim.practicaltest02v5.general.Constants
import ro.pub.cs.systems.eim.practicaltest02v5.network.ClientThread
import ro.pub.cs.systems.eim.practicaltest02v5.network.ServerThread
import ro.pub.cs.systems.eim.practicaltest02v5.R

class MainActivity : AppCompatActivity() {
    private lateinit var serverPortEditText: EditText
    private lateinit var clientPortEditText: EditText
    private lateinit var wordEditText: EditText
    private lateinit var wordTextView: TextView
    private lateinit var connectButton: Button
    private lateinit var getWordButton: Button

    private var serverThread: ServerThread? = null

    private val connectButtonClickListener = android.view.View.OnClickListener {
        val serverPort = serverPortEditText.text.toString()
        if (serverPort.isEmpty()) {
            Toast.makeText(
                applicationContext,
                "[MAIN ACTIVITY] Server port should be filled!",
                Toast.LENGTH_SHORT
            ).show()
            return@OnClickListener
        }
        serverThread = ServerThread(serverPort.toInt())
        if (serverThread?.serverSocket == null) {
            Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!")
            return@OnClickListener
        }
        serverThread?.start()
    }

    private val getWordButtonClickListener = android.view.View.OnClickListener {
        val clientAddress = "localhost"
        val clientPort = clientPortEditText.text.toString()
        val word = wordEditText.text.toString()

        if (clientPort.isEmpty() || word.isEmpty()) {
            Toast.makeText(
                applicationContext,
                "[MAIN ACTIVITY] Client port and word should be filled!",
                Toast.LENGTH_SHORT
            ).show()
            return@OnClickListener
        }

        if (serverThread == null || serverThread?.isAlive == false) {
            Log.e(Constants.TAG, "[MAIN ACTIVITY] There is no server to connect to!")
            return@OnClickListener
        }

        wordTextView.text = ""

        val clientThread = ClientThread(
            clientAddress, clientPort.toInt(), word, wordTextView
        )
        clientThread.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onCreate() callback method has been invoked")
        setContentView(R.layout.activity_practical_test02v5_main)

        serverPortEditText = findViewById(R.id.server_port_edit_text)
        connectButton = findViewById(R.id.connect_button)
        connectButton.setOnClickListener(connectButtonClickListener)

        clientPortEditText = findViewById(R.id.client_port_edit_text)
        wordEditText = findViewById(R.id.word_edit_text)
        getWordButton = findViewById(R.id.get_info)
        getWordButton.setOnClickListener(getWordButtonClickListener)
        wordTextView = findViewById(R.id.info_view)
    }

    override fun onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked")
        serverThread?.stopThread()
        super.onDestroy()
    }
}
