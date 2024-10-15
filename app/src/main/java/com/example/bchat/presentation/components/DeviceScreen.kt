package com.example.bchat.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bchat.domain.chat.BluetoothDevice
import com.example.bchat.domain.chat.BluetoothDeviceDomain
import com.example.bchat.presentation.BluetoothUiState

@Composable
fun DeviceScreen(
    state:BluetoothUiState,
    onStartScan:()->Unit,
    onStopScan:()->Unit,
    onStartServer:()->Unit,
    onDeviceClick:(BluetoothDeviceDomain)->Unit

) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment  = Alignment.Start,
        ) {
            BluetoothDeviceList(scannedDevice = state.scannedDevices, pairedDevice = state.pairedDevices, onClick = onDeviceClick, modifier = Modifier
                .fillMaxWidth().weight(1f))


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement  = Arrangement.SpaceEvenly,
                verticalAlignment  = Alignment.CenterVertically,
            ) {
                Button(onClick = onStartScan) {
                    Text(text = "Start scan")
                }
                Button(onClick = onStopScan) {
                    Text(text = "Stop scan")
                }
                Button(onClick = onStartServer) {
                    Text(text = "Start server")
                }
            }

        }
}

@Composable
fun BluetoothDeviceList(
    scannedDevice: List<BluetoothDevice>,
    pairedDevice: List<BluetoothDevice>,
    onClick :(BluetoothDeviceDomain)->Unit,
    modifier: Modifier = Modifier
){
    LazyColumn(modifier = modifier) {
        item {
            Text(text = "Paired Devices", fontWeight = FontWeight.Bold, fontSize = 24.sp, modifier = Modifier.padding(16.dp))
        }
        items(pairedDevice){device->
            Text(
                text = device.name.toString(),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(device) }
                    .padding(16.dp)
            )
        }

        item {
            Text(text = "Scanned Devices", fontWeight = FontWeight.Bold, fontSize = 24.sp, modifier = Modifier.padding(16.dp))
        }
        items(scannedDevice){device->
            Text(
                text = device.name.toString(),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(device) }
                    .padding(16.dp)
            )
        }
    }
}