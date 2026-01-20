package ro.pub.cs.systems.eim.practicaltest02v5.network

import android.util.Log
import ro.pub.cs.systems.eim.practicaltest02v5.general.Constants
import ro.pub.cs.systems.eim.practicaltest02v5.network.CommunicationThread
import java.io.IOException
import java.net.ServerSocket

val hashMap = HashMap<String, String>()
val hashMap2 = HashMap<String, String>()
//cum declar global a1 aici


class ServerThread(port: Int) : Thread() {
    var serverSocket: ServerSocket? = null
        private set



    init {
        try {
            serverSocket = ServerSocket(port)
        } catch (e: Exception) {
            Log.e(Constants.TAG, "An exception has occurred: " + e.message)
        }
    }


    override fun run() {
        try {
            while (!currentThread().isInterrupted()) {
                Log.i(Constants.TAG, "[SERVER THREAD] Waiting for a client invocation...")
                val socket = serverSocket!!.accept()
                Log.i(
                    Constants.TAG,
                    "[SERVER THREAD] A connection request was received from " + socket.getInetAddress() + ":" + socket.getLocalPort()
                )
                val communicationThread = CommunicationThread(this, socket)
                communicationThread.start()
            }
        } catch (ioException: IOException) {
            Log.e(
                Constants.TAG,
                "[SERVER THREAD] An exception has occurred: " + ioException.message
            )
        }
    }

    fun stopThread() {
        interrupt()
        if (serverSocket != null) {
            try {
                serverSocket!!.close()
            } catch (ioException: IOException) {
                Log.e(
                    Constants.TAG,
                    "[SERVER THREAD] An exception has occurred: " + ioException.message
                )
            }
        }
    }
}