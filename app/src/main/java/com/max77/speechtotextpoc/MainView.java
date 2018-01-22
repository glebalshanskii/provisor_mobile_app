package com.max77.speechtotextpoc;

import android.content.Context;

/**
 * SpeechToTextPOC project
 * Created by max77 on 20180117.
 */

public interface MainView {
    Context getContext();

    void showSourceText(String text);

    void showRecognizedText(String text);

    void showProgress(boolean show);

    void showMessage(String text);

    void enableSpeakButton(boolean enable);

    void enableNextButton(boolean enable);
}
