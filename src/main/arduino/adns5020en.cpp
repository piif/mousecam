#include <Arduino.h>
#include "adns5020en.h"

// TODO : pass pineout as argument, instead of hardcoded defines ?
adns5020en::adns5020en(byte SCLK, byte SDIO, byte NCS, byte NRESET):
		SCLK(SCLK), SDIO(SDIO), NCS(NCS), NRESET(NRESET) {
	pinMode(SCLK, OUTPUT);
	pinMode(NRESET, OUTPUT);
	pinMode(NCS, OUTPUT);
}

// TODO : refaire ça en manipulant les registres en direct
// pour avoir un timing constant et donc connu
// idéalement, le refaire en assembleur en s'inspirant de la lib LEDMatrix ?
byte adns5020en::readRegister(byte address) {
	pinMode(SDIO, OUTPUT);
	digitalWrite(NCS, LOW);
	delayMicroseconds(1);

	for (byte i = 128; i > 0; i >>= 1) {
		digitalWrite(SCLK, LOW);
		digitalWrite(SDIO, (address & i) != 0 ? HIGH : LOW);
		digitalWrite(SCLK, HIGH);
	}

	pinMode(SDIO, INPUT);

	delayMicroseconds(100); // tHOLD = 100us min.

	byte res = 0;
	for (byte i = 128; i > 0; i >>= 1) {
		digitalWrite(SCLK, LOW);
		digitalWrite(SCLK, HIGH);
		if (digitalRead(SDIO) == HIGH)
			res |= i;
	}

	delayMicroseconds(100); // tSWW, tSWR = 100us min.
//	delayMicroseconds(1);
	digitalWrite(NCS, HIGH);

	return res;
}

void adns5020en::writeRegister(byte address, byte data) {
	digitalWrite(NCS, LOW);
	delayMicroseconds(1);

	address |= 0x80; // MSB indicates write mode.
	pinMode(SDIO, OUTPUT);

	for (byte i = 128; i > 0; i >>= 1) {
		digitalWrite(SCLK, LOW);
		digitalWrite(SDIO, (address & i) != 0 ? HIGH : LOW);
		digitalWrite(SCLK, HIGH);
	}

	for (byte i = 128; i > 0; i >>= 1) {
		digitalWrite(SCLK, LOW);
		digitalWrite(SDIO, (data & i) != 0 ? HIGH : LOW);
		digitalWrite(SCLK, HIGH);
	}

	delayMicroseconds(100); // tSWW, tSWR = 100us min.
//	delayMicroseconds(1);
	digitalWrite(NCS, HIGH);
}

void adns5020en::reset() {
//	pinMode(SCLK, OUTPUT);
//	pinMode(SDIO, INPUT);
//	digitalWrite(SCLK, LOW);
	digitalWrite(NRESET, LOW);
	delayMicroseconds(1);
	digitalWrite(NRESET, HIGH);
}

void adns5020en::getId(byte &productId, byte &revisionId) {
	productId = readRegister(REG_PRODUCT_ID);
	revisionId = readRegister(REG_REVISION_ID);
}

byte adns5020en::getSum() {
	return readRegister(REG_SUM_PIXEL);
}

void adns5020en::getFrame(int dumpWidth, byte *frame) {
	writeRegister(REG_PIXEL_GRAB, 42);

	int count = 0;
	do {
		byte data = readRegister(REG_PIXEL_GRAB);
		if ((data & 0x80) != 0) { // Data is valid
			frame[count++] = data;
		}
	} while (count != dumpWidth);
}

bool adns5020en::getMotion(char &dx, char &dy) {
	if (readRegister(REG_MOTION) & 0x80) {
		dx = readRegister(REG_DELTA_X);
		dy = readRegister(REG_DELTA_Y);
		writeRegister(REG_MOTION, 0);
		if (dx != 0 || dy != 0) {
			return 1;
		}
	}
	return 0;
}
