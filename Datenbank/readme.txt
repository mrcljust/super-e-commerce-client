Zur Inbetriebnahme muss XAMPP lokal installiert sein und sowohl Apache als auch MySQL gestartet sein.

Auf das Webinterface kann dann über die Adresse http://localhost/phpmyadmin/ im Webbrowser zugegriffen werden.
Hier muss eine neue Datenbank mit dem Namen "sep" angelegt werden.
In der Datenbank "sep" dann oben auf "Importieren" klicken und die Datei "sep.sql" importieren.
Dann sollten die 4 Tabellen users, products, orders, categories angelegt werden.

Im Quellcode ist die Datenbank über 127.0.0.1 oder localhost erreichbar, standardmäßig über den Port 3306 (so lassen).