package ru.uproom.gate.test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by osipenko on 10.08.14.
 */
public class Main {

    public static void main(String[] args) {

        int port = 6009; // случайный порт (может быть любое число от 1025 до 65535)
        try {
            ServerSocket ss = new ServerSocket(port); // создаем сокет сервера и привязываем его к вышеуказанному порту
            System.out.println("Waiting for a client...");

            Socket socket = ss.accept(); // заставляем сервер ждать подключений и выводим сообщение когда кто-то связался с сервером
            System.out.println("Client accepted!");
            System.out.println();

            // Берем входной и выходной потоки сокета, теперь можем получать и отсылать данные клиенту.
            BufferedReader sin = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter sout = new PrintWriter(socket.getOutputStream(),true);

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter writer = new PrintWriter(System.out,true);

            String line = null, cons = null;
            while(true) {
                // ждем ввода команды вручную
                writer.print("input >\t");
                writer.flush();
                cons = reader.readLine();
                if (cons.equalsIgnoreCase("exit")) break;

                // отсылаем ее клиенту
                sout.println(cons);

                writer.println("<<--\t " + cons);

                // ждем ответа клиента
                line = sin.readLine();
                writer.println("-->>\t " + line);
            }
            writer.close();
            reader.close();
            sin.close();
            sout.close();
            socket.close();
            ss.close();

        } catch(Exception x) { x.printStackTrace(); }
    }

}
