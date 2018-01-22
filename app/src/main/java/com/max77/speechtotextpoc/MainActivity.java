package com.max77.speechtotextpoc;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements MainView {

    private Button btnSpeak;
    private Button btnNext;
    private TextView tvSourceText;
    private TextView tvRecognizedText;
    private ProgressBar pbProgress;
    private MainPresenter mPresenter = new MainPresenterImpl(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSpeak = findViewById(R.id.speak);
        btnNext = findViewById(R.id.next);
        tvSourceText = findViewById(R.id.source_text);
        tvRecognizedText = findViewById(R.id.recognized_text);
        pbProgress = findViewById(R.id.progress);

        btnSpeak.setOnClickListener(v -> mPresenter.speak());
        btnNext.setOnClickListener(v -> mPresenter.next());

        PermissionHandlerFragment
                .attach(getSupportFragmentManager())
                .requestPermissions((permissions, grantResult, numGranted) -> {
                            if (numGranted >= 1)
                                mPresenter.setup();
                        },
                        false,
                        Manifest.permission.RECORD_AUDIO);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showSourceText(String text) {
        tvSourceText.setText(text);
    }

    @Override
    public void showRecognizedText(String text) {
        tvRecognizedText.setText(text);
    }

    @Override
    public void showProgress(boolean show) {
        pbProgress.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showMessage(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void enableSpeakButton(boolean enable) {
        btnSpeak.setEnabled(enable);
    }

    @Override
    public void enableNextButton(boolean enable) {
        btnNext.setEnabled(enable);
    }
}
