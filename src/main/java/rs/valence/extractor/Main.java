package rs.valence.extractor;

public class Main {
    public static void main(String[] args) {
        System.out.println("Downloading jar...");
        Utils.downloadJar(args[0]);
    }
}