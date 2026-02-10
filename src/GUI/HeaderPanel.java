package GUI;

import Control.Event;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HeaderPanel extends JPanel {
    private PanelDecorator decorator;
    private Control.Event event;
    private String subject;
    public String term;
    public String promptTerm = "";
    public String actionTerm = "";
    public String imperativeActionTerm;
    public String completedVerb;
    private String pluralNoun = "";
    private String pluralVerb = "s";
    private String pluralHas = "s";
    private String match = "";
    private int counter;

    public HeaderPanel(PanelDecorator decorator, Control.Event event){
        this.decorator = decorator;
        this.event = event;
        setTerms(event);
        setBackground(Colors.getBackgroundColor());
        setLayout(new BorderLayout());
        setVisible(true);
        add(getHeaderPanel(), BorderLayout.CENTER);
        if (event.getOutcome() == Event.Outcome.OK &&(event.getAction() == Event.Action.VIEW)) {
            add(getCounter(), BorderLayout.SOUTH);
        }
    }
    private JPanel getHeaderPanel() {
        System.out.println();
        JPanel headerPanel = new JPanel(new GridLayout(2,1));
        headerPanel.setBackground(Colors.getBackgroundColor());

        JTextArea header = new JTextArea(resolveHeaderText());
        header.setBackground(Colors.getBackgroundColor());
        header.setForeground(Colors.getHeaderColor());
        header.setFont(Fonts.getHeaderFont());
        headerPanel.add(header);

        String subHeaderText = resolveSubHeaderText();
        if (resolveSubHeaderText() != null){
            JTextArea subHeader = new JTextArea(subHeaderText);
            decorator.adjustHeader(subHeader);
            headerPanel.add(subHeader);
        }
        return headerPanel;
    }
    private JPanel getCounter(){
        JPanel wrapperPanel = new JPanel();
        JPanel countPanel = new JPanel(new GridLayout(1, 3));
        JLabel count = new JLabel("Total count:");
        JTextArea showNumber = new JTextArea(String.valueOf(counter));
        decorator.adjustLabel(count);
        decorator.adjustTextArea(showNumber);
        countPanel.add(count);
        countPanel.add(showNumber);
        wrapperPanel.add(countPanel);
        wrapperPanel.setBackground(Colors.getBackgroundColor());
        return wrapperPanel;
    }
    private String resolveHeaderText() {
        String text = actionTerm + " " + term;
        if (event.getOrigin() == Event.Origin.LOGIC) {
            if (event.getOutcome() == Event.Outcome.NOT_FOUND ||
                    event.getOutcome() == Event.Outcome.FAILURE) {
                text = getErrorHeader(event);
            }
            else if (event.getAction() == Event.Action.VIEW){
                text = "";
            }
        }
        else if (event.getAction() == Event.Action.CHOOSE_TYPE ){
            text = "";
        }
        return text;
    }

    private String resolveSubHeaderText() {
        if (!shouldAddSubHeader()){
            return null;
        }
        else if (event.getAction() == Event.Action.VIEW){
            return getConfirmHeader();
        }
        else if (event.getPhase() == Event.Phase.SUBMIT || event.getPhase() == Event.Phase.SELECT){
            return getInputHeader();
        }
        else {
            return getConfirmHeader();

        }
    }
    private boolean shouldAddSubHeader( ) {
        return event.getAction() != Event.Action.CHOOSE_TYPE && event.getOutcome() == Event.Outcome.OK;
    }
    private String getConfirmHeader(){
        String text = ";";
        if (event.getAction() == Event.Action.VIEW) {
            text = "The following " + term + pluralNoun + " ha"+ pluralHas + " been found:";
        }

        else {
            text = "The following " + term + pluralNoun + " contain" + pluralVerb + " your search term:";
        }
        return text;
    }

    private String getErrorHeader(Event event){
        return "No " + subject + " was found for " + actionTerm;
    }

    private String getInputHeader(){
        String text = "";
        if (event.getPhase() == Event.Phase.SELECT && event.getOrigin() == Event.Origin.GUI){
            text = "Submit new input:";
        }
        //(event.getAction() == Event.Action.EDIT || event.getAction() == Event.Action.ADD)
        else if (event.getPhase() == Event.Phase.SUBMIT)  {
            text = "Input the " + promptTerm + " that you would like to " + imperativeActionTerm + ":";

        }
        return text;
    }

    public void setTerms(Event event) {
        Terms terms = new Terms(event);
        this.term = terms.getTerm();
        this.promptTerm = terms.getPromptTerm();
        this.imperativeActionTerm = terms.getImperativeActionTerm();
        this.actionTerm = terms.getActionTerm();
        this.completedVerb = terms.getCompletedVerb();
        Object contents = event.getContents();

        }
    }
