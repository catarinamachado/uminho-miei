package Model;

import java.io.Serializable;
import java.util.Base64;

public class TFile implements Serializable {
    private String filename;
    private String fileContents;
    private int part = 0;
    private int total = 0;

    public TFile(String filename) {
        this.filename = filename;
        this.fileContents = "";
    }

    public TFile(String filename, byte[] fileContents) {
        this.filename = filename;
        this.fileContents = new String(fileContents);
    }

    public TFile(String filename, int part, int total, String fileContents) {
        this.filename = filename;
        this.part = part;
        this.total = total;
        this.fileContents = fileContents;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFileContents() {
        return fileContents;
    }

    public void setFileContents(String fileContents) {
        this.fileContents = fileContents;
    }

    public int getPart() {
        return part;
    }

    public void setPart(int part) {
        this.part = part;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public static TFile fromString(String s) {
        String[] ss = s.split("'---'");
        return new TFile(
                ss[0], Integer.parseInt(ss[1]), Integer.parseInt(ss[2]),
                new String(Base64.getDecoder().decode(ss.length < 4 ? "" : ss[3])));
    }

    public String toString() {
        return filename + "'---'" + part + "'---'" + total + "'---'" + Base64.getEncoder().encodeToString(fileContents.getBytes());
    }
}
