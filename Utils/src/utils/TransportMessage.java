package utils;

public class TransportMessage extends Message{

    int totalAmount;
    int count;
    int piece;
    int sizeLastPart;
    byte[] bytes;
    String fileName;

    public TransportMessage(Command command, String login, String pass,
                            byte[] bytes, int totalAmount, int count,
                            int piece, int sizeLastPart, String fileName) {
        super(command, login, pass);
        this.bytes = bytes;
        this.totalAmount = totalAmount;
        this.count = count;
        this.piece = piece;
        this.sizeLastPart = sizeLastPart;
        this.fileName = fileName;
    }

    public TransportMessage(Command command, byte[] bytes, int totalAmount, int count,
                            int piece, int sizeLastPart, String fileName) {
        super(command);
        this.bytes = bytes;
        this.totalAmount = totalAmount;
        this.count = count;
        this.piece = piece;
        this.sizeLastPart = sizeLastPart;
        this.fileName = fileName;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public int getCount() {
        return count;
    }

    public int getPiece() {
        return piece;
    }

    public int getSizeLastPart() {
        return sizeLastPart;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public String getFileName() {
        return fileName;
    }
}
