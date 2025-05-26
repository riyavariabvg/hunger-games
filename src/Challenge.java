public class Challenge {
    private String prompt;
    private String solution;
    private boolean isCompleted;

    // Constructor
    public Challenge(String prompt, String solution) {
        this.prompt = prompt;
        this.solution = solution.toLowerCase(); // make comparison case-insensitive
        this.isCompleted = false;
    }


    // Get the challenge prompt
    public String getPrompt() {
        return prompt;
    }

    // Check if the challenge has been completed
    public boolean isCompleted() {
        return isCompleted;
    }

    // Attempt to solve the challenge
    public boolean attempt(String answer) {
        if (answer == null) return false;
        if (answer.toLowerCase().equals(solution)) {
            isCompleted = true;
            return true;
        }
        return false;
    }

    // Optional: give a generic hint
    public String getHint() {
        return "Think carefully... what would help you survive?";
    }

}