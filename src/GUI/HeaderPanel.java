package GUI;

import Control.Event;
import Model.ProductTerm;

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
        System.out.println("HeaderPanel constructor is reached");
        if (event.getExtraContents() != null){
            System.out.println("extracontents instance of: " + event.getExtraContents().getClass());
        }
        this.decorator = decorator;
        this.event = event;
        setTerms(event);
        setBackground(Colors.bg());
        setLayout(new BorderLayout());
        setVisible(true);
        add(getHeaderPanel(), BorderLayout.CENTER);
    }
    private JPanel getHeaderPanel() {
        System.out.println();
        JPanel headerPanel = new JPanel(new GridLayout(2,1));
        headerPanel.setBackground(Colors.bg());

        JLabel header = new JLabel(resolveHeaderText());
        header.setBackground(Colors.panel());
        header.setForeground(Colors.accent());
        header.setFont(Fonts.getHeaderFont());
        headerPanel.add(header);

        String subHeaderText = resolveSubHeaderText();
        if (resolveSubHeaderText() != null){
            JLabel subHeader = new JLabel(subHeaderText);
            decorator.adjustLabel(subHeader);
            headerPanel.add(subHeader);
        }
        return headerPanel;
    }

    private String resolveHeaderText() {
        String text = "";
        if (event.getSubject() == Event.Subject.CUSTOMER && event.getContents() instanceof String){
            String name = (String) event.getExtraContents();
            text = "Welcome " + name;
        }
        else if (event.getOrigin() == Event.Origin.LOGIC) {
            if (event.getOutcome() == Event.Outcome.NOT_FOUND ||
                    event.getOutcome() == Event.Outcome.FAILURE) {
                text = "No results were found";
            }
            else if (event.getAction() == Event.Action.VIEW){
                text = "Browse shoes";
            }
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
        String text = "";
        if (event.getAction() == Event.Action.VIEW) {
            text = "Filter on:";
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
