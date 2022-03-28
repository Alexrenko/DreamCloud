package utils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Cargo implements Serializable {
    private String fileName;
    private int piece;
    private int totalAmount;
    private int lastPart;
    private ArrayList<byte[]> dataList;


    public Cargo(Path path, int piece) {
        try {

            byte[] data = Files.readAllBytes(path);
            this.piece = piece;
            dataList = divideData(data, piece);
            totalAmount = dataList.size();
            fileName = path.getFileName().toString();
            System.out.println("Имя файла: " + fileName);
            System.out.println("Количество байт в одном куске: " + piece);
            System.out.println("Общее количество кусков: " + totalAmount);
            System.out.println("Байт в последнем куске: " + lastPart);
            System.out.println("Всего байт: " + data.length);
            for (int i = 0; i < totalAmount; i++) {
                System.out.println("ArrayList[" + i + "]: " + dataList.get(i).length );
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<byte[]> divideData(byte[] data, int piece) {
        ArrayList<byte[]> result = new ArrayList<>();
        long fileSize = data.length;
        int fullParts = (int)(fileSize / piece);
        int lastPart = (int)(fileSize - piece * fullParts);
        this.lastPart = lastPart;
        int partsCount = (lastPart == 0) ? fullParts : fullParts + 1;

        ByteBuffer byteBuffer = ByteBuffer.wrap(data);

        for (int i = 0; i < partsCount; i++) {
            //если это последний кусок
            if (i == partsCount - 1) {
                result.add(new byte[lastPart]);
                byteBuffer.get(result.get(i), 0, lastPart);
            } else {
                result.add(new byte[piece]);
                byteBuffer.get(result.get(i), 0, piece);
            }
        }

        return result;
    }

    public String getFileName() {
        return fileName;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public int getLastPart() {
        return lastPart;
    }

    public ArrayList<byte[]> getDataList() {
        return dataList;
    }

    public int getPiece() {
        return piece;
    }
}
