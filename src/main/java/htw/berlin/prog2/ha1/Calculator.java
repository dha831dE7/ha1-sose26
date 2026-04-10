package htw.berlin.prog2.ha1;

/**
 * Eine Klasse, die das Verhalten des Online Taschenrechners imitiert, welcher auf
 * https://www.online-calculator.com/ aufgerufen werden kann (ohne die Memory-Funktionen)
 * und dessen Bildschirm bis zu zehn Ziffern plus einem Dezimaltrennzeichen darstellen kann.
 * Enthält mit Absicht noch diverse Bugs oder unvollständige Funktionen.
 */

/*
Prinzipielle Funktionalität der Klasse:
Es existiert eine aktuelle Eingabe (screen), deren Inhalt in zwei Variablen (first/secondValue) gespeichert werden kann.
Daneben kann eine Operation festgelegt werden, die sich auf die erste oder beide Variablen bezieht.
Das Wählen einer Operation setzt den ersten Speicher, das Wählen des Gleichheitszeichens den Zweiten.
 */
public class Calculator {

    //ATTRIBUTE
    private String screen = "0";    //Hier wird die aktuelle Eingabe abgelegt

    private double firstValue; //Es kann immer genau ein Wert im Speicher festgehalten werden, der mit einer Operation mit einem zweiten verknüpft werden kann
    private double secondValue; //Speicherplatz für zweite Zahl der Rechenoperation

    private String latestOperation = "";    //In dieser Variable kann eine gewählte Operation gespeichert werden
    double result;


    //METHODEN
    //1
    /**
     * @return den aktuellen Bildschirminhalt als String
     */
    public String readScreen() {
        return screen;  //Beim Attribut screen handelt es sich um die aktuelle Eingabe
    }

    //2
    /**
     * Empfängt den Wert einer gedrückten Zifferntaste. Da man nur eine Taste auf einmal
     * drücken kann muss der Wert positiv und einstellig sein und zwischen 0 und 9 liegen.
     * Führt in jedem Fall dazu, dass die gerade gedrückte Ziffer auf dem Bildschirm angezeigt
     * oder rechts an die zuvor gedrückte Ziffer angehängt angezeigt wird.
     * @param digit Die Ziffer, deren Taste gedrückt wurde
     */
    public void pressDigitKey(int digit) {
        if(digit > 9 || digit < 0) throw new IllegalArgumentException();

        if(screen.equals("0") || firstValue == Double.parseDouble(screen)) screen = "";
        /*
        Der Vergleich latestValue == Double.parseDouble(screen) führt für den Fall, dass zwei gleiche Ziffern hintereinander
        eingegeben werden dazu, dass die aktuelle Eingabe geleert wird..? (Siehe Test2f)
        JA, UND DIES IST AUCH KORREKT/SINNVOLL!
        (vergleiche Kommentar unter Test2h)
         */

        screen = screen + digit;
    }

    //3
    private boolean clearStorage = false;   //Dieses Objektattribut soll ermöglichen, zwischen dem ersten und dem zweiten Benutzen des clearKey zu unterscheiden
    /**
     * Empfängt den Befehl der C- bzw. CE-Taste (Clear bzw. Clear Entry).
     * Einmaliges Drücken der Taste löscht die zuvor eingegebenen Ziffern auf dem Bildschirm
     * so dass "0" angezeigt wird, jedoch ohne zuvor zwischengespeicherte Werte zu löschen.
     * Wird daraufhin noch einmal die Taste gedrückt, dann werden auch zwischengespeicherte
     * Werte sowie der aktuelle Operationsmodus zurückgesetzt, so dass der Rechner wieder
     * im Ursprungszustand ist.
     */
    public void pressClearKey() {
        screen = "0";

        /*
        Diese Bedingung ist nicht sinnvoll, aber erforderlich um sicherzustellen,
        dass der Rechner sich wie der Online-Rechner verhält (vgl Tests 2a und 2e).
        */
        if(operationCheckIn){
            firstValue = 0.0;
        }

        if(clearStorage){
            latestOperation = "";
            operationCheckIn = false;

            firstValue = 0.0;
            firstValueCheckIn = false;

            secondValue = 0.0;
            secondValueCheckIn = false;

            clearStorage = false;
            consecutive = false;
            repeat = false;
        }else{
            clearStorage = true;
        }
        /*
        Die Funktionsbeschreibung erfordert eigentlich,
        dass der Wert der Variable latestValue erst dann aus dem Speicher gelöscht wird, wenn man
        die Methode Calculator.pressClearKey() ein zweites Mal hintereinander aufruft. (Siehe Test2a)

        Verbesserung der Funktionalität so, dass sie dem Test genügt (erster Bugfix-Commit)
        Problem: als latestValue wird die erste Zahl abgelegt und nicht die Zweite
        */
    }

