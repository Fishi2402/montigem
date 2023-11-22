<!-- (c) https://github.com/MontiCore/monticore -->

# Raspberry Pi Hardware
Raspberry Pi 4B with Raspberry Pi Shield - Grove Base HAT. 

Connect an M5Stack ENV III (U001-C) to I2C and an M5Stack motion sensor (U004) to D5. 

Raspberry Pi Shield - Grove Base HAT: https://wiki.seeedstudio.com/Grove_Base_Hat_for_Raspberry_Pi/

M5Stack ENV III (U001-C): https://shop.m5stack.com/products/env-iii-unit-with-temperature-humidity-air-pressure-sensor-sht30-qmp6988

M5Stack motion sensor: https://shop.m5stack.com/products/pir-module?variant=16804756553818

# Raspberry Pi Installation
We used Raspberry Pi OS Lite (32-bit), released 2023-10-10.

Run the `install.sh` on the freshly installed OS.
Call `./start.sh` to manually start reading sensor output, it will also be called automatically when the device starts.
Disable the script with `sudo systemctl disable sensorpi`
