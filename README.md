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

