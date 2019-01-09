package com.max77.speechtotextpoc;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * SpeechToTextPOC project
 * Created by max77 on 20180117.
 */

class MainPresenterImpl implements MainPresenter {
    private boolean isBusy;
    private MainView mView;
    private SpeechRecognizer mSpeechRecognizer;
    private PhraseApi mPhraseApi;
    private PhraseInfo mCurrentPhrase;
    private AudioManager mAudioManager;

    public MainPresenterImpl(MainView view) {
        mView = view;
    }

    @Override
    public void setup() {
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(mView.getContext());
        mAudioManager = (AudioManager) mView.getContext().getSystemService(Context.AUDIO_SERVICE);

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        if (BuildConfig.DEBUG) {
            Interceptor loggingInterceptor =
                    new HttpLoggingInterceptor(message -> Log.d("STT", message))
                            .setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.addInterceptor(loggingInterceptor);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.randomtext.me")
                .addConverterFactory(GsonConverterFactory.create())
                .client(clientBuilder.build())
                .build();

        mPhraseApi = retrofit.create(PhraseApi.class);
        loadNextPhrase();
    }

    @Override
    public void speak() {
        if (isBusy)
            return;

        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                isBusy = true;
                mView.enableSpeakButton(false);
                mView.enableNextButton(false);
                mView.showMessage(getString(R.string.please_read_the_text));
            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {
            }

            @Override
            public void onError(int error) {
                cleanup();

                mView.showMessage(getString(R.string.recognition_error));
            }

            @Override
            public void onResults(Bundle results) {
                cleanup();

                ArrayList<String> strings = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (!strings.isEmpty()) {
                    mCurrentPhrase.setRecognizedText(strings.get(0));

                    float[] scores = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
                    if (scores.length > 0)
                        mCurrentPhrase.setScore(scores[0]);

                    mView.showRecognizedText(mCurrentPhrase.getRecognizedText());
                } else
                    onError(-1);
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                ArrayList<String> strings = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (!strings.isEmpty())
                    mView.showRecognizedText(strings.get(0));
            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }

            private void cleanup() {
                isBusy = false;
                mView.enableSpeakButton(true);
                mView.enableNextButton(true);
//                mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            }
        });

        mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
        mSpeechRecognizer.startListening(new Intent()
                .putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru-RU")
                .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                .putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                .putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                .putExtra(RecognizerIntent.EXTRA_PROMPT, mCurrentPhrase.getSourceText()));
                //.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true));
    }

    @Override
    public void next() {
        if (isBusy)
            return;

        processRecognitionResultAndLoadNextPhrase();
    }

    private void processRecognitionResultAndLoadNextPhrase() {
        isBusy = true;
        mView.showProgress(true);
        mView.enableNextButton(false);
        mView.enableSpeakButton(false);

        // send string to server
        new Handler().postDelayed(() -> {
            mCurrentPhrase = null;
            loadNextPhrase();
        }, 3000);
    }

    private void loadNextPhrase() {
        isBusy = true;
        mView.showProgress(true);
        mView.enableNextButton(false);
        mView.enableSpeakButton(false);
        mView.showSourceText("");
        mView.showRecognizedText("");

        mPhraseApi.getPhrase().enqueue(new Callback<PhraseModel>() {
            @Override
            public void onResponse(Call<PhraseModel> call, Response<PhraseModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mCurrentPhrase = new PhraseInfo()
                            .setId(System.currentTimeMillis())
                            .setSourceText(response.body().getText());

                    isBusy = false;
                    mView.showProgress(false);
                    mView.showSourceText(mCurrentPhrase.getSourceText());
                    mView.showRecognizedText("");
                    mView.enableNextButton(false);
                    mView.enableSpeakButton(true);
                } else
                    onFailure(null, new Exception(getString(R.string.no_data)));
            }

            @Override
            public void onFailure(Call<PhraseModel> call, Throwable t) {
                isBusy = false;
                mView.showProgress(false);
                mView.enableNextButton(true);
                mView.showMessage(t.getLocalizedMessage());
            }
        });
    }

    private String getString(int resId) {
        try {
            return mView.getContext().getString(resId);
        } catch (Exception e) {
            return null;
        }
    }
}
