# Satogaeri Überlegungen

## Erste Ideen
* Nach jedem Schritt prüfen, ob Kreise verfügbar sind, die sich
  ausschließlich in eine Richtung verschieben lassen (Aktion ausführen)
* Bahnen von Kreisen als markiert kennzeichnen (mit `boolean`-Wert in der Zelle)
* Algorithmus anfangen mit Kreisen, die Zahlen enthalten und sich **nur in eine Richtung**
  verschieben lassen (die einzige Möglichkeit)
* Kreise mit 0 als Wert können auch direkt als final markiert werden (einzige Möglichkeit,
  können sich nicht bewegen und bleiben auf dem aktuellen Feld stehen)
  
## Weitergehende Betrachtungen
* Nachdem alle initialen Züge mit nur einer Möglichkeit gemacht wurden und alle Regionen mit Kreisen der Distanzklasse 0
  als invariant markiert wurden, fängt der Algorithmus nun mit Kreisen an, die aus bereits invarianten Regionen herausverschoben
  werden müssen, da dort schon ein Kreis final gesetzt ist
* Anwendung eines **Warnsdorf-Algorithmus** zur Bestimmung des erstbesten Feldes mit der geringsten Anzahl an freien
  Nachbarfeldern, weil dort die Wahrscheinlichkeit einer Überkreuzung von Pfaden mehrerer Kreise am geringsten ist
* Nach jeder Iteration soll geprüft werden, ob es Kreise gibt, die nur einen möglichen Zug erlauben (nur eine gültige Richtung)
* Nach jeder Iteration kann weiterhin geprüft werden, ob es Zellen gibt, die nur von einem Kreis angesprungen werden können
    * Diese Zellen dürfen allerdings nicht invariant sein
* Implementierung eines kombinierten Backtracking-Algorithmus mit Eintragung in die Liste `recentMoves` und eines
  Listenanzeigers `movePointer` zur Umkehrung der getätigten Schritte
    * Dazu muss allerdings die Klasse `MoveProposal` um eine Eintragung erweitert werden (z.B. Referenz auf den Kreis
      oder ID), um den Kreis zum gegangenen Zug zuzuordnen
    * Neue Schritte werden hinten an die Liste angehängt und falls es keine Lösung gibt, wird die Liste nach dem
      LIFO-Prinzip (_Last In, First Out_) von hinten abgearbeitet, bis der Algorithmus zu einem Punkt einer Verzweigung
      kommt, die noch nicht gegangen wurde
    * Jede neue Eintragung bewirkt das richtige Setzen des `movePointer`
* Grundsätzlich sollen nach den initialen Zügen, die nur eine Möglichkeit einer Bewegung des Kreises zulassen, und nach
  der Invariante der Distanzklasse 0 alle Züge in die Backtracking-Liste aufgenommen werden
* Für Kreise **ohne Distanzangabe** wird parallel zu den Versuchen der anderen Kreise ein Versuch in alle Richtungen unternommen
  (angefangen bei _Links_, _Oben_, _Rechts_ und _Unten_)
    * Während einer jeden Zugberechnung in eine Richtung wird zuerst der am weitesten mögliche Zug des Kreises angenommen
    * Dies verhindert, dass der **noch** mögliche Zug durch einen anderen Kreis abgeschnitten werden kann
    * In jeder Iteration wird der Kreis nun ein Feld wieder zurückgezogen und geschaut, ob das aktuelle Feld geeignet ist
    * Zusatz: Auf jedem Feld kann geprüft werden, ob die Zelle noch von einem anderen Kreis erreicht werden kann
       * Falls nein, soll der Kreis stehen bleiben
       * Falls ja, suche weiter nach einem Feld
    * Jeder gegangene Zug wird in der Liste `recentMoves` protokolliert und bei Scheitern des Versuchs wieder herausgenommen
    * Die Berechnung soll parallel zu den anderen Heuristiken stattfinden, d.h. es muss gespeichert werden, an welcher Position
      welcher Kreis stehen geblieben ist und was schon ausprobiert wurde
    * In jeder Iteration soll von diesen Kreisen nur ein Schritt getätigt werden, um die Berechnung nicht zu verzögern
* Neue Heuristik: Finde Regionen mit nur einer Zelle (in diesem Beispiel sind es 2) und finde heraus, welcher Kreis als Einziger
  die Möglichkeit hat, dort hineinzuspringen
* Weitere Heuristik: Finde Kreise, die zwangsläufig in einer bestimmten Region landen, egal welchen Zug sie machen und falls bereits
  ein Kreis in dieser Region liegt, muss er dort heraus
* Weitere Heuristik: Finde Kreise, die bewegt werden können und damit einem anderen Kreis den Weg abschneiden, woraufhin dieser
  nur noch eine oder mehr Möglichkeiten hat, sich zu bewegen
    * Diese restlichen Möglichkeiten müssen geprüft werden und wenn sie zu keinem Ergebnis führen, dann mache den Zug in die
      Richtung, in der der ursprüngliche Kreis ihm den Weg abgeschnitten hätte
    * Daraufhin hat der ursprüngliche Kreis auch eine Möglichkeit weniger
    
 **Dokumentation der Rundenfunktionen `solelySolutions()` und `lonelyCells()`**
