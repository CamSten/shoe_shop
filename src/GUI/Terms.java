package GUI;
import Control.Event;
public class Terms {
    private Event.Subject subject;
    private Event.Action action;
    private String term;
    private String promptTerm;
    private String imperativeActionTerm;
    private String actionTerm;
    private String completedVerb;

    public Terms (Event event){
        this.subject = event.getSubject();
        this.action = event.getAction();
        setTerms();
    }
    private void setTerms() {

        if (subject == Event.Subject.NONE) {
            this.term = "job seeker";
            this.promptTerm = "name of the job seeker";
        }
        switch (action) {
            case SEARCH -> {
                this.actionTerm = "Searching for";
                this.completedVerb = "found";
            }

        }
    }
    public String getTerm() {
        return term;
    }
    public String getPromptTerm() {
        return promptTerm;
    }
    public String getImperativeActionTerm() {
        return imperativeActionTerm;
    }
    public String getActionTerm() {
        return actionTerm;
    }
    public String getCompletedVerb(){
        return completedVerb;
    }
}