    //4
    private boolean consecutive = false;
    /**
     * Empfängt den Wert einer gedrückten binären Operationstaste, also eine der vier Operationen
     * Addition, Substraktion, Division, oder Multiplikation, welche zwei Operanden benötigen.
     * Beim ersten Drücken der Taste wird der Bildschirminhalt nicht verändert, sondern nur der
     * Rechner in den passenden Operationsmodus versetzt.
     * Beim zweiten Drücken nach Eingabe einer weiteren Zahl wird direkt des aktuelle Zwischenergebnis
     * auf dem Bildschirm angezeigt. Falls hierbei eine Division durch Null auftritt, wird "Error" angezeigt.
     * @param operation "+" für Addition, "-" für Substraktion, "x" für Multiplikation, "/" für Division
     */
    public void pressBinaryOperationKey(String operation){
        //Zurücksetzen der Unterscheidungsvariable für die Löschen-Taste (Operation soll beim ersten Drücken noch erhalten bleiben)
        clearStorage = false;

        //Verzweigung, falls zuvor abgeschlossene Berechnung existiert:
        if(consecutive){
            //Belegen der für die Kalkulation erforderlichen Variablen
            secondValue = Double.parseDouble(screen);
            secondValueCheckIn = true;

            //Ausführen der mathematischen Kalkulation mit der aktuellen Variablenbelegung
            backgroundCalculation();

            //Behandlung der Anzeige
            screen = Double.toString(result);
            if(screen.endsWith(".0")) screen = screen.substring(0,screen.length()-2);
            if(screen.contains(".") && screen.length() > 11) screen = screen.substring(0, 10);
        }

        //Verzweigung, falls Belegung der Variablen für neue Berechnung gestartet werden soll
        if(!readyToCalculate()){
            firstValue = Double.parseDouble(screen);
            firstValueCheckIn = true;

            latestOperation = operation;
            operationCheckIn = true;

            //Erforderliche Unterscheidungsvariable, damit mit erneutem Drücken der Taste Funktionalität des Gleichheitszeichens umfasst wird
            consecutive = true;
        }else{
            //Gerade unklar
            latestOperation = operation;
            repeat = false;
        }
    }
    /*
    Programmierung der zweiten im Javadoc beschriebenen Funktionalität.
     */

    //5
    /**
     * Empfängt den Wert einer gedrückten unären Operationstaste, also eine der drei Operationen
     * Quadratwurzel, Prozent, Inversion, welche nur einen Operanden benötigen.
     * Beim Drücken der Taste wird direkt die Operation auf den aktuellen Zahlenwert angewendet und
     * der Bildschirminhalt mit dem Ergebnis aktualisiert.
     * @param operation "√" für Quadratwurzel, "%" für Prozent, "1/x" für Inversion
     */
    public void pressUnaryOperationKey(String operation) {
        if(!firstValueCheckIn){
            firstValue = Double.parseDouble(screen);
            firstValueCheckIn = true;
        }else{
            firstValue = Double.parseDouble(screen);
        }
        /*
        Im else-Zweig wird das korrekte Verhalten der Methode in der konsekutiven Verwendung sichergestellt.
         */

        latestOperation = operation;
        operationCheckIn = true;

        var result = switch(operation) {
            case "√" -> Math.sqrt(Double.parseDouble(screen));
            case "%" -> Double.parseDouble(screen) / 100;
            case "1/x" -> 1 / Double.parseDouble(screen);
            default -> throw new IllegalArgumentException();
        };

        screen = Double.toString(result);
        if(screen.equals("NaN")) screen = "Error";

        if(screen.endsWith(".0")) screen = screen.substring(0,screen.length()-2);
        if(screen.contains(".") && screen.length() > 11) screen = screen.substring(0, 10);

        repeat = false;
    }

    //6
    /**
     * Empfängt den Befehl der gedrückten Dezimaltrennzeichentaste, im Englischen üblicherweise "."
     * Fügt beim ersten Mal Drücken dem aktuellen Bildschirminhalt das Trennzeichen auf der rechten
     * Seite hinzu und aktualisiert den Bildschirm. Daraufhin eingegebene Zahlen werden rechts vom
     * Trennzeichen angegeben und daher als Dezimalziffern interpretiert.
     * Beim zweimaligem Drücken, oder wenn bereits ein Trennzeichen angezeigt wird, passiert nichts.
     */
    public void pressDotKey() {
        if(!screen.contains(".")) screen = screen + ".";
    }
    /*
    Methode kontrolliert, ob es bereits einen Dezimalpunkt gibt und fügt ihn hinzu, falls nicht.
     */

