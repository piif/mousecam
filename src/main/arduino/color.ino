#ifdef PIF_TOOL_CHAIN
	#include <Arduino.h>
	// other includes with full pathes
	// example : #include "led7/led7.h"
#else
	// other includes with short pathes
	// example : #include "led7.h"
#endif

#ifndef DEFAULT_BAUDRATE
	#define DEFAULT_BAUDRATE 115200
#endif

#include "adns5020en.h"

// RGB Led pineout
#define RED 9
#define GREEN 10
#define BLUE 11

#define READ_DELAY 10

//adns5020en captor(2, 3, 4, 5);  // SCLK, SDIO, NCS, NRESET

void setup() {
	Serial.begin(DEFAULT_BAUDRATE);

	pinMode(RED, OUTPUT);
	pinMode(GREEN, OUTPUT);
	pinMode(BLUE, OUTPUT);

	pinMode(A5, INPUT_PULLUP);
	pinMode(2, INPUT);

//	captor.reset();
//	byte productId;
//	byte revisionId;
//	captor.getId(productId, revisionId);
//
//	Serial.print("* Found productId ");
//	Serial.print(productId, HEX);
//	Serial.print(", rev. ");
//	Serial.print(revisionId, HEX);
//	Serial.println(productId == 0x02 ? " OK." : " Unknown productID. Carry on.");
}

const char *colors[] = {
	"black",
	"red",
	"green",
	"yellow",
	"blue",
	"magenta",
	"cyan",
	"white"
};

byte color = 0;

int count;

void loop() {
//	digitalWrite(RED, 1);
//	delay(READ_DELAY);
//	byte r = captor.getSum();
//	digitalWrite(RED, 0);
//	digitalWrite(GREEN, 1);
//	delay(READ_DELAY);
//	byte g = captor.getSum();
//	digitalWrite(GREEN, 0);
//	digitalWrite(BLUE, 1);
//	delay(READ_DELAY);
//	byte b = captor.getSum();
//	digitalWrite(BLUE, 0);
//
//	byte newColor =
//			((r > 0x80) ? 1 : 0) |
//			((g > 0x80) ? 2 : 0) |
//			((b > 0x80) ? 4 : 0);
//	if (color != newColor) {
//		color = newColor;
//		Serial.println(colors[color]);
//	}
//
//	if(count++ % 10 == 0) {
	Serial.print(digitalRead(2));
	Serial.print(" ");
	Serial.println(analogRead(A5));
		delay(500);
//	}
}
