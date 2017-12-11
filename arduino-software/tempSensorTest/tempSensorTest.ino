
void setup() {
  Serial.begin(115200);
}

void loop()
{
  byte packet[9];
  delay(100);

  packet[0] = 12; //start byte
  packet[1] = 13; //start byte
  
  packet[2] = 0;  // Id
  packet[3] = 0;  // some value
  packet[4] = 50;  // some value
  packet[5] = 0;  // some value
  packet[6] = 10;  // some value
  packet[7] = 12; // stop byte
  packet[8] = 22; // stop byte
  
  Serial.write(packet, 9);
}





