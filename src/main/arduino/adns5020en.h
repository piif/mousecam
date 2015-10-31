// some stuff about ADNS-5020-EN component
// see http://www.wessnitzer.net/academic/documents/ADNS5020EN.pdf


// on ADNS5020EN
// 1    SDIO      Serial Port Data Input and Output
// 2    XY_LED    LED Control
// 3    NRESET    Reset Pin (active low input)
// 4    NCS       Chip Select (active low input)
// 5    VDD5      Supply Voltage
// 6    GND       Ground
// 7    REGO      Regulator Output
// 8    SCLK      Serial Clock Input

// register addresses
#define REG_PRODUCT_ID 0x00
#define REG_REVISION_ID 0x01
#define REG_MOTION 0x02
#define REG_DELTA_X 0x03
#define REG_DELTA_Y 0x04
#define REG_SQUAL 0x05
#define REG_SHUTTER_LOWER 0x06
#define REG_SHUTTER_UPPER 0x07
#define REG_MAXIMUM_PIXEL 0x08
#define REG_SUM_PIXEL 0x09
#define REG_MINIMUM_PIXEL 0x0A
#define REG_PIXEL_GRAB 0x0B
// 0C reserved
#define REG_MOUSE_CONTROL 0x0D
// 0E-39 reserved
#define REG_CHIP_RESET 0x3A
// 3B-3E reserved
#define REG_INV_REV_ID 0x3F
// 40-62 reserved
#define REG_MOTION_BURST 0x63

#define MAP_SIZE (15*15)

class adns5020en {
	public:
		adns5020en(byte SCLK, byte SDIO, byte NCS, byte NRESET);
		byte readRegister(byte address);
		void writeRegister(byte address, byte data);
		void reset();
		void getId(byte &productId, byte &revisionId);
		byte getSum();
		void getFrame(int dumpWidth, byte *frame);
		bool getMotion(char &dx, char &dy);
	private:
		byte SCLK;
		byte SDIO;
		byte NCS;
		byte NRESET;
};
