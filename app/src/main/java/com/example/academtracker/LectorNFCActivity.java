package com.example.academtracker;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.UnsupportedEncodingException;

public class LectorNFCActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lector_nfcactivity);

        textView = findViewById(R.id.textView); // Añade un TextView en tu layout para mostrar el contenido de la etiqueta

        // Configura el adaptador NFC
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC no está disponible en este dispositivo.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Crear un PendingIntent para capturar la detección de etiquetas
        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);

        // Configura el intent-filter para detectar etiquetas NFC con NDEF
        IntentFilter[] intentFilters = new IntentFilter[] {
                new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
        };

        // Activar la detección de NFC en primer plano
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Desactivar la detección de NFC
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tag != null) {
                readFromNfcTag(intent);
            }
        }
    }

    // Método para leer datos de la etiqueta NFC
    private void readFromNfcTag(Intent intent) {
        Ndef ndef = Ndef.get((Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG));

        if (ndef != null) {
            try {
                ndef.connect();
                NdefMessage ndefMessage = ndef.getNdefMessage();

                if (ndefMessage != null) {
                    NdefRecord[] records = ndefMessage.getRecords();
                    for (NdefRecord record : records) {
                        if (record.getTnf() == NdefRecord.TNF_WELL_KNOWN &&
                                new String(record.getType()).equals("T")) { // Tipo de texto
                            String payload = new String(record.getPayload(), "UTF-8");
                            textView.setText(payload); // Mostrar el contenido en el TextView
                        }
                    }
                } else {
                    Toast.makeText(this, "No se encontró contenido en la etiqueta.", Toast.LENGTH_SHORT).show();
                }
                ndef.close();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al leer la etiqueta NFC", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
