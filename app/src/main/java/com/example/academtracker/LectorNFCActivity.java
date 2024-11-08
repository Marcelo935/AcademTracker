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

    // Declaramos el adaptador NFC y el TextView para mostrar el texto de la etiqueta
    private NfcAdapter nfcAdapter;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Activar la funcionalidad de Edge to Edge para gestionar el espacio de las barras del sistema.
        EdgeToEdge.enable(this);

        // Establecer el layout de la actividad
        setContentView(R.layout.activity_lector_nfcactivity);

        // Inicializar el TextView para mostrar el contenido de la etiqueta NFC
        textView = findViewById(R.id.textView);

        // Obtener el adaptador NFC del dispositivo
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // Comprobar si el dispositivo soporta NFC
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC no está disponible en este dispositivo.", Toast.LENGTH_SHORT).show();
            finish(); // Si el dispositivo no tiene NFC, cerramos la actividad
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

        // Crear un PendingIntent para manejar los intents que se generen cuando se detecte una etiqueta NFC
        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);

        // Establecer un filtro de intentos para detectar etiquetas NFC con mensajes NDEF
        IntentFilter[] intentFilters = new IntentFilter[] {
                new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED) // Filtro de acción para NDEF
        };

        // Activar la detección de etiquetas NFC mientras la actividad esté en primer plano
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Desactivar la detección de etiquetas NFC cuando la actividad no esté en primer plano
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // Comprobar si el intent recibido es para la acción de descubrir una etiqueta NDEF
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            // Obtener la etiqueta NFC desde el intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tag != null) {
                // Llamar al método para leer el contenido de la etiqueta NFC
                readFromNfcTag(intent);
            }
        }
    }

    // Método para leer los datos de la etiqueta NFC
    private void readFromNfcTag(Intent intent) {
        System.out.println("Se accede a la funcion");
        // Obtener el objeto Ndef desde la etiqueta NFC
        Ndef ndef = Ndef.get((Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG));

        if (ndef != null) {
            System.out.println("NDEF NO ES NULO");
            try {
                // Conectar con la etiqueta NFC
                ndef.connect();
                // Obtener el mensaje NDEF de la etiqueta
                NdefMessage ndefMessage = ndef.getNdefMessage();

                // Si se encuentra un mensaje NDEF, procesamos los registros
                if (ndefMessage != null) {
                    System.out.println("EL MENSAJE NO ES NULO");
                    NdefRecord[] records = ndefMessage.getRecords(); // Obtener todos los registros de la etiqueta

                    // Recorrer todos los registros de la etiqueta NFC
                    for (NdefRecord record : records) {
                        // Verificar si el tipo de registro es de tipo texto (TNF_WELL_KNOWN y tipo "T")
                        if (record.getTnf() == NdefRecord.TNF_WELL_KNOWN &&
                                new String(record.getType()).equals("T")) { // Tipo de texto
                            // Extraer el contenido del payload de la etiqueta usando UTF-8
                            String payload = new String(record.getPayload(), "UTF-8");
                            System.out.println("EL MENSAJE ES: " + payload);
                            // Mostrar el contenido del payload en el TextView
                            textView.setText(payload);
                            Toast.makeText(this, payload, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    System.out.println("NDEF ES NULO");
                    // Si no se encuentra contenido en la etiqueta, mostrar un mensaje
                    Toast.makeText(this, "No se encontró contenido en la etiqueta.", Toast.LENGTH_SHORT).show();
                }

                // Cerrar la conexión con la etiqueta NFC
                ndef.close();
            } catch (Exception e) {
                System.out.println("NO SE ACCEDE A LA FUNCION");
                // En caso de error, mostrar un mensaje y hacer seguimiento del error
                e.printStackTrace();
                Toast.makeText(this, "Error al leer la etiqueta NFC", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


