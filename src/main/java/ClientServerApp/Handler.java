package ClientServerApp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Handler implements Closeable {

    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;

    public Handler(String ip, int port) {
        try {
            this.socket = new Socket(ip, port);
            this.reader = createReader();
            this.writer = createWriter();
        } catch (UnknownHostException e) {
            new MyException(e).print();
            throw new RuntimeException();
        } catch (IOException e) {
            new MyException(e).print();
            throw new RuntimeException();
        }
    }

    public Handler(ServerSocket server) {
        try {
            this.socket = server.accept();
            this.reader = createReader();
            this.writer = createWriter();
        } catch (IOException e) {
            new MyException(e).print();
            throw new RuntimeException();
        }
    }

    public void write(String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            new MyException(e).print();
        }
    }

    public String read() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            new MyException(e).print();
            throw new RuntimeException();
        }
    }

    private BufferedWriter createWriter() {
        try {
            return new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            new MyException(e).print();
            throw new RuntimeException();
        }
    }

    private BufferedReader createReader() {
        try {
            return new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            new MyException(e).print();
            throw new RuntimeException();
        }
    }

    @Override
    public void close() throws IOException {
        writer.close();
        reader.close();
        socket.close();
    }
}
