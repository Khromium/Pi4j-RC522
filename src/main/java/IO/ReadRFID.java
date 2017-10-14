package IO;
/**
 * Created by Liang on 2016/3/7.
 */

import com.pi4j.wiringpi.Spi;
import rc522.Convert;
import rc522.RaspRC522;

public class ReadRFID extends Thread {
    public static void main(String[] args) throws InterruptedException {
        ReadRFID readRFID = new ReadRFID();
        readRFID.start();

    }

    @Override
    public void run() {
        RaspRC522 rc522 = new RaspRC522();
        int back_bits[] = new int[1];
        String strUID;
        byte tagid[] = new byte[5];
        int i, status;
        byte blockaddress = 8;  //读写块地址0-63
        byte sector = 15, block = 2;


        while (true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            tagid = new byte[5];
            status = rc522.Select_MirareOne(tagid);
            System.out.println(status);

            strUID = Convert.bytesToHex(tagid);
            System.out.println("Card Read UID:" + strUID.substring(0, 2) + "," +
                    strUID.substring(2, 4) + "," +
                    strUID.substring(4, 6) + "," +
                    strUID.substring(6, 8));

            //default key
            byte[] keyA = new byte[]{(byte) 0x03, (byte) 0x03, (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03};
            byte[] keyB = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};

            byte data[] = new byte[16];
//            status = rc522.Auth_Card(RaspRC522.PICC_AUTHENT1A, sector, block, keyA, tagid);
//            if (status != RaspRC522.MI_OK) {
//                System.out.println("Authenticate A error");
////                continue;
//            } else {
//
//                status = rc522.Read(sector, block, data);
//                System.out.println("Successfully authenticated,Read data=" + Convert.bytesToHex(data));
//                status = rc522.Read(sector, (byte) 3, data);
//                System.out.println("Read control block data=" + Convert.bytesToHex(data));
//
//            }
            status = rc522.Auth_Card(RaspRC522.PICC_AUTHENT1B, sector, block, keyB, tagid);
            if (status != RaspRC522.MI_OK) {
                System.out.println("Authenticate B error");
                continue;
//                return;
            } else {

                status = rc522.Read(sector, block, data);
                System.out.println("Successfully authenticated,Read data=" + Convert.bytesToHex(data));
                status = rc522.Read(sector, (byte) 3, data);
                System.out.println("Read control block data=" + Convert.bytesToHex(data));

            }
            rc522.Stop_Crypto();


        }

    }

    public static void rfidReaderLoop(int sleeptime) throws InterruptedException {
        int count = 0;
        while (count++ < 3) {

            int packetlength = 5;

            byte packet[] = new byte[packetlength];
            packet[0] = (byte) 0x80; // FIRST PACKET GETS IGNORED BUT HAS
            // TO BE SET TO READ
            packet[1] = (byte) 0x80; // ADDRESS 0 Gives data of Address 0
            packet[2] = (byte) 0x82; // ADDRESS 1 Gives data of Address 1
            packet[3] = (byte) 0x84; // ADDRESS 2 Gives data of Address 2
            packet[4] = (byte) 0x86; // ADDRESS 3 Gives data of Address 3

            System.out.println("-----------------------------------------------");
            System.out.println("Data to be transmitted:");
            System.out.println("[TX] " + bytesToHex(packet));
            System.out.println("[TX1] " + packet[1]);
            System.out.println("[TX2] " + packet[2]);
            System.out.println("[TX3] " + packet[3]);
            System.out.println("[TX4] " + packet[4]);
            System.out.println("Transmitting data...");

            // Send data to Reader and receive answerpacket.
            packet = readFromRFID(0, packet, packetlength);

            System.out.println("Data transmitted, packets received.");
            System.out.println("Received Packets (First packet to be ignored!)");
            System.out.println("[RX] " + bytesToHex(packet));
            System.out.println("[RX1] " + packet[1]);
            System.out.println("[RX2] " + packet[2]);
            System.out.println("[RX3] " + packet[3]);
            System.out.println("[RX4] " + packet[4]);
            System.out.println("-----------------------------------------------");

            if (packet.length == 0) {
                //Reset when no packet received
                //ResetPin.high();
                Thread.sleep(50);
                //ResetPin.low();
            }

            // Wait 1/2 second before trying to read again
            Thread.sleep(sleeptime);
        }

    }

    public static byte[] readFromRFID(int channel, byte[] packet, int length) {
        Spi.wiringPiSPIDataRW(channel, packet, length);

        return packet;
    }

    public static boolean writeToRFID(int channel, byte fullAddress, byte data) {

        byte[] packet = new byte[2];
        packet[0] = fullAddress;
        packet[1] = data;

        if (Spi.wiringPiSPIDataRW(channel, packet, 1) >= 0)
            return true;
        else
            return false;
    }

    public static String bytesToHex(byte[] bytes) {
        final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
