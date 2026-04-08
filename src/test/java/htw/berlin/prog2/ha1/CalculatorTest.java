package htw.berlin.prog2.ha1;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Retro calculator")
class CalculatorTest {

    @Test
    @DisplayName("should display result after adding two positive multi-digit numbers")
    void testPositiveAddition() {
        Calculator calc = new Calculator();

        calc.pressDigitKey(2);
        calc.pressDigitKey(0);
        calc.pressBinaryOperationKey("+");
        calc.pressDigitKey(2);
        calc.pressDigitKey(0);
        calc.pressEqualsKey();

        String expected = "40";
        String actual = calc.readScreen();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("should display result after getting the square root of two")
    void testSquareRoot() {
        Calculator calc = new Calculator();

        calc.pressDigitKey(2);
        calc.pressUnaryOperationKey("√");

        String expected = "1.41421356";
        String actual = calc.readScreen();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("should display error when dividing by zero")
    void testDivisionByZero() {
        Calculator calc = new Calculator();

        calc.pressDigitKey(7);
        calc.pressBinaryOperationKey("/");
        calc.pressDigitKey(0);
        calc.pressEqualsKey();

        String expected = "Error";
        String actual = calc.readScreen();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("should display error when drawing the square root of a negative number")
    void testSquareRootOfNegative() {
        Calculator calc = new Calculator();

        calc.pressDigitKey(7);
        calc.pressNegativeKey();
        calc.pressUnaryOperationKey("√");

        String expected = "Error";
        String actual = calc.readScreen();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("should not allow multiple decimal dots")
    void testMultipleDecimalDots() {
        Calculator calc = new Calculator();

        calc.pressDigitKey(1);
        calc.pressDotKey();
        calc.pressDigitKey(7);
        calc.pressDotKey();
        calc.pressDigitKey(8);

        String expected = "1.78";
        String actual = calc.readScreen();

        assertEquals(expected, actual);
    }

    //TODO hier weitere Tests erstellen
    //Aufgabenteil1a
    @Test
    @DisplayName("should follow mathematical rule: product of two negative numbers is positive")
    void testPowerNotNegative(){
        Calculator calc = new Calculator();

        calc.pressDigitKey(2);
        calc.pressNegativeKey();
        calc.pressBinaryOperationKey("x");
        calc.pressDigitKey(2);
        calc.pressNegativeKey();
        calc.pressEqualsKey();

        String expected = "4";
        String actual = calc.readScreen();

        assertEquals(expected, actual);
    }
    //Erster Commit

    //Aufgabenteil1b
    @Test
    @DisplayName("should react correctly to multiple use of equals key")
    void testMultipleUseOfEqualsKey(){
        Calculator calc = new Calculator();

        calc.pressDigitKey(3);
        calc.pressBinaryOperationKey("+");
        calc.pressDigitKey(3);
        calc.pressEqualsKey();
        calc.pressEqualsKey();
        calc.pressEqualsKey();

        String expected = "12";
        String actual = calc.readScreen();

        assertEquals(expected, actual);
    }
    //Zweiter Commit

    //Aufgabenteil2 - 7 Tests für fehlerhafte Funktionalitäten/Abweichungen vom Verhalten des Online-Rechners
    @Test
    @DisplayName("revoke last stored value")
    void test2a(){
        Calculator calc = new Calculator();

        calc.pressDigitKey(1);
        calc.pressBinaryOperationKey("+");
        calc.pressDigitKey(4);
        calc.pressEqualsKey();
        calc.pressClearKey();
        calc.pressEqualsKey();

        String expected = "4";  //4 ist der letzte eingegebene Wert und sollte nun im Speicher stehen
        String actual = calc.readScreen();

        assertEquals(expected, actual);
    }
    /*
    Verdacht:
    Methode Calculator.pressClearKey() funktioniert nicht wie in der Beschreibung angegeben.
    Die Methode setzt beim Betätigen unverzüglich den letzten Wert auf 0, soll dies aber erst bei zweifachem Aufruf.
    Dritter Commit
     */

    //ACHTUNG: DIESER TEST DECKT KEINE FEHLERHAFTE FUNKTIONALITÄT AUF, DER RECHNER ERFÜLLT DIESE ANFORDERUNG
    @Test
    @DisplayName("should respond appropriately to request for division by 0")
    void test2b(){
        Calculator calc = new Calculator();

        calc.pressDigitKey(4);
        calc.pressBinaryOperationKey("/");
        calc.pressDigitKey(0);
        calc.pressEqualsKey();

        String expected = "Error";
        String actual = calc.readScreen();

        assertEquals(expected, actual);
    }
    /*
    Verdacht:
    Division durch 0 wirft nicht die vorgesehene Exception/ schreibt nicht den vorgesehenen Text auf die Konsole..
     */

    @Test
    @DisplayName("Second test for incorrect implemented functionality")
    void test2c(){
        Calculator calc = new Calculator();

        calc.pressDigitKey(9);
        calc.pressUnaryOperationKey("√");
        calc.pressEqualsKey();  //Diese Zeile ist Optional, im Display sollte bereits beim Drücken von √ das Ergebnis stehen (wie auch implementiert)

        String expected = "3";
        String actual = calc.readScreen();

        assertEquals(expected, actual);
    }
    /*
    Verdacht:
    Funktionalitäten der Operation Calculator.pressUnaryOperationKey() sind nicht auflösbar:
    Die Methode legt das Ergebnis der Kalkulation in der Variable Screen als String ab,
    diese kan allerdings von Calculator.pressEqualsKey() nicht gelesen werden.
    FALSCH! Das funktioniert wohl und liefert korrekte Ergebnisse.
    Allerdings liefert die Methode einen double als Ergebnis (var result), während der Online-Rechner die Ganzzahl ausgibt.
    */

    @Test
    @DisplayName("should calculate sqrt correctly as double")
    void test2d(){
        Calculator calc = new Calculator();

        calc.pressDigitKey(8);
        calc.pressUnaryOperationKey("√");
        calc.pressEqualsKey();  //Diese Zeile ist Optional, im Display sollte bereits beim Drücken von √ das Ergebnis stehen (wie auch implementiert)

        String expected = "2.82842712";
        String actual = calc.readScreen();

        assertEquals(expected, actual);
    }
    /*
    Der Rechner berechnet offenbar nicht die korrekte Ausgabe für die Quadratwurzel aus 8.
    Das könnte am Format der Ausgabe liegen. Die in der Test-Methode angegebene Lösung ist die, die der Online-Rechner hergibt.
     */

    @Test
    @DisplayName("should give back identity")
    void test2e(){
        Calculator calc = new Calculator();

        calc.pressDigitKey(1);
        calc.pressDigitKey(4);
        calc.pressEqualsKey();

        String expected = "14";
        String actual = calc.readScreen();

        assertEquals(expected, actual);
    }
    /*
    Die Methode Calculator.pressEqualsKey() kann nicht damit umgehen, wenn keine Operation gewählt wird.
     */

    //ACHTUNG: DIESER TEST DECKT KEINE FEHLERHAFTE FUNKTIONALITÄT AUF, DER RECHNER ERFÜLLT DIESE ANFORDERUNG
    @Test
    @DisplayName("should control, whether input accessibility is correct")
    void test2f(){
        Calculator calc = new Calculator();

        calc.pressDigitKey(3);
        calc.pressBinaryOperationKey("-");
        calc.pressDigitKey(3);
        calc.pressDigitKey(3);
        calc.pressEqualsKey();

        String expected = "0";
        String actual = calc.readScreen();

        assertEquals(expected, actual);
    }
    /*
    Hintergrund des Tests war, dass ich wegen der Formulierung
    "(...) || latestValue == Double.parseDouble(screen)) screen = "";"
    den Verdacht hatte, dass - falls die zweite eingegebene Zahl einstellig ist
    und der ersten entspricht - die zweite Stelle der zweiten Zahl nicht korrekt gelesen werden kann.
     */

    @Test
    @DisplayName("should start new calculation from start")
    void test2g(){
        Calculator calc = new Calculator();

        calc.pressDigitKey(4);
        calc.pressBinaryOperationKey("x");
        calc.pressDigitKey(3);
        calc.pressEqualsKey();
        calc.pressDigitKey(5);

        String expected = "5";
        String actual = calc.readScreen();

        assertEquals(expected, actual);
    }
    /*
    Test soll prüfen, ob die Variablenbelegung der Objektattribute nach dem Durchführen einer Berechnung
    dem Start einer Nächsten im Weg steht.
     */

    @Test
    @DisplayName("combined test with control for input accessibility and end-calculation behaviour")
    void test2h(){
        Calculator calc = new Calculator();

        calc.pressDigitKey(3);
        calc.pressBinaryOperationKey("x");
        calc.pressDigitKey(3);
        calc.pressEqualsKey();
        calc.pressDigitKey(3);

        String expected = "3";
        String actual = calc.readScreen();

        assertEquals(expected, actual);
    }
    /*
    Hier sollen die beiden Vorgänger-Tests gleichzeitig geprüft werden.
    Augenscheinlich kann ich das Verhalten der Methode pressDigitKey() aus dem Test 2f
    noch nicht richtig kontrollieren.
    ACHTUNG:
    Die Bedingung ist daher erforderlich, da die erste Zahl (und damit bei einstelligen Zahlen die erste Ziffer)
    nach dem Wählen der Rechenoperation weiterhin auf dem Display bestehen bleibt!
    Lediglich das Objektattribut wird belegt, das Display (Attribut screen) wird nicht auf 0 gesetzt.
    Ohne die Bedingung würde im Fall einer einstelligen ersten Zahl die entsprechende Ziffer fälschlich
    als führende Ziffer der zweiten Zahl erkannt/gelesen.
     */

    @Test
    @DisplayName("correct behaviour in consecutive calculations")
    void test2i(){
        Calculator calc = new Calculator();

        calc.pressDigitKey(2);
        calc.pressBinaryOperationKey("+");
        calc.pressDigitKey(7);
        calc.pressBinaryOperationKey("-");

        String expected = "9";
        String actual = calc.readScreen();

        assertEquals(expected, actual);
    }
    /*
    Methode soll korrektes Verhalten bei aufeinanderfolgenden Berechnungen prüfen
     */
}