# Satogaeri Puzzle
Satogaeri ist ein NP-vollständiges Problem.

[How to solve Satogaeri](http://www.nikoli.com/en/puzzles/satogaeri/rule.html)

## Todos
* [ ] Schritte und Bedingungen zur Prüfung im Algorithmus notieren
* [ ] Müller wegen Abbildung auf Aufgabenblatt fragen, Regionen haben mehr als einen Kreis in Lösung
 --&gt; (hat sich erledigt, Abbildung ist korrekt)
 
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

