package ru.uproom.gate.devices;

/**
 * Created by osipenko on 12.08.14.
 */
public class ZWaveFeedback {


    //##############################################################################################################
    //######    параметры класса


    private long homeId = 0;
    private String feedback = "";
    private boolean created = false;
    private boolean successful = false;


    //##############################################################################################################
    //######    обработка параметров класса


    //------------------------------------------------------------------------
    //  Идентификатор совокупности помещений (дома) ассоциированного с сетью

    public long getHomeId() {
        return homeId;
    }

    public void setHomeId(long homeId) {
        this.homeId = homeId;
    }


    //------------------------------------------------------------------------
    //  текст ответа

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }


    //------------------------------------------------------------------------
    //  текст ответа

    public boolean isCreated() {
        return created;
    }

    public void setCreated(boolean created) {
        this.created = created;
    }


    //------------------------------------------------------------------------
    //  текст ответа

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }
}
