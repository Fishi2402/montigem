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

## Data Migration with PostgresQL

Prerequisites are that you have Docker installed on your local pc. 
Make sure no other process is running on your Postgres port.

1. Create a SQL dump of the current database state with the following command on the deployed server

```
docker exec -i demorec-db-1 /bin/bash -c "PGPASSWORD={db pwd} pg_dump --username {username} {dbName}" > ../demorec/back_up/{currentDate}backUp.sql
```

2. SSH copy it to your local pc 
```
scp demorec:/../back_up/{currentDate}.sql "{localPath}\DemoRec\{CurrentDate}backUp.sql"
```

3. Run local Postgres Docker container
```
docker run --name test_db -e POSTGRES_DB={dbName} -e POSTGRES_USER={username} -e POSTGRES_PASSWORD=newpass -p 5432:5432 postgres:15-alpine
```

4. Copy SQL dump into your local Postgres Docker container
```
docker cp "{localPath}\DemoRec\{CurrentDate}backUp.sql" test_db:/dump.sql
```

5. Start interactive docker terminal of local Postgres Container
```
docker exec -it test_db bash
```

6. Load SQL Dump into database 
```
psql -U admin -d umlpdb -f /dump.sql
```

7. Use psql to manually edit the data with SQL commands 
```
psql -U admin -d umlpdb
```

8. While editing the SQL DB with your SQL commands create a Update SQL file in a text editor containing each command you executed

9. Adapt `application.properties` to let the backend use the locally running Postgres Database from the backend 

9. Check if everything works by running the application locally. While clicking through the App, check if no errors appear in the backend.

10. After finishing your manual migration, repeat steps 3 to 6.
 
11. Additionally, load the freshly created Update SQL into you docker container and execute it after the dump.
 
12. Repeat step 9

13. SSH copy the Update SQL file to the deployment server
```
scp "{localPath}\DemoRec\240313Update.sql" demorec:/../back_up/{currentDate}.sql
```

14. Run the Update SQL file in your database container as in Step 11

15. Run `docker compose down` and `docker compose up -d` and check again if everything works as expected. If not, use **{CurrentDate}** to restore the state of the Database to the backUp of your changes beforehand  

16. Commit and push your Update SQL file to GIT with the name schema {CurrentDate}Update.sql


⚠️ **<span style="background-color: yellow;">Important Remarks:</span>** <br>
If you happen to insert tables into the SQL dump from your current database, please pay attention to these details:
* Declare tables in the right schema (typically: `public`).
* The limit of characters for table names in PostgreSQL is **63 characters**. Thus, PostgreSQL will cut off everything going beyond that limit.
