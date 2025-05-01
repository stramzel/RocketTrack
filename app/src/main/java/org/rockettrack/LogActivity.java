package org.rockettrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class LogActivity extends AppCompatActivity {

    private TextView logTextView;
    private final StringBuilder logContent = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        logTextView = findViewById(R.id.log_output);
        Button emailButton = findViewById(R.id.email_button);

        loadLogcat();

        emailButton.setOnClickListener(v -> emailLog());
    }

    private void loadLogcat() {
        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                logContent.append(line).append('\n');
            }

            logTextView.setText(logContent.toString());

        } catch (Exception e) {
            logTextView.setText("Failed to load logcat: " + e.getMessage());
        }
    }

    private void emailLog() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Logcat Output");
        intent.putExtra(Intent.EXTRA_TEXT, logContent.toString());

        try {
            startActivity(Intent.createChooser(intent, "Send Email"));
        } catch (android.content.ActivityNotFoundException ex) {
            logTextView.setText("No email clients installed.");
        }
    }
}