package Control;

public class Event {
    private Phase phase;
    private Action action;
    private final Subject subject;
    private final Origin origin;
    private final Outcome outcome;
    private Object contents;
    private Object extraContents;

    public Event(Phase phase, Action action, Subject subject, Origin origin, Outcome outcome, Object contents, Object extraContents){
        this.phase = phase;
        this.action = action;
        this.subject = subject;
        this.origin = origin;
        this.outcome = outcome;
        this.contents = contents;
        this.extraContents = extraContents;
    }
    public enum Phase {
        AWAIT_INPUT,
        SUBMIT,
        SELECT,
        DISPLAY,
        COMPLETE,
    }

    public enum Action {
        VALIDATE,
        CREATE_ACCOUNT,
        CHOOSE_TYPE,
        SEARCH,
        PURCHASE,
        EDIT,
        VIEW,
        LOG_OUT
    }
    public enum Subject {
        SHOE,
        CUSTOMER,
        CART,
        ADMIN,
        SALES,
        STOCK,
        NON_STOCK,
        NONE
    }
    public enum Origin {
        LOGIC,
        GUI
    }

    public enum Outcome {
        OK,
        NOT_FOUND,
        INVALID_INPUT,
        ALREADY_EXISTS,
        PENDING,
        FAILURE
    }
    public static Event initiate(){
        return new Event(
                Phase.AWAIT_INPUT,
                Action.VALIDATE,
                Subject.NONE,
                Origin.LOGIC,
                null,
                null,
                null
        );
    }

    public static Event awaitInput(Action action, Subject subject, Origin origin) {
        return new Event(
                Phase.AWAIT_INPUT,
                action,
                subject,
                origin,
                null,
                null,
                null
        );
    }
    public static Event select(Subject subject) {
        return new Event(
                Phase.SUBMIT,
                Action.VIEW,
                subject,
                Origin.GUI,
                null,
                null,
                null
        );
    }
    public static Event submit(Action action, Subject subject, Object contents, Object extraContents) {
        return new Event(
                Phase.SUBMIT,
                action,
                subject,
                Origin.GUI,
                Outcome.OK,
                contents,
                extraContents

        );
    }
    public static Event confirmComplete(Action action, Subject subject, Outcome outcome, Object data) {
        return new Event(
                Phase.COMPLETE,
                action,
                subject,
                Origin.LOGIC,
                outcome,
                data,
                null
        );
    }
    public static Event chooseType(Subject subject, Object data) {
        return new Event(
                Phase.COMPLETE,
                Action.CHOOSE_TYPE,
                subject,
                Origin.GUI,
                Outcome.OK,
                data,
                null
        );
    }
    public static Event error(Action action, Origin origin, Outcome outcome) {
        return new Event(
                Phase.COMPLETE,
                action,
                Subject.NONE,
                origin,
                outcome,
                null,
                null
        );
    }
    public static Event returnAdminInfo(Subject subject, Outcome outcome, Object data) {
        return new Event(
                Phase.COMPLETE,
                Action.VIEW,
                subject,
                Origin.LOGIC,
                outcome,
                data,
                Subject.ADMIN
        );
    }
    public static Event requestAdminInfo(Subject subject) {
        return new Event(
                Phase.SUBMIT,
                Action.VIEW,
                subject,
                Origin.GUI,
                Outcome.PENDING,
                null,
                Subject.ADMIN
        );
    }

    public Phase getPhase() {
        return phase;
    }

    public Action getAction() {
        return action;
    }

    public Subject getSubject() {
        return subject;
    }

    public Origin getOrigin() {
        return origin;
    }

    public Outcome getOutcome() {
        return outcome;
    }

    public Object getContents() {
        return contents;
    }
    public Object getExtraContents(){
        return extraContents;
    }
    public void setContents(Object input){
        this.contents = input;
    }
    public void setExtraContents(Object input){
        this.extraContents = input;
    }
    public void setPhase(Phase phase){
        this.phase = phase;
    }
    public void setAction(Action action){this.action = action;}

    @Override
    public String toString() {
        return super.toString();
    }
}
