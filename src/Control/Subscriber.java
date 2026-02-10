package Control;

import java.sql.SQLException;

public interface Subscriber {

    void Update(Event event) throws SQLException, ClassNotFoundException;
}
