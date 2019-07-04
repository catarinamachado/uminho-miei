package Model;

import Common.ConnectionType;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Packet implements Serializable {
    private ConnectionType connectionType;
    private List<TFile> tFiles;
    private String digitalSignature;

    public Packet(@NotNull ConnectionType connectionType) {
        this.connectionType = connectionType;
        this.tFiles = new ArrayList<>();
        this.digitalSignature = "";
    }

    public Packet(@NotNull ConnectionType connectionType, @NotNull List<TFile> tFiles) {
        this.connectionType = connectionType;
        this.tFiles = tFiles;
        this.digitalSignature = "";
    }

    public Packet(@NotNull ConnectionType connectionType, @NotNull List<TFile> tFiles, @NotNull String digitalSignature) {
        this.connectionType = connectionType;
        this.tFiles = tFiles;
        this.digitalSignature = digitalSignature;
    }

    public ConnectionType getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(ConnectionType connectionType) {
        this.connectionType = connectionType;
    }

    public List<TFile> gettFiles() {
        return tFiles;
    }

    public void settFiles(List<TFile> tFiles) {
        this.tFiles = tFiles;
    }

    public String getDigitalSignature() {
        return digitalSignature;
    }

    public void setDigitalSignature(String digitalSignature) {
        this.digitalSignature = digitalSignature;
    }

    public static Packet fromString(String p) {
        String[] ss = p.split("'-'");

        return new Packet(
                ConnectionType.valueOf(ss[0]),
                ss.length > 1 ? tFilesFromString(ss[1]) : new ArrayList<>(),
                ss.length > 2 ? ss[2] : ""
        );
    }

    public String toString() {
        return connectionType + "'-'" + tFilesToString()+ "'-'" + digitalSignature;
    }

    private String tFilesToString() {
        StringBuilder s = new StringBuilder();

        tFiles.forEach(tFile -> {
            s.append(tFile.toString());
            s.append("'--'");
        });

        return s.toString();
    }

    private static List<TFile> tFilesFromString(String s) {
        String[] ss = s.split("'--'");

        List<TFile> tFiles = new ArrayList<>();
        for (String s1 : ss) {
            tFiles.add(TFile.fromString(s1));
        }

        return tFiles;
    }
}
