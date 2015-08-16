/*
 * This module contains setup() and loop().
 * 
 */
#include <avr/wdt.h>

const unsigned long PROGMEM DEFAULT_LOOP_TIME_MILLIS = 29500;
const unsigned long PROGMEM  MIN_LOOP_TIME_MILLIS = 5000;
unsigned long loopCount = 0;

//per: http://playground.arduino.cc/Main/StreamingOutput
template<class T> inline Print &operator <<(Print &obj, T arg) { obj.print(arg); return obj; }


void setup(void)
{
  wdt_disable(); //disable watchdog just in case
  unsigned long startLoopMillis = millis();
  Serial.begin(115200);
  if(Serial) Serial.println(F("Setting up LEDs"));

  ledSetup();
  // BEGIN facilitate LED parse testing
//  processHttpContentString("STATION,000000,000000,000000,000000,000000,ff00ff,0000ff,00ff00,ff0000,000000,000000,000000,000000,ff00ff,0000ff,000000,000000,ff00ff,0000ff,daca00,ff0000,000000,000000,000000,000000,");
//  delay(600000);
  // END facilitate LED parse testing
    
  setOnePixel(0, 255, 0, 0 );  //red

  initializeWifi();




  
  setOnePixel(0, 200, 170, 0 ); //yellow3
  
//  setOnePixel(0, 255, 0, 255 ); //purple
//  connectToWifiNetwork();
//  setOnePixel(0, 0, 255, 0 ); //green
  unsigned long elapsedLoopMillis = millis() - startLoopMillis;
  if(Serial) Serial << F("\n*** setup() took ") << elapsedLoopMillis << F("ms ****\r\n"); 
}


unsigned long elapsedLoopMillis = -1;
unsigned long startLoopMillis = -1;
unsigned long sleepTime = -1;
void loop(void){  
  loopCount++;
  startLoopMillis = millis();
  
  doWebClientTest(loopCount, elapsedLoopMillis);
  
  elapsedLoopMillis = millis() - startLoopMillis;
  sleepTime = min(max(MIN_LOOP_TIME_MILLIS, DEFAULT_LOOP_TIME_MILLIS - elapsedLoopMillis), DEFAULT_LOOP_TIME_MILLIS);
  if(Serial) Serial << F("\n*** loop(") << loopCount << F(") took ") << elapsedLoopMillis << F("ms.  Sleep for ") << sleepTime <<  F("ms  ****\n"); 
  delay(sleepTime);
}


/**
 * Interrupt routine.  Should use neither delay() nor Serial.  No worky?
 */
//ISR(WDT_vect){
//  // Include your interrupt code here.
//  Serial.println(F("\nINTERRUPTED"));
//}
