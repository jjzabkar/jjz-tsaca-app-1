/*
 * This module contains all LED-related functions.
 * 
 */
 
#include <Adafruit_WS2801.h>

// LED leads connected to PWM pins for CC3000 Wifi: 3,4,5,9,10,11,12,13
const int RED_LED_PIN = 6;  
const int GREEN_LED_PIN = 7; 
const int BLUE_LED_PIN = 8; 

// Current intensity level of the individual LEDs
int redIntensity = 0;
int greenIntensity = 0;
int blueIntensity = 0;

// Length of time we spend showing each color
const int DISPLAY_TIME = 60; // In milliseconds

/*
void doLedLoop(){
  Serial << "\ndoLedLoop()";
  // Cycle color from red through to green
  // (In this loop we move from 100% red, 0% green to 0% red, 100% green)
  analogWrite(BLUE_LED_PIN, 255);
  Serial << "\ndoLedLoop(): red/green";
  for (greenIntensity = 0; greenIntensity <= 255; greenIntensity+=5) {
        redIntensity = 255-greenIntensity;
        analogWrite(GREEN_LED_PIN, greenIntensity);
        analogWrite(RED_LED_PIN, redIntensity);
        delay(DISPLAY_TIME);
  }

  // Cycle color from green through to blue
  // (In this loop we move from 100% green, 0% blue to 0% green, 100% blue)  
  Serial << "\ndoLedLoop(): green/blue";
  analogWrite(RED_LED_PIN, 255);
  for (blueIntensity = 0; blueIntensity <= 255; blueIntensity+=5) {
        greenIntensity = 255-blueIntensity;
        analogWrite(BLUE_LED_PIN, blueIntensity);
        analogWrite(GREEN_LED_PIN, greenIntensity);
        delay(DISPLAY_TIME);
  }

  // Cycle cycle from blue through to red
  // (In this loop we move from 100% blue, 0% red to 0% blue, 100% red)    
  analogWrite(GREEN_LED_PIN, 255);
  Serial << "\ndoLedLoop(): blue/red";
  for (redIntensity = 0; redIntensity <= 255; redIntensity+=5) {
        blueIntensity = 255-redIntensity;
        analogWrite(RED_LED_PIN, redIntensity);
        analogWrite(BLUE_LED_PIN, blueIntensity);
        delay(DISPLAY_TIME);
  }

}
*/

void configureRGBOutputPins(void){
  Serial << "\n NO-OP: 'configureRGBOutputPins' \n";
//  Serial << "\n  Configure RGB pins ("<< RED_LED_PIN << ","<< GREEN_LED_PIN << ","<< BLUE_LED_PIN << ") as OUTPUT.\n";
//  pinMode(RED_LED_PIN, OUTPUT);
//  pinMode(GREEN_LED_PIN, OUTPUT);
//  pinMode(BLUE_LED_PIN, OUTPUT);
}


void setColorRGB (int r, int g, int b){
  Serial << "\n NO-OP: 'setColorRGB' \n";
//  Serial << "\n  Set LED to RGB (" << r << ","<< g << ","<< b << ")  pins ("<< RED_LED_PIN << ","<< GREEN_LED_PIN << ","<< BLUE_LED_PIN << ")\n";
//  redIntensity= 255 - r;
//  analogWrite(RED_LED_PIN, redIntensity);
//  greenIntensity=255 - g;
//  analogWrite(GREEN_LED_PIN, greenIntensity);
//  blueIntensity=255 - b;
//  analogWrite(BLUE_LED_PIN, blueIntensity);  
//  delay(500);
}

