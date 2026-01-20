package ro.pub.cs.systems.eim.practicaltest02v5.network

import android.util.Log
import org.json.JSONObject // <--- ATENTIE: Ai nevoie de JSONObject acum
// import org.json.JSONArray (daca e nevoie, dar JSONObject il include de obicei in Android)
import ro.pub.cs.systems.eim.practicaltest02v5.general.Constants
import ro.pub.cs.systems.eim.practicaltest02v5.general.Utilities
import ro.pub.cs.systems.eim.practicaltest02v5.network.ServerThread
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.Socket
import java.net.URL
import kotlin.concurrent.timer

class CommunicationThread(private val serverThread: ServerThread?, private val socket: Socket?) : Thread() {



    override fun run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!")
            return
        }
        try {
            val bufferedReader = Utilities.getReader(socket)
            val printWriter = Utilities.getWriter(socket)

            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (word)!")
            val request = bufferedReader.readLine()

            if (request.isNullOrEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client!")
                return
            }

            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from client for: $request")
            //sa se creeze un hashtable pt put si get care sa tina minte cheia si valoarea

            val urlString = "https://time.now/developer/api/timezone/UTC"

            var pageSourceCode = ""

            try {
                val url = URL(urlString)
                val urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.connectTimeout = 5000

                if (urlConnection.responseCode == 200) {
                    val reader = BufferedReader(InputStreamReader(urlConnection.inputStream))
                    pageSourceCode = reader.readText()
                    reader.close()
                } else {
                    pageSourceCode = ""
                }
            } catch (e: Exception) {
                Log.e(Constants.TAG, "Eroare la conectare (posibil cuvant inexistent): ${e.message}")
            }

            var timp = ""

            //acum din jsonul returnat trebuie sa iau unix time
//            {
//                "abbreviation": "UTC",
//                "client_ip": "141.85.150.216",
//                "datetime": "2026-01-20T16:48:53.593074+00:00",
//                "day_of_week": 2,
//                "day_of_year": 20,
//                "dst": false,//                "dst_from": null,
//                "dst_offset": 0,
//                "dst_until": null,
//                "raw_offset": 0,
//                "timezone": "UTC",
//                "unixtime": 1768927733,
//                "utc_datetime": "2026-01-20T16:48:53.593104Z",
//                "utc_offset": "+00:00",
//                "week_number": 4
//            }
            if(pageSourceCode.isNotEmpty()) {
                val json = JSONObject(pageSourceCode)
                timp = json.getString("unixtime")
                Log.d("timp", timp)
            }
            //vreau sa am un hashmap in care sa le retin toate cele 3 elemente, cheie, valoare si acest time


            var result = ""
            var result2 = ""
            var a1 = ""
            var a2 = ""
            try {
                val parts = request.split(",")
                if(parts.size == 3) {
                    val operation = parts[0]
                    val x1 = parts[1]
                    val x2 = parts[2]

                    var final = ""
                    //forma put, cheie, valoaare
                    //get cheie -> valoarea
                    when (operation) {
                        "put" -> {
                            hashMap.put(x1, x2)
                            hashMap2.put(x1,timp)
                            Log.d("Hashmap", hashMap.toString())
                            result = hashMap.toString()
                            a1 = hashMap2.get(x1).toString()
                        }
                    }
                    Log.d("Hashmap", hashMap.toString())
                }
                //cum testez timpul de la get - timpul de la put sa nu aiba mai mult de 60 sec diferenta
                else if(parts.size == 2) {
                    Log.d("Hashmap", hashMap.toString())
                    val operation = parts[0]
                    val x1 = parts[1]
                    when (operation) {
                        "get" -> {
                            if(hashMap.containsKey(x1)) {
                                result = hashMap.get(x1).toString()
                                result2 = hashMap2.get(x1).toString()
                                Log.d("Hashmap", hashMap.toString())
                                if(result2.toDouble() - a1.toDouble() > 5) {
                                    Log.d("ERROR", timp)
                                }
                            }
                    }
                }
            } else{
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client!")
                return
            }
            }catch (e: Exception) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: ${e.message}")
            }

            printWriter.println(result2 + "," + result)
//            printWriter.println(result2)
            //printWriter.println(timp)

            //clientul sa printeze exact cum printeaza pentru result
            printWriter.println(timp)

            printWriter.println(a1)
            printWriter.flush()

        } catch (ioException: IOException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Eroare de retea: " + ioException.message)
        } finally {
            try {
                socket.close()
            } catch (e: IOException) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Could not close socket: " + e.message)
            }
        }
    }
}