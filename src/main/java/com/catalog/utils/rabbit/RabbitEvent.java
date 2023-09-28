package com.catalog.utils.rabbit;

import com.catalog.utils.gson.GsonTools;
import com.google.gson.annotations.SerializedName;
import jakarta.validation.constraints.NotEmpty;

public class RabbitEvent {
    // tipo de mensaje enviado
    @SerializedName("type")
    @NotEmpty
    public String type;

    // Version del protocolo
    @SerializedName("version")
    public int version;

    // Por si el destinatario necesita saber de donde viene el mensaje
    @SerializedName("queue")
    public String queue;

    // Por si el destinatario necesita saber de donde viene el mensaje
    @SerializedName("exchange")
    public String exchange;

    // El body del mensaje
    @SerializedName("message")
    @NotEmpty
    public Object message;

    public static RabbitEvent fromJson(String json) {
        return GsonTools.gson().fromJson(json, RabbitEvent.class);
    }
}
