package ro.pub.cs.systems.eim.practicaltest02v5.network

import android.widget.TextView
import ro.pub.cs.systems.eim.practicaltest02v5.general.Utilities
import java.io.BufferedReader
import java.io.IOException
import java.io.PrintWriter
import java.net.Socket

class ClientThread(
    private val address: String?,
    private val port: Int,
    private val word: String?,
    private val infoTextView: TextView
) : Thread() {
    private var socket: Socket? = null

    override fun run() {
        try {
            socket = Socket(address, port)
            val bufferedReader: BufferedReader = Utilities.getReader(socket!!)
            val printWriter: PrintWriter = Utilities.getWriter(socket!!)
            printWriter.println(word)
            printWriter.flush()
            //            String wordInformation;
            val finalizedWordInformation = bufferedReader.readLine()
            infoTextView.post(Runnable { infoTextView.setText(finalizedWordInformation) })
        } catch (e: IOException) {
            throw RuntimeException(e)
        } finally {
            if (socket != null) {
                try {
                    socket!!.close()
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }
            }
        }
    }
}