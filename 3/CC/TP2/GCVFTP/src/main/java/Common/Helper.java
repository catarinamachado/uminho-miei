package Common;

import Model.Packet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Helper {
    public static List<String> decryptAndPut(String path, Packet packet) {
        List<String> files = new ArrayList<>();

        packet.gettFiles().forEach(f -> {
            put(path, f.getFilename(), f.getFileContents());
            files.add(f.getFilename());
        });

        return files;
    }

    public static void put(String path, String filename, String contents) {
        File file = new File(path + filename);
        try {
            Files.write(file.toPath(), contents.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<byte[]> divideArray(byte[] source, int chunksize) {
        List<byte[]> result = new ArrayList<>();
        int start = 0;
        while (start < source.length) {
            int end = Math.min(source.length, start + chunksize);
            result.add(Arrays.copyOfRange(source, start, end));
            start += chunksize;
        }

        return result;
    }
}
