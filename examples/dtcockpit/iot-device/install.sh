#!/bin/bash
# (c) https://github.com/MontiCore/monticore

set -e

sudo date -s "$(wget -qSO- --max-redirect=0 google.com 2>&1 | grep Date: | cut -d' ' -f5-8)Z"
sudo apt-get update

# CHANGE IP ADDRESS BEFORE RUNNING!
sudo nmcli con mod "Wired connection 1" ipv4.addresses "137.226.168.XXX/26" ipv4.gateway "137.226.168.193" ipv4.dns "134.130.4.1,134.130.5.1" ipv4.method "manual"

sudo apt-get install -y i2c-tools
sudo apt install -y python3-pip
sudo apt install --upgrade -y python3-setuptools
sudo apt-get install -y python3-smbus
sudo apt install -y python3-gpiozero

sudo pip install --break-system-packages piqmp6988
sudo pip install --break-system-packages paho-mqtt

sudo raspi-config nonint do_i2c 0

sudo mkdir -p /var/log/sensepi/
sudo bash -c "echo \"[Unit]
Description=IoT Sensor
After=mosquitto.service pigpiod.service
Requires=pigpiod.service
StartLimitIntervalSec=0
[Service]
ExecStart=/bin/bash /home/raspberry/start.sh
Type=simple
StandardOutput=file:/var/log/sensepi/sensor_standard.log
StandardError=file:/var/log/sensepi/sensor_error.log
Restart=always
RestartSec=1
[Install]
WantedBy=multi-user.target
\" > /etc/systemd/system/sensepi.service"

sudo systemctl daemon-reload
sudo systemctl enable sensepi.service

sudo reboot
