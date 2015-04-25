# About

Deze Java-applicatie is (voornamelijk) gemaakt om te kunnen communiceren met de meetbordjes van SEFLab.
De applicatie is ontwikkeld met behulp van:

- Het PAD forum (https://home.informatica.hva.nl/forum/index.php/board,37.0.html)
- Jan Derriks (Onderzoek & zijn applicatie: http://pastebin.com/iTvfcucf)
- Emile (Uitleg & formules)


# Installatie

- Download deze bestanden
- Sluit het meetbordje aan op je computer (Windows, Mac, Linux)

Wanneer je onder Windowswerkt (Geen Windows dus uit mijn hoofd):

- Controleer of je het meetbordje kan vinden in apparaatbeheer (moet staan onder 'Communicatie' of 'USB apparaten')

<hr />
<u>Wanneer je onder Windows 8 werkt moet je eerst deze stappen volgen:</u><br /><br />
Eerst zorgen dat je 'niet-geauthoriseerde' drivers mag installeren.<br />
Hoe je dit kan doen staat hier uitgelegd:<br />
http://www.pcpro.co.uk/blogs/2012/08/06/getting-older-drivers-to-work-in-windows-8<br />
Volg ALLEEN de stappen onder 'Installing unsigned drivers' !!<br />
<hr />

- Druk met rechter muisknop op het meetbordje in apparaatbeheer en druk op 'software installeren' om de driver te installeren
- Bij het installeren van de driver moet je opgeven dat je een driver vanaf je computer wilt installeren, kies hier dan 
  voor de map 'driver' welke bij deze applicatie zit.
- Volg de stappen en als het goed is moet de driver geinstalleerd zijn.


<br /><br /><br />


Wanneer je de driver geinstalleerd hebt of niet Windows gebruikt kan je aan de slag met de applicatie. Open de folder 'java-seriele-poort' in je IDE (Netbeans, IntelliJ IDEA, etc..)

In de applicatie bevind zich een library 'jssc' (zie folder 'lib'), deze dien je eerst als library toe te voegen. Hoe je 
dit doet is afhankelijk van je IDE. Meestal is het iets als rechter muisknop en dan kiezen voor 'Add as Library...'


Als dit allemaal gelukt is moet je zorgen dat de applicatie weet met welk apparaat die verbinding moet maken.
Dit doe je door in 'SerialPortControl.java' de constant 'PORT' aan te passen.
 
Mac: /dev/tty.usbmodemXXXX
Windows: COMX

waarbij je X moet vervangen door het getal wat bij je apparaat staat.

Windows:
 - Bij apparaatbeheer, eigenschappen staat welk COM nummer, bijvoorbeeld COM4
Mac:
 - Via de terminal: ls /dev/ [TAB]   zie je welke apparaten allemaal zijn aangesloten, zoek naar iets van usbmodemXXXX en neem dat nummer over, bijvoorbeeld /dev/tty.usbmodem1411
 
Dus dan wordt het bijvoorbeeld: <br />
final String PORT = "COM4";

