#include <dht.h>

dht DHT;

#define DHT11_PIN 7

//returns one telemetry package
void getMeasurements(byte packet[])
{
  int chk = DHT.read11(DHT11_PIN);  // Read temp and humidy from DHT11 sensor;
  int temp1 = DHT.temperature;      // For simulation purpose use temp twice.
  int temp2 = DHT.temperature;
  int humd1 = DHT.humidity;
  int humd2 = DHT.humidity;

  //Lets asume we want to send 64 bits

  packet[0] = highByte(temp1);
  packet[1] = lowByte(temp1);

  packet[2] = highByte(temp2);
  packet[3] = lowByte(temp2);

  packet[4] = highByte(humd1);
  packet[5] = lowByte(humd1);

  packet[6] = highByte(humd2);
  packet[7] = lowByte(humd2);

  packet[8] = 42;
  packet[9] = 0;
}

double counter = 0.0;

void getMeasurements2(byte packet[]) {
  int chk  = DHT.read11(DHT11_PIN);  // Read temp and humidy from DHT11 sensor;
  int temp = 200*sin(counter); //DHT.temperature;   
  counter += 0.05;
  int humd = 10; //DHT.humidity;
  
  packet[0] = 5; //start byte
  packet[1] = 1; //id

  packet[2] = highByte(temp);
  packet[3] = lowByte(temp);

  packet[4] = highByte(humd);
  packet[5] = lowByte(humd);

  packet[6] = 0;
  packet[7] = 0;
  packet[8] = 0;
  packet[9] = 0;
  packet[10] = 0;
  packet[11] = 0;
  packet[12] = 0;
  packet[13] = 0;
  packet[14] = 0;
}

void setup() {
  Serial.begin(38400);
}

void loop()
{
  byte packet[20];
  getMeasurements2(packet);
  Serial.write(packet, 20);
//    int chk  = DHT.read11(DHT11_PIN);  // Read temp and humidy from DHT11 sensor;
//  Serial.print("Temperature = ");
//  Serial.println(DHT.temperature);
//  Serial.print("Humidity = ");
//  Serial.println(DHT.humidity);
  delay(10);
}




