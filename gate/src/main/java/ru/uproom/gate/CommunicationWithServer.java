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
    private int times = 0;

    private MainCommander commander = null;
    private MainWatcher watcher = null;
    private ZWaveHome home = null;


    //##############################################################################################################
    //######    конструкторы


    public CommunicationWithServer() {

    }


    // direction - направление
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


    //------------------------------------------------------------------------
    //  количество попыток подключения

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }


    //------------------------------------------------------------------------
    //  получение внутренних сущностей

    protected Socket getSocket() {
        return socket;
    }

    protected BufferedReader getReader() {
        return reader;
    }

    protected PrintWriter getWriter() {
        return writer;
    }


    //##############################################################################################################
    //######    методы класса


    //------------------------------------------------------------------------
    //  установление связи с системой внешнего управления

    public void open() {

        int counter = 0;
        while (!commander.isExit() && !isConnected() && (counter < getTimes() || getTimes() <= 0)) {

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
            }

            // следующая попытка
            counter++;
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


    //------------------------------------------------------------------------
    //  обработка данных в цикле обмена

    @Override
    public void run() {

        // обмен данными начат
        do {

            // установка соединения с сервером
            open();
            if (!isConnected()) continue;

            String line = null;
            do {
                System.out.println("[INF] - CommunicationWithServer - run - waiting for next command...");

                // попытка чтения очередной команды
                try {
                    line = getReader().readLine();
                } catch (IOException e) {
                    line = null;
                    System.out.println("[ERR] - CommunicationWithServer - run - " + e.getLocalizedMessage());
                }
                if (line == null) continue;

                System.out.println("[INF] - CommunicationWithServer - run - command : " + line);

                // формирование команды
                ZWaveCommand command = new ZWaveCommand();
                command.setCommandFromString(line);
                command.setHomeId(getWatcher().getHome().getHomeId());

                // исполнение команды
                ZWaveFeedback feedback = getCommander().execute(command);

                // ответное сообщение серверу
                if (feedback != null) getWriter().println(feedback.getFeedback());
                else getWriter().println(command.getCommand() + " ( err )");

                System.out.println("[INF] - CommunicationWithServer - run - command finished ");
            } while (!getCommander().isExit() && !isReconnect() && line != null);

            // закрываем существующее соединение
            close();

        } while (!getCommander().isExit());

    }


    //------------------------------------------------------------------------
    //  поток ожидания команд сервера


}
