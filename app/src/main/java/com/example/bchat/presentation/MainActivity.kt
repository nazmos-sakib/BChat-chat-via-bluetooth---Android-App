package com.example.bchat.presentation

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bchat.presentation.components.ChatScreen
import com.example.bchat.presentation.components.DeviceScreen
import com.example.bchat.ui.theme.BChatTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var discoverableLauncher: ActivityResultLauncher<Intent>


    private val bluetoothManager by lazy {
        applicationContext.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }
    private val isBluetoothEnabled:Boolean
        get() = bluetoothAdapter?.isEnabled == true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //enable bluetooth discovery
        discoverableLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Bluetooth discoverability denied", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Device is now discoverable for ${result.resultCode} seconds", Toast.LENGTH_SHORT).show()
            }
        }

        //check if the device bluetooth is enabled
        val enabledBluetoothLauncher = registerForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ){ /*not needed */}

        //checkBluetooth permission
        val permissionLauncher = registerForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { perms->
            //1st- check bluetooth can be enable
            val canEnableBluetooth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                perms[Manifest.permission.BLUETOOTH_CONNECT] == true
            } else {
                true
            }

            // 2nd - if the bluetooth is available but not enabled
            if (canEnableBluetooth && !isBluetoothEnabled){
                enabledBluetoothLauncher.launch(
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                )
            }
        }


        //check permission and if not permitted then ask for permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN
                )
            )
        }

        enableDiscoverable()


        setContent {
            BChatTheme {
                val viewModel:BluetoothViewModel = hiltViewModel()
                val state by viewModel.state.collectAsState()

                LaunchedEffect(key1 = state.errMessage) {
                    state.errMessage?.let {message->
                        Toast.makeText(applicationContext,message,Toast.LENGTH_SHORT).show()
                    }
                }

                LaunchedEffect(key1 = state.isConnected) {
                    if (state.isConnected  ) {
                        Toast.makeText(applicationContext,"You are connected",Toast.LENGTH_SHORT).show()

                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                     Surface(
                         modifier = Modifier.padding(innerPadding),
                         color = MaterialTheme.colorScheme.background
                     ) {

                         when{
                             state.isConnecting ->{
                                 Column(
                                     modifier = Modifier.fillMaxSize(),
                                     verticalArrangement  = Arrangement.Center,
                                 horizontalAlignment  = Alignment.CenterHorizontally,
                                 ){
                                     CircularProgressIndicator( )
                                     Text(text = "Connecting..")
                                 }
                             }
                             state.isConnected ->{
                                 ChatScreen(
                                     state = state,
                                     onDisconnect =  viewModel::disconnectFromDevice,
                                     onSendMessage = viewModel::sendMessage)
                             }
                             else -> {
                                 DeviceScreen(
                                     state = state,
                                     onStartScan =  viewModel::startScan,
                                     onStopScan = viewModel::stopScan,
                                     onStartServer = viewModel::waitForIncomingConnection,
                                     onDeviceClick = viewModel::connectToDevice
                                 )
                             }
                         }

                     }
                }
            }
        }
    }

    private fun enableDiscoverable() {
        Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300) // 300 seconds (5 minutes)
        }.also {discoverableIntent->
            discoverableLauncher.launch(discoverableIntent)
        }

    }

}
