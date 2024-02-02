# DemoRec

DemoRec ist eine Fenix Anwendung die zur Sammlung von Daten eines Batterie-Demontageprozesses dient.
Eine Besonderheit von DemoRec ist es, dass neben den üblichen Daten vor allem 3D Scans und Fotos 
gespeichert werden sollen.

Diese Anwendung wird als Teil des vom BMBF geförderten Batterie-Demontageprojekts entwickelt. 

## Starting
Start the backend with `gradle :products:demorec:backend:run` and the frontend with `gradle :products:demorec:frontend:run`.
Define the Environment Variable `FILE_LOCATION` to specify where images/3D-Scans location. You can visit the Start page on `localhost:4200/gui/Start`

If you get a node problem after adapting GUI models and restarting the FrontEnd, you have to kill the node process that block port 4200.  

## Design of Image and 3D-Scan storage
Es gibt zwei spezielle Rest Endpoints im Backend, die sich um das speichern und laden der Bild/3D-Scan 
Dateien kümmern. Diese sind: `/blob` für die Speicherung und `/images/{imagePath}` für das Laden. 
Die Dateien werden auf dem FileSystem des Servers abgespeichert. 
