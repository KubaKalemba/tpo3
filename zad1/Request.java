package zad1;

import java.io.Serializable;

public class Request implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String wordToTranslate;
    private final String languageCode;

    public Request(String wordToTranslate, String languageCode) {
        this.wordToTranslate = wordToTranslate;
        this.languageCode = languageCode;
    }

    public String getWordToTranslate() {
        return wordToTranslate;
    }

    public String getTargetLanguageCode() {
        return languageCode;
    }
}
