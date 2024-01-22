package QuizCards;

public class QuizCard {
    private final String question, answer;

    public QuizCard(String question, String answer){
        this.question = question;
        this.answer = answer;
    }

    String getQuestion() { return this.question; }
    String getAnswer() { return this.answer; }
}
