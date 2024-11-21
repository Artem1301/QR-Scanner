package com.example.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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
    private static final String PREFERENCE_NAME = "app_preferences";
    private static final String LANGUAGE_KEY = "language";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Встановлюємо збережену мову
        loadLocale();

        setContentView(R.layout.activity_main);

        // Ініціалізація елементів UI
        resultTextView = findViewById(R.id.resultTextView);
        Button scanButton = findViewById(R.id.scanButton);
        Button languageButton = findViewById(R.id.languageButton);

        // Налаштування кнопок
        scanButton.setOnClickListener(view -> startQRScanner());
        languageButton.setOnClickListener(view -> switchLanguage());

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

    private void switchLanguage() {
        // Отримуємо поточну мову
        String currentLanguage = Locale.getDefault().getLanguage();

        // Встановлюємо нову мову
        String newLanguage = currentLanguage.equals("uk") ? "en" : "uk";
        setLocale(newLanguage);

        // Зберігаємо мову
        saveLocale(newLanguage);

        // Перезапускаємо активність
        Intent intent = getIntent();
        finish();
        startActivity(intent);

        Toast.makeText(this, R.string.language_changed, Toast.LENGTH_SHORT).show();
        logger.info("Мову змінено на: " + newLanguage);
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    private void saveLocale(String languageCode) {
        SharedPreferences preferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LANGUAGE_KEY, languageCode);
        editor.apply();
    }

    private void loadLocale() {
        SharedPreferences preferences = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        String language = preferences.getString(LANGUAGE_KEY, "en"); // За замовчуванням англійська
        setLocale(language);
    }
}
