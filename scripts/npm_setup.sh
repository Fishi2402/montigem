#!/bin/bash

# Check if the number of parameters is less than 2 or if the first two parameters are empty
if [ $# -lt 2 ] || [ -z "$1" ] || [ -z "$2" ]; then
    echo "Usage: $0 <user> <pass> [folder]"
    exit 1
fi

# Check if the third parameter is provided, otherwise set it to the home directory
folder=${3:-$HOME}

# Create the output file path
output_file="$folder/.npmrc"

# Extract the first and second parameters
user=$1
pass=$2

concatenated_params="$user:$pass"

encoded_params=$(echo -n "$concatenated_params" | base64)

# Write the parameters to the output file
echo "@monticore:registry=https://nexus.se.rwth-aachen.de/repository/montigem-frontendrte-snapshots/" > "$output_file"
echo "@umlp:registry=https://nexus.se.rwth-aachen.de/repository/montigem-frontendrte-snapshots" >> "$output_file"
echo "_auth=\"$encoded_params\"" >> "$output_file"

# Print the location of the output file
echo "Output file created at: $output_file"

