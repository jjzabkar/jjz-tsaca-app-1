/** 
 *  commented out 2015-08-12T12:00 after observing no memory leaks, always at least 270 bytes RAM free
int freeRam () {
  extern int __heap_start, *__brkval; 
  int v; 
  return (int) &v - (__brkval == 0 ? (int) &__heap_start : (int) __brkval); 
}

void printFreeMemory(void){
  return ;
  if(Serial){
    Serial.print(F("\nfreeMemory="));
    Serial.print(freeMemory());
    Serial.print(F(", freeRam="));
    Serial.println(freeRam());
  }
}
 *
 */

void printFreeMemory(void){
  return ;
}

