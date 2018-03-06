
void setup() {
  Serial.begin(115200);
}

void loop()
{
  byte packet[8];
  delay(1000);

  packet[0] = 12; //start byte
  packet[1] = 13; //start byte
  
  packet[2] = 0;  // Id
  packet[3] = 1;  // lat 
  packet[4] = 2;  // lon
  packet[5] = 3;
  packet[6] = 12; // stop byte
  packet[7] = 22; // stop byte
  Serial.write(packet, 8);

}





