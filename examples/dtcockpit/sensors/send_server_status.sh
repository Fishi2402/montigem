#!/bin/bash

get_gb_usage() {
    df -BG --output=used,size "$1" | tail -n 1 | tr -d a-zA-z | awk '{print $1, $2}'
}

get_ram_info() {
    free -m --total | grep "Total" | awk '{print $2, $3}'
}

get_processor_usage() {
    vmstat 1 2|tail -1|awk '{print ((100 - $15)/100)}'
}

time=$(date -u +"%Y-%m-%dT%H:%M:%SZ")
disk_mount_point="/"
read -r diskUsage maxDisk <<< "$(get_gb_usage "$disk_mount_point")"

read -r maxRam ramUsage <<< "$(get_ram_info)"
ramUsage=$((ramUsage))  # Convert to integer
maxRam=$((maxRam))

processorUsage=$(get_processor_usage)

json_string=$(cat <<EOF
{
  "time": "$time",
  "diskUsage": $diskUsage,
  "maxDisk": $maxDisk,
  "ramUsage": $ramUsage,
  "maxRam": $maxRam,
  "processorUsage": $processorUsage,
  "hostname": "$(hostname)"
}
EOF
)

echo "$json_string"

url="https://dt.demos.se.rwth-aachen.de/umlp/api/server_status/"
echo "Sending to $url"
curl -X POST -H "Content-Type: application/json" -d "$json_string" "$url"
