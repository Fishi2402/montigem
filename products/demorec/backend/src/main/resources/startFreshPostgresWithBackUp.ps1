# Prompt the user for the path to the SQL dump file
$dumpFilePath = Read-Host -Prompt "Enter the path to your SQL dump file"

# Define the container name
$containerName = "test_db"

# Check if a container with the specified name already exists and remove it if it does
$existingContainer = docker ps -a -q -f "name=$containerName"
if ($existingContainer) {
    docker rm -f ${containerName}
	Echo "Removed Container"
}

# Start the PostgreSQL Docker container
docker run --name ${containerName} -e POSTGRES_DB=umlpdb -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=newpass -d -p 5432:5432 postgres:15-alpine

Echo "Started Container; Now Waiting"

# Wait for a few seconds to ensure the container is fully up and running
Start-Sleep -Seconds 10

Echo "Waiting finished"

# Copy the SQL dump file into the container
docker cp ${dumpFilePath} ${containerName}:/dumpfile.sql

Echo "Copied Dump"

# Execute the SQL dump file inside the container
docker exec -i ${containerName} psql -U admin -d umlpdb -f /dumpfile.sql

Echo "Executed Dump; Starting interactive Terminal"

# Go into docker container for tests
docker exec -it test_db bash