package com.example.bchat.data.chat

import android.bluetooth.BluetoothSocket
import com.example.bchat.domain.chat.BluetoothMessage
import com.example.bchat.domain.chat.ConnectionResult
import com.example.bchat.domain.chat.TransferFailedException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.withContext
import java.io.IOException
import java.nio.ByteBuffer

class BluetoothDataTransferService(
    private val socket: BluetoothSocket
) {
    fun listenForIncomingMessages(): Flow<BluetoothMessage> {
        return flow {
            if (!socket.isConnected) {
                return@flow
            }
            val buffer = ByteArray(1024)
            while (true) {
                val byteCount = try {
                    socket.inputStream.read(buffer)
                } catch (e: IOException) {
                    throw TransferFailedException()
                }

                emit(
                    buffer.decodeToString(
                        endIndex = byteCount
                    ).toBluetoothMessage(
                        isFromLocalUser = false
                    )
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun sendMessage(bytes:ByteArray):Boolean{
        return withContext(Dispatchers.IO){
            try {
                socket.outputStream.write(bytes)
            } catch (e:IOException){
                e.stackTrace
                return@withContext false
            }
            true
        }
    }
}