package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import java.util.Locale;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    private static final Logger logger = Logger.getLogger(MainActivity.class.getName());
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ініціалізація елементів UI
        resultTextView = findViewById(R.id.resultTextView);

        // Налаштування QR-сканера
        findViewById(R.id.scanButton).setOnClickListener(view -> startQRScanner());

        logger.info("Застосунок запущено мовою: " + Locale.getDefault().getLanguage());
    }

    private void startQRScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt(getString(R.string.scan_prompt));
        integrator.setCameraId(0); // Використовуємо основну камеру
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String scannedData = result.getContents();
                resultTextView.setText(scannedData);
                logger.info("QR-код успішно відскановано: " + scannedData);
            } else {
                Toast.makeText(this, R.string.scan_cancelled, Toast.LENGTH_SHORT).show();
                logger.warning("Користувач скасував сканування.");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
