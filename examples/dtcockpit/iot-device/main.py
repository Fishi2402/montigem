# (c) https://github.com/MontiCore/monticore

# Run sudo pigpiod before running the script
import os
import uuid

# Uses code from https://pypi.org/project/piqmp6988/
# Uses code from https://github.com/ControlEverythingCommunity/SHT30/blob/master/Python/SHT30.py
# Uses code from https://www.emqx.com/en/blog/how-to-use-mqtt-in-python

import smbus
import time
import piqmp6988 as QMP6988
from paho.mqtt import client as mqtt_client
from paho import mqtt
import json
from gpiozero import MotionSensor

TOPIC_PREFIX = "/room/roomNumber/43XX"

BROKER = 'dt.demos.se.rwth-aachen.de'
PORT = 1883
#USERNAME = 'admin'
#PASSWORD = 'Passwort1'

FIRST_RECONNECT_DELAY = 1
RECONNECT_RATE = 2
MAX_RECONNECT_COUNT = 12
MAX_RECONNECT_DELAY = 60

config = {
    'temperature' : QMP6988.Oversampling.X4.value,
    'pressure' :    QMP6988.Oversampling.X32.value,
    'filter' :      QMP6988.Filter.COEFFECT_32.value,
    'mode' :        QMP6988.Powermode.NORMAL.value
}


def read_or_generate_uuid():
    """
    This function checks if the file 'uuid.txt' exists. If it does not exist,
    the function generates a new UUID and writes it to a newly created 'uuid.txt'.
    If the file does exist, the function reads the UUID from the file.

    Returns:
        str: The UUID read from the file or the newly generated UUID.
    """
    filename = 'uuid.txt'

    # Check if file exists
    if not os.path.isfile(filename):
        # If not, generate a UUID and write it to the file
        with open(filename, 'w') as file:
            generated_uuid = str(uuid.uuid4())
            file.write(generated_uuid)
            return generated_uuid
    else:
        # If file exists, read the UUID from the file
        with open(filename, 'r') as file:
            return file.read()

def connect_mqtt():
    """
    Establishes an MQTT client connection to broker specified above. Handles connection and reconnection logic including
    setting up TLS for secure connections, authenticating with a username and password if provided, and retrying
    connection on disconnect with exponential backoff up to a maximum number of attempts.
    """
    def on_connect(client, userdata, flags, rc):
        if rc == 0:
            print("Connected to MQTT Broker!")
        else:
            print("Failed to connect, return code %d\n", rc)

    def on_disconnect(client, userdata, rc):
        print("Disconnected with result code: %s", rc)
        reconnect_count, reconnect_delay = 0, FIRST_RECONNECT_DELAY
        while reconnect_count < MAX_RECONNECT_COUNT:
            print("Reconnecting in %d seconds...", reconnect_delay)
            time.sleep(reconnect_delay)

            try:
                client.reconnect()
                print("Reconnected successfully!")
                return
            except Exception as err:
                print("%s. Reconnect failed. Retrying...", err)

            reconnect_delay *= RECONNECT_RATE
            reconnect_delay = min(reconnect_delay, MAX_RECONNECT_DELAY)
            reconnect_count += 1
        print("Reconnect failed after %s attempts. Exiting...", reconnect_count)

    # Set Connecting Client ID
    client = mqtt_client.Client(f'raspi-{read_or_generate_uuid()}')
    if PORT == 8883:
        client.tls_set(tls_version=mqtt.client.ssl.PROTOCOL_TLS)
    if 'USERNAME' in globals():
        client.username_pw_set(USERNAME, PASSWORD)
    client.on_connect = on_connect
    client.on_disconnect = on_disconnect

    client.connect(BROKER, PORT)
    return client

def format_mqtt_message_for_umlp(value):
    return json.dumps({'uuid': read_or_generate_uuid(), 'value': value}, sort_keys=True)

def read_sht30():
    # Get I2C bus
    bus = smbus.SMBus(1)

    # SHT30 address, 0x44(68)
    # Send measurement command, 0x2C(44)
    #		0x06(06)	High repeatability measurement
    bus.write_i2c_block_data(0x44, 0x2C, [0x06])
    time.sleep(0.5)
    # SHT30 address, 0x44(68)
    # Read data back from 0x00(00), 6 bytes
    # cTemp MSB, cTemp LSB, cTemp CRC, Humididty MSB, Humidity LSB, Humidity CRC
    data = bus.read_i2c_block_data(0x44, 0x00, 6)
    # Convert the data
    cTemp = ((((data[0] * 256.0) + data[1]) * 175) / 65535.0) - 45
    fTemp = cTemp * 1.8 + 32
    humidity = 100 * (data[3] * 256 + data[4]) / 65535.0

    return cTemp, fTemp, humidity

if __name__ == '__main__':
    qmp = QMP6988.PiQmp6988(config)
    mqtt_client = connect_mqtt()
    pir = MotionSensor(5)

    try:
        while True:
            # Read PIR sensor
            motion = pir.value
            print("Motion: %r" % motion)
            mqtt_client.publish(TOPIC_PREFIX + "/motionDetected", format_mqtt_message_for_umlp(motion))

            # Read out SHT30
            cTemp, fTemp, humidity = read_sht30()
            print("Relative Humidity : %.2f %%RH" % humidity)
            print("Temperature in Celsius : %.2f C" % cTemp)
            print("Temperature in Fahrenheit : %.2f F" % fTemp)
            mqtt_client.publish(TOPIC_PREFIX + "/temperature", format_mqtt_message_for_umlp(cTemp))
            mqtt_client.publish(TOPIC_PREFIX + "/humidity", format_mqtt_message_for_umlp(humidity))

            # Read out QMP6988
            value = qmp.read()
            if (isinstance(value['pressure'], float)):
                pressure = round(value['pressure'], 2)
                print("Atmospheric pressure:  %8.2f hPa" % pressure)
                mqtt_client.publish(TOPIC_PREFIX + "/atmosphericPressure", format_mqtt_message_for_umlp(pressure))

            time.sleep(1)
    except KeyboardInterrupt:
        pass
