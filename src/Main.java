import Control.ApplicationManager;
import Control.Event;
import java.sql.*;

public class Main {
    static Control.ApplicationManager appManager = new ApplicationManager();
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
      appManager.Update(Event.awaitInput(Event.Action.VALIDATE, Event.Subject.NONE, Event.Origin.LOGIC));
    }
}