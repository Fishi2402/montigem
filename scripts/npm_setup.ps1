param (
    [Parameter(Mandatory=$true, Position=0)]
    [string]$user,

    [Parameter(Mandatory=$true, Position=1)]
    [string]$pass,

    [Parameter(Position=2)]
    [string]$folder = $HOME
)

# Check if the number of parameters is less than 2 or if the first two parameters are empty
if (($user -eq '') -and ($pass -eq '') -or ($PSBoundParameters.Count -lt 2)) {
    Write-Host "Usage: .\create_output.ps1 <user> <pass> [folder]"
    exit
}

# Create the output file path
$output_file = Join-Path $folder ".npmrc"

# Concatenate the parameters with a colon separator
$concatenated_params = "${user}:${pass}"

# Encode the concatenated parameters using base64 encoding
$encoded_params = [System.Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes($concatenated_params))

# Write the parameters to the output file
"@monticore:registry=https://nexus.se.rwth-aachen.de/repository/montigem-frontendrte-snapshots/" | Out-File -FilePath $output_file -Encoding UTF8
"@umlp:registry=https://nexus.se.rwth-aachen.de/repository/montigem-frontendrte-snapshots" | Out-File -FilePath $output_file -Append -Encoding UTF8
"_auth=`"$encoded_params`"" | Out-File -FilePath $output_file -Append -Encoding UTF8

# Print the location of the output file
Write-Host "Output file created at: $output_file"

