/*
 * This module contains all LED-related functions.
 * 
 * NB: 74HC595 stuff from CIRC-05
 */
 
#include <Adafruit_WS2801.h>

// LED leads connected to PWM pins for CC3000 Wifi: 3,4,5,9,10,11,12,13
//Pin Definitions
//The 74HC595 uses a serial communication 
//link which has three pins
int DATA_74HC595_PIN = 6;   // original: 6
int CLOCK_74HC595_PIN = 7;  // original: 7
int LATCH_74HC595_PIN = 8;  // original: 8

//Used for single LED manipulation
int ledState = 0;
const int ON = HIGH;
const int OFF = LOW;
                        

void configureRGBOutputPins(void){
//  Serial << "\n NO-OP: 'configureRGBOutputPins' \n";
  pinMode(DATA_74HC595_PIN, OUTPUT);
  pinMode(CLOCK_74HC595_PIN, OUTPUT);  
  pinMode(LATCH_74HC595_PIN, OUTPUT);  
  for (int led = 0; led < 8; led++){
    Serial << "\nInitialize LED " << led ;
      changeLED(led,0) ; //0 = OFF
  }
}


// DEPRECATED
void setColorRGB (int r, int g, int b){
  Serial << "\n NO-OP: 'setColorRGB' \n";
}


/*
 * updateLEDs() - sends the LED states set in ledStates to the 74HC595
 * sequence
 */
void updateLEDs(int value){
  digitalWrite(LATCH_74HC595_PIN, LOW);     //Pulls the chips latch low
  shiftOut(DATA_74HC595_PIN, CLOCK_74HC595_PIN, MSBFIRST, value); //Shifts out the 8 bits to the shift register
  digitalWrite(LATCH_74HC595_PIN, HIGH);   //Pulls the latch high displaying the data
}

//These are used in the bitwise math that we use to change individual LEDs
//For more details http://en.wikipedia.org/wiki/Bitwise_operation
int bits[] = {B00000001, B00000010, B00000100, B00001000, B00010000, B00100000, B01000000, B10000000};
int masks[] = {B11111110, B11111101, B11111011, B11110111, B11101111, B11011111, B10111111, B01111111};
/*
 * changeLED(int led, int state) - changes an individual LED 
 * LEDs are 0 to 7 and state is either 0 - OFF or 1 - ON
 */
 void changeLED(int led, int state){
   ledState = ledState & masks[led];  //clears ledState of the bit we are addressing
   if(state == ON){ledState = ledState | bits[led];} //if the bit is on we will add it to ledState
   updateLEDs(ledState);              //send the new LED state to the shift register
 }
