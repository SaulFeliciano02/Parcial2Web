package logico;

public class Main {

    public static void main(String[] args) {

        Codec codec = new Codec();

        codec.encode("https://www.youtube.com/watch?v=5bBfEt5W1Nc");
        codec.encode("https://www.youtube.com/watch?v=5bBfEt5W1ac");
    }

}
