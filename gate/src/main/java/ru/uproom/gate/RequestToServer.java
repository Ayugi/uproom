package ru.uproom.gate;

import java.io.IOException;

/**
 * Created by osipenko on 26.08.14.
 */
public class RequestToServer extends CommunicationWithServer {


    //------------------------------------------------------------------------
    //  обработка данных в цикле обмена

    @Override
    public void run() {

        // установка соединения с сервером
        open();
        if (!isConnected()) return;

        // если соединение установлено - отсылаем запрос серверу
        while (!getCommander().isExit()) {

            String line = null;
            do {
                System.out.println("[INF] - RequestToServer - run - waiting for next command...");

                // попытка чтения очередной команды
                try {
                    line = getReader().readLine();
                } catch (IOException e) {
                    line = null;
                    System.out.println("[ERR] - RequestToServer - run - " + e.getLocalizedMessage());
                }
                if (line == null) continue;

                System.out.println("[INF] - RequestToServer - run - command : " + line);

                // формирование команды
                ZWaveCommand command = new ZWaveCommand();
                command.setCommandFromString(line);
                command.setHomeId(getWatcher().getHome().getHomeId());

                // исполнение команды
                ZWaveFeedback feedback = getCommander().execute(command);

                // ответное сообщение серверу
                if (feedback != null) getWriter().println(feedback.getFeedback());
                else getWriter().println(command.getCommand() + " ( err )");

                System.out.println("[INF] - RequestToServer - run - command finished ");
            } while (!getCommander().isExit() && !isReconnect() && line != null);
        }

        // закрываем существующее соединение
        close();


    }
}
