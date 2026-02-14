package Model.Repositories;

import Control.Event;
import Control.Subscriber;
import Model.DatabaseRelay;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminRepo implements Subscriber {
    private DatabaseRelay databaseRelay;
    private Connection c;

    public AdminRepo (DatabaseRelay databaseRelay, Connection c){
        this.databaseRelay = databaseRelay;
        this.c = c;
    }
    private void assessIfAdmin(Event event) throws SQLException, ClassNotFoundException {
        List<String> userInput = new ArrayList<>();
        boolean isAdmin = false;
        Event.Outcome outcome = Event.Outcome.NOT_FOUND;
        if (event.getContents() instanceof List list){
            if (!list.isEmpty() && list.getFirst() instanceof String){
                userInput = (List<String>) event.getContents();
                String email = userInput.get(0).trim();
                String userPassword = userInput.get(1).trim();

                CallableStatement s = c.prepareCall("CALL checkIfAdmin(?, ?, ?)");
                s.setString(1, email);
                s.setString(2, userPassword);
                ResultSet rs = s.executeQuery();
                isAdmin = s.getBoolean(3);
            }
        }
        if (isAdmin){
            outcome = Event.Outcome.OK;
        }
        databaseRelay.Relay(new Event(Event.Phase.COMPLETE, Event.Action.VALIDATE, Event.Subject.ADMIN, Event.Origin.LOGIC, outcome, userInput, null));
    }

    @Override
    public void Update(Event event) throws SQLException, ClassNotFoundException {
        assessIfAdmin(event);
    }
}
