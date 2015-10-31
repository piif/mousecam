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

adns5020en captor(2, 3, 4, 5);  // SCLK, SDIO, NCS, NRESET

int dumpWidth = 0; // Number of pixels to read for each frame.
byte frame[MAP_SIZE];

void dumpFrame() {
	captor.getFrame(dumpWidth, frame);

	Serial.print("FRAME:");
	for (int i = 0; i < dumpWidth; i++) {
		byte pix = frame[i];
		if (pix < 0x10)
			Serial.print("0");
		Serial.print(pix, HEX);
	}
	Serial.println();

	Serial.print("SUM:");
	Serial.println(captor.getSum(), HEX);

}

byte c2x(char c) {
	if (c >= '0' && c <= '9') {
		return c - '0';
	} else if (c >= 'a' && c <= 'f') {
		return c - 'a' + 10;
	}
	return 0;
}

void setup() {
	Serial.begin(DEFAULT_BAUDRATE);

	pinMode(RED, OUTPUT);
	pinMode(GREEN, OUTPUT);
	pinMode(BLUE, OUTPUT);

	captor.reset();
	byte productId;
	byte revisionId;
	captor.getId(productId, revisionId);

	Serial.print("* Found productId ");
	Serial.print(productId, HEX);
	Serial.print(", rev. ");
	Serial.print(revisionId, HEX);
	Serial.println(productId == 0x02 ? " OK." : " Unknown productID. Carry on.");
}

void loop() {
	// Allows to set the dump window by sending the number of lines to read via the serial port.
	if (Serial.available() > 0) {
		int c = Serial.read();
		if (c == 'W') {
			int c = Serial.read();
			dumpWidth = 15 * c2x(c);
			Serial.print("* Width set to ");
			Serial.println(dumpWidth);
		} else if (c == 'L') {
			char rgb[6];
			Serial.readBytes(rgb, 6);
			byte r = c2x(rgb[0]) * 16 + c2x(rgb[1]);
			byte g = c2x(rgb[2]) * 16 + c2x(rgb[3]);
			byte b = c2x(rgb[4]) * 16 + c2x(rgb[5]);
			analogWrite(RED, r);
			analogWrite(GREEN, g);
			analogWrite(BLUE, b);
			Serial.print("* Led set to ");
			Serial.print(r, HEX);
			Serial.print(", ");
			Serial.print(g, HEX);
			Serial.print(", ");
			Serial.println(b, HEX);
		}
		// else, silently ignore
	}

	if (dumpWidth > 0) {
		dumpFrame();
	}

	char dx, dy;
	if(captor.getMotion(dx, dy)) {
		if (dx != 0 || dy != 0) {
			Serial.print("DELTA:");
			Serial.print(dx, DEC);
			Serial.print(" ");
			Serial.println(dy, DEC);
		}
	}
}
