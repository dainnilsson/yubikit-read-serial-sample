package com.yubico.example.readserial

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.yubico.yubikit.YubiKitManager
import com.yubico.yubikit.management.ManagementApplication
import com.yubico.yubikit.transport.usb.UsbConfiguration
import com.yubico.yubikit.transport.usb.UsbSession
import com.yubico.yubikit.transport.usb.UsbSessionListener
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class UsbSerialActivity : Activity() {
    lateinit var yubikit: YubiKitManager
    lateinit var executor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        executor = Executors.newSingleThreadExecutor()
        yubikit = YubiKitManager(this)
        yubikit.startUsbDiscovery(UsbConfiguration(), object : UsbSessionListener {
            override fun onSessionReceived(session: UsbSession, hasPermission: Boolean) {
                Log.d("READ SERIAL", "USB received, permission: $hasPermission")
                if (hasPermission) {
                    executor.run {
                        ManagementApplication(session).use {
                            val serial = it.readConfiguration().serial
                            runOnUiThread { Toast.makeText(this@UsbSerialActivity, "Serial: $serial", Toast.LENGTH_SHORT).show() }
                        }
                    }
                }
            }

            override fun onSessionRemoved(session: UsbSession) {
                Log.d("READ SERIAL", "USB removed")
            }

            override fun onRequestPermissionsResult(session: UsbSession, isGranted: Boolean) {
                Log.d("READ SERIAL", "USB permissions: $isGranted")
            }

        })
        finish()
    }

    override fun onDestroy() {
        yubikit.stopUsbDiscovery()
        executor.shutdownNow()
        super.onDestroy()
    }
}