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
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class LectorNFCActivity extends AppCompatActivity {
    private static final String TAG = "LectorNFCActivity"; // Para logcat
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFilters;
    private FirebaseFirestore db;
    String id = "a19100579@ceti.mx";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lector_nfcactivity);
        db = FirebaseFirestore.getInstance();


        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC no está disponible en este dispositivo", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Configuración de PendingIntent para NFC
        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_IMMUTABLE);

        // Filtro para datos NDEF tipo text/plain
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Error al configurar el filtro MIME", e);
        }
        intentFilters = new IntentFilter[]{ndef};
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
            Log.d(TAG, "NFC foreground dispatch habilitado");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
            Log.d(TAG, "NFC foreground dispatch deshabilitado");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.d("NFC", "Intent recibido: " + intent.getAction());
        getstudentID(id);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()) ||
                NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            if (tag != null) {
                String text = readTextFromTag(tag);

                if (text != null) {
                    Toast.makeText(this, "Texto recuperado: " + text, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Etiqueta NFC no contiene texto", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Etiqueta NFC no detectada o no compatible", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String readTextFromTag(Tag tag) {
        Ndef ndef = Ndef.get(tag);
        if (ndef == null) {
            Log.d(TAG, "No se pudo obtener NDEF del tag");
            return null;
        }

        NdefMessage ndefMessage = ndef.getCachedNdefMessage();
        if (ndefMessage == null) {
            Log.d(TAG, "No se encontró un mensaje NDEF en el tag");
            return null;
        }

        NdefRecord[] records = ndefMessage.getRecords();
        for (NdefRecord record : records) {
            if (record.getTnf() == NdefRecord.TNF_WELL_KNOWN &&
                    Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)) { // Cambiado para asegurar tipo de texto
                try {
                    byte[] payload = record.getPayload();

                    // Determina el tipo de codificación
                    String encoding = (payload[0] & 128) == 0 ? "UTF-8" : "UTF-16";
                    int languageCodeLength = payload[0] & 0x3F; // Determina longitud del código de idioma

                    // Extrae el texto del payload después del código de idioma
                    String text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, encoding);
                    Log.d(TAG, "Texto leido: " + text);
                    return text;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Error de codificación de texto NDEF", e);
                }
            } else {
                Log.d(TAG, "Registro NDEF no es de tipo texto o tiene otro formato.");
            }
        }
        return null;
    }

    public void getstudentID(String id){//Funcion para obtener el id de los documentos de los estudiantes
        db.collection("Alumnos").document(id)
                .update("asistencia", FieldValue.increment(1)) // Incrementa el campo "asistencia" en 1
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Asistencia capturada", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(this, "No se encontró el documento", Toast.LENGTH_SHORT).show();
                });
    }

}




