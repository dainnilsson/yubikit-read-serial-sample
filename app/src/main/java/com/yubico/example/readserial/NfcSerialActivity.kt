package com.yubico.example.readserial

import android.app.Activity
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Bundle
import android.widget.Toast
import com.yubico.yubikit.apdu.TlvUtils
import java.nio.ByteBuffer

class NfcSerialActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)?.let { tag ->
            val isodep = IsoDep.get(tag)
            isodep.connect()
            // Select the management application
            isodep.transceive(byteArrayOf(0x00, 0xa4.toByte(), 0x04, 0x00, 0x08, 0xa0.toByte(), 0x00, 0x00, 0x05, 0x27, 0x47, 0x11, 0x17))
            // Read device configuration
            val response = isodep.transceive(byteArrayOf(0x00, 0x1d, 0x00, 0x00, 0x00))
            // Parse the serial number from the response
            val serial = ByteBuffer.wrap(
                TlvUtils.parseTlvMap(response.sliceArray(1 until response.size)).get(0x02)
            ).int
            Toast.makeText(
                this@NfcSerialActivity,
                "Serial: $serial",
                Toast.LENGTH_SHORT
            ).show()
        }
        
        finish()
    }
}