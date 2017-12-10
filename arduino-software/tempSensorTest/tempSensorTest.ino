
void setup() {
  Serial.begin(115200);
}

void loop()
{
  byte packet[40];
  delay(10);

  packet[0] = 12; //start byte
  packet[1] = 13; //start byte
  
  packet[2] = 0;  // Id
  packet[3] = 0;  // some value
  packet[4] = 0;  // some value
  packet[5] = 0;  // some value
  packet[6] = 68;  // some value
  packet[7] = 68;  // some value
  packet[8] = 68;  // some value
  packet[9] = 68;  // some value
  packet[10] = 68;  // some value
  packet[12] = 68;  // some value
  packet[13] = 68;  // some value
  packet[14] = 68;  // some value
  packet[15] = 68;  // some value
  packet[16] = 68;  // some value
  packet[17] = 68;  // some value
  packet[18] = 68;  // some value
  packet[19] = 68;  // some value
  packet[20] = 68;  // some value
  packet[21] = 68;  // some value
  packet[22] = 68;  // some value
  packet[23] = 68;  // some value
  packet[24] = 68;  // some value
  packet[25] = 68;  // some value
  packet[26] = 68;  // some value
  packet[27] = 68;  // some value
  packet[28] = 68;  // some value
  packet[29] = 68;  // some value
  packet[30] = 68;  // some value
  packet[31] = 68;  // some value
  packet[32] = 68;  // some value
  packet[33] = 68;  // some value
  packet[34] = 68;  // some value
  packet[35] = 68;  // some value
  packet[36] = 68;  // some value
  packet[37] = 68;  // some value
  packet[38] = 12; // stop byte
  packet[39] = 22; // stop byte
  
  Serial.write(packet, 40);
}





