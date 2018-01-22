package com.max77.speechtotextpoc;

/**
 * SpeechToTextPOC project
 * Created by max77 on 20180117.
 */

public class PhraseInfo {
    private long mId;
    private String mSourceText;
    private String mRecognizedText;
    private float mScore;

    public long getId() {
        return mId;
    }

    public PhraseInfo setId(long id) {
        mId = id;
        return this;
    }

    public String getSourceText() {
        return mSourceText;
    }

    public PhraseInfo setSourceText(String sourceText) {
        mSourceText = sourceText;
        return this;
    }

    public String getRecognizedText() {
        return mRecognizedText;
    }

    public PhraseInfo setRecognizedText(String recognizedText) {
        mRecognizedText = recognizedText;
        return this;
    }

    public float getScore() {
        return mScore;
    }

    public PhraseInfo setScore(float score) {
        mScore = score;
        return this;
    }
}
