package com.example.btlapp;

public class Question {
    private int id;
    private String content;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private int correctAnswer; // 1, 2, 3, 4
    private String explanation;
    private Integer imageResId;
    private boolean isCritical;

    public Question(int id, String content, String optionA, String optionB, String optionC, String optionD, int correctAnswer, String explanation) {
        this(id, content, optionA, optionB, optionC, optionD, correctAnswer, explanation, null, false);
    }

    public Question(int id, String content, String optionA, String optionB, String optionC, String optionD, int correctAnswer, String explanation, Integer imageResId, boolean isCritical) {
        this.id = id;
        this.content = content;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
        this.imageResId = imageResId;
        this.isCritical = isCritical;
    }

    public int getId() { return id; }
    public String getContent() { return content; }
    public String getOptionA() { return optionA; }
    public String getOptionB() { return optionB; }
    public String getOptionC() { return optionC; }
    public String getOptionD() { return optionD; }
    public int getCorrectAnswer() { return correctAnswer; }
    public String getExplanation() { return explanation; }
    public Integer getImageResId() { return imageResId; }
    public boolean isCritical() { return isCritical; }
}
