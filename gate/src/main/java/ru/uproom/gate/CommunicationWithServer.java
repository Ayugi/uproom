package ru.uproom.gate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Класс, реализующий функционал обмена данными с облачным сервером
 * <p/>
 * Created by osipenko on 08.08.14.
 */
public class CommunicationWithServer implements AutoCloseable, Runnable {


    //##############################################################################################################
    //######    параметры класса


    private String host = "localhost";
    private int port = 6009;
    private Socket socket = null;
    private BufferedReader reader = null;
    private PrintWriter writer = null;
    private boolean connected = false;
    private boolean reconnect = false;

    private MainCommander commander = null;
    private MainWatcher watcher = null;
    private ZWaveHome home = null;


    //##############################################################################################################
    //######    конструкторы


    public CommunicationWithServer() {
    }

    public CommunicationWithServer(String host, int port) {
        this.host = host;
        this.port = port;
    }


    //##############################################################################################################
    //######    обработка параметров класса


    //------------------------------------------------------------------------
    //  адрес сервера

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }


    //------------------------------------------------------------------------
    //  порт сервера

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    //------------------------------------------------------------------------
    //  Объект обработки команд внешних систем (облачного сервиса, например)

    public MainCommander getCommander() {
        return commander;
    }

    public void setCommander(MainCommander commander) {
        this.commander = commander;
    }


    //------------------------------------------------------------------------
    //  обработчик событий сети узлов Z-Wave

    public MainWatcher getWatcher() {
        return watcher;
    }

    public void setWatcher(MainWatcher watcher) {
        this.watcher = watcher;
    }


    //------------------------------------------------------------------------
    //  список узлов сети Z-Wave

    public ZWaveHome getHome() {
        return home;
    }

    public void setHome(ZWaveHome home) {
        this.home = home;
    }


    //------------------------------------------------------------------------
    //  установлено ли соединение с сервером

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }


    //------------------------------------------------------------------------
    //  требование переподключения

    public boolean isReconnect() {
        return reconnect;
    }

    public void setReconnect(boolean reconnect) {
        this.reconnect = reconnect;
    }


    //##############################################################################################################
    //######    методы класса


    //------------------------------------------------------------------------
    //  установление связи с системой внешнего управления

    public void open() {

        try {
            // создаем сокет
            socket = new Socket(host, port);
            // Поток чтения
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // поток записи
            writer = new PrintWriter(socket.getOutputStream(), true);
            // признак удачного соединения
            setConnected(true);
        } catch (IOException e) {
            setConnected(false);
            System.out.println("[ERR] - CommunicationWithServer - open - " + e.getLocalizedMessage());
        }

    }


    //------------------------------------------------------------------------
    //  завершение работы с обменом данными

    @Override
    public void close() {

        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
        } catch (IOException e) {
            System.out.println("[ERR] - CommunicationWithServer - close - " + e.getLocalizedMessage());
        }

        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.out.println("[ERR] - CommunicationWithServer - close - " + e.getLocalizedMessage());
        }

        setConnected(false);

    }

    @Override
    public void run() {

        while (!commander.isExit()) {

            // установка соединения с сервером
            open();
            // если не установлено соединение
            if (!isConnected()) {
                // закрываем текущее соединение
                close();
                // ожидаем следующую попытку подключения
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                // следующая попытка
                continue;
            }

            // если соединение установлено
            String line = null;
            do {
                System.out.println("[INF] - CommunicationWithServer - run - waiting for next command...");

                // попытка чтения очередной порции данных
                try {
                    line = reader.readLine();
                } catch (IOException e) {
                    line = null;
                    System.out.println("[ERR] - CommunicationWithServer - run - " + e.getLocalizedMessage());
                }
                if (line == null) continue;

                System.out.println("[INF] - CommunicationWithServer - run - command : " + line);

                // формирование команды
                ZWaveCommand command = new ZWaveCommand();
                command.setCommandFromString(line);
                command.setHomeId(watcher.getHome().getHomeId());

                // исполнение команды
                ZWaveFeedback feedback = commander.execute(command);

                // ответное сообщение серверу
                if (feedback != null) writer.println(feedback.getFeedback());
                else writer.println(command.getCommand() + " ( err )");

                System.out.println("[INF] - CommunicationWithServer - run - command finished ");
            } while (!commander.isExit() && !isReconnect() && line != null);
        }

        // закрываем существующее соединение
        close();

    }

}