    //7
    /**
     * Empfängt den Befehl der gedrückten Vorzeichenumkehrstaste ("+/-").
     * Zeigt der Bildschirm einen positiven Wert an, so wird ein "-" links angehängt, der Bildschirm
     * aktualisiert und die Inhalt fortan als negativ interpretiert.
     * Zeigt der Bildschirm bereits einen negativen Wert mit führendem Minus an, dann wird dieses
     * entfernt und der Inhalt fortan als positiv interpretiert.
     */
    public void pressNegativeKey() {
        screen = screen.startsWith("-") ? screen.substring(1) : "-" + screen;
    }
    /*
    Ternärer Operator fügt Vorzeichen hinzu oder entfernt es, je nachdem ob das erste Zeichen
    der aktuellen Eingabe das Vorzeichen war oder nicht
     */

    //8
    private boolean repeat = false; //Hilfsattribut
    /**
     * Empfängt den Befehl der gedrückten '='-Taste.
     * Wurde zuvor keine Operationstaste gedrückt, passiert nichts.
     * Wurde zuvor eine binäre Operationstaste gedrückt und zwei Operanden eingegeben, wird das
     * Ergebnis der Operation angezeigt. Falls hierbei eine Division durch Null auftritt, wird "Error" angezeigt.
     * Wird die Taste weitere Male gedrückt (ohne andere Tasten dazwischen), so wird die letzte
     * Operation (ggf. inklusive letztem Operand) erneut auf den aktuellen Bildschirminhalt angewandt
     * und das Ergebnis direkt angezeigt.
     */
    public void pressEqualsKey() {
        if(!repeat) {
            secondValue = Double.parseDouble(screen);
            secondValueCheckIn = true;
            repeat = true;
        }

        if(readyToCalculate()) {
            backgroundCalculation();
        }else{
            result = secondValue;
        }

        screen = Double.toString(result);
        if(screen.equals("Infinity")) screen = "Error";

        //Hier noch lediglich Einkürzen des Ergebnisses
        if(screen.endsWith(".0")) screen = screen.substring(0,screen.length()-2);
        if(screen.contains(".") && screen.length() > 11) screen = screen.substring(0, 10);
    }

    //9
    private boolean firstValueCheckIn = false;
    private boolean secondValueCheckIn = false;
    private boolean operationCheckIn = false;
    /*
    Die Attribute readyToCalculate und die drei CheckIn-Attribute sollen sicherstellen, dass der Rechner
    'weiß', ob die entsprechenden Attribute auch mit den durch den Benutzer gewünschten Werten belegt sind.
     */
    /**
     * Diese Methode kann angeben, ob alle drei (bzw. zwei) für eine Berechnung erforderlichen Eingaben
     * (erste, ggf zweite Zahl und Operation) auch durch den Bediener so gewünscht sind.
     * @return den Zustand, der drei mit logischem und verknüpften CheckIn-Attribute
     */
    private boolean readyToCalculate(){
        return firstValueCheckIn && secondValueCheckIn && operationCheckIn;
    }

    //10
    /**
     * Diese Methode lagert die Kalkulation hinter der Methode pressEqualsKey() aus,
     * sodass sie nur noch Display- und Variablenbelegungs-Funktionen übernimmt.
     * Daneben dient sie dazu, im Fall konsekutiver Operationen Schrittweise
     * das korrekte Ergebnis anzuzeigen.
     */
    public void backgroundCalculation(){
        if(readyToCalculate()) {
            switch (latestOperation) {
                case "+": {
                    result = firstValue + secondValue;
                    break;
                }
                case "-": {
                    result = firstValue - secondValue;
                    break;
                }
                case "x": {
                    result = firstValue * secondValue;
                    break;
                }
                case "/": {
                    result = firstValue / secondValue;
                    break;
                }
                default: //throw new IllegalArgumentException();
                {
                    result = secondValue;
                    break;
                }
            }
            firstValue = result;    //Ergebnis im ersten Speicher ablegen
        }
    }
}

/*
Wichtig fürs mathematische Verständnis:
'Berechnen' bedeutet in diesem Rechner, die Variable result mit einem Inhalt zu belegen.
 */