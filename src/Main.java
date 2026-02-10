import Control.ApplicationManager;
import Control.Event;
import Model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    static Control.ApplicationManager appManager = new ApplicationManager();
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        appManager.Update(Event.awaitInput(Event.Action.VALIDATE, Event.Subject.NONE, Event.Origin.LOGIC));
    }


    private void addToCart() {

    }


}
// Skapa ett gränssnitt till din procedur AddToCart (alltså ett Java-program som låter
//        användaren lägga in produkter i en beställning.
//        Ett enkelt console-program räcker bra, men det är ok att bygga ett grafiskt gränssnitt om du vill.)
// Ett console-gränssnitt via kommandoraden kan se ut enligt följande:
// -    användaren blir promptad att skriva sitt användarnamn och lösenord för att logga in
//          (så att vi vet vilken användare som beställer).
// -    Prompta sedan användaren genom att skriva ut alla produkter som finns i lager,
//          användaren får välja en. Beroende på hur du har modellerat din databas
//          kan användaren behöva promptas flera gånger för att specifik produkt ska kunna pekas ut
//          (kanske färg, storlek etc. måste väljas separat?).
// -    Skriv den kod som behövs för att detta ska funka med just din databas.
//          När användaren har valt ut en unik produkt anropar du din stored procedure så att
//          produkten läggs in i aktuell beställningen (eller att en ny beställning, som innehåller
//          vald produkt, skapas upp).
// -    Se till att användaren får återkoppling om allt gick bra eller om fel uppstod när en
//          produkt lades till i beställningen.
// -    Ett krav är att användaren aldrig ska behöva se databasens interna id:n.
//          Antag att användaren vill välja en svart sko. Svart har id 145 i databasen.
//          När du listar färger som användaren ska välja mellan ska ”svart” skrivas ut, inte ”145”.
//          (Om det är så att dina produkter har id 1,2,3,4.. osv och du väljer att skriva ut alla produkter
//          i en numrerad lista där kunden får välja nummer från listan, och id:na råkar vara desamma som listnumren, så är detta ok)