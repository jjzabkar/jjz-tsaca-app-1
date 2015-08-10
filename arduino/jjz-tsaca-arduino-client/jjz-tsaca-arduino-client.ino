#include <MemoryFree.h>

/*
 * This module contains setup() and loop().
 * 
 */

const unsigned long PROGMEM DEFAULT_LOOP_TIME_MILLIS = 29500;
const unsigned long PROGMEM  MIN_LOOP_TIME_MILLIS = 5000;
unsigned long loopCount = 0;

//per: http://playground.arduino.cc/Main/StreamingOutput
template<class T> inline Print &operator <<(Print &obj, T arg) { obj.print(arg); return obj; }


int freeRam () {
  extern int __heap_start, *__brkval; 
  int v; 
  return (int) &v - (__brkval == 0 ? (int) &__heap_start : (int) __brkval); 
}

void printFreeMemory(void){
  if(Serial){
    Serial.print(F("\nfreeMemory="));
    Serial.print(freeMemory());
//    Serial.print(F(", availableMemory="));
//    Serial.print(availableMemory());
    Serial.print(F(", freeRam="));
    Serial.println(freeRam());
  }
}


void setup(void)
{
  unsigned long startLoopMillis = millis();
  Serial.begin(115200);
  // printFreeMemory();
  if(Serial) Serial.println(F("Setting up LEDs"));
  ledSetup();
  // BEGIN facilitate LED parse testing
//  processHttpContentString("STATION,000000,000000,000000,000000,000000,ff00ff,0000ff,00ff00,ff0000,000000,000000,000000,000000,ff00ff,0000ff,000000,000000,ff00ff,0000ff,daca00,ff0000,000000,000000,000000,000000,");
//  delay(600000);
  // END facilitate LED parse testing
    
  setOnePixel(0, 255, 0, 0 );  //red
  // printFreeMemory();
  initializeWifi();
  setOnePixel(0, 200, 170, 0 ); //yellow3
  setStaticIpAddress();
  setOnePixel(0, 255, 0, 255 ); //purple
  connectToWifiNetwork();
//  setOnePixel(0, 0, 255, 0 ); //green
  unsigned long elapsedLoopMillis = millis() - startLoopMillis;
  if(Serial) Serial << F("\n*** setup() took ") << elapsedLoopMillis << F("ms ****\r\n"); 
}


unsigned long elapsedLoopMillis = -1;
unsigned long startLoopMillis = -1;
unsigned long sleepTime = -1;
void loop(void){  
  loopCount++;
  printFreeMemory();
  startLoopMillis = millis();
  setOnePixel(0, 200, 170, 0 ); //yellow3; reset to green within connectToWebSite()
  checkConnectedToWifiNetwork();
  setOnePixel(0, 0, 255, 0 ); //green
  doWebClientTest(loopCount, elapsedLoopMillis);
  //disconnectFromWifiNetwork();
  elapsedLoopMillis = millis() - startLoopMillis;
  //sleepTime = min(max(MIN_LOOP_TIME_MILLIS, DEFAULT_LOOP_TIME_MILLIS - elapsedLoopMillis), DEFAULT_LOOP_TIME_MILLIS);
  if(Serial) Serial << F("\n*** loop(") << loopCount << F(") took ") << elapsedLoopMillis << F("ms.  Sleep for ") << sleepTime <<  F("ms  ****\n"); 
//  delay(sleepTime);
}


