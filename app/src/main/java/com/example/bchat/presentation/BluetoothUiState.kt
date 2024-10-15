package com.example.bchat.presentation

import com.example.bchat.domain.chat.BluetoothDevice
import com.example.bchat.domain.chat.BluetoothDeviceDomain
import com.example.bchat.domain.chat.BluetoothMessage

data class BluetoothUiState(
    val scannedDevices: List<BluetoothDeviceDomain> = emptyList(),
    val pairedDevices: List<BluetoothDeviceDomain> = emptyList(),
    val isConnected:Boolean = false,
    val isConnecting:Boolean = false,
    val errMessage:String? = null,
    val messages:List<BluetoothMessage> = emptyList()
)
