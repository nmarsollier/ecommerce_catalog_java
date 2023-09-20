package com.catalog.utils.rabbit;

import com.catalog.utils.gson.Builder;
import com.catalog.utils.gson.JsonSerializable;
import com.catalog.utils.validator.Required;
import com.google.gson.annotations.SerializedName;

public class RabbitEvent implements JsonSerializable {
    // tipo de mensaje enviado
    @SerializedName("type")
    @Required
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
    @Required
    public Object message;

    public static RabbitEvent fromJson(String json) {
        return Builder.gson().fromJson(json, RabbitEvent.class);
    }

    @Override
    public String toJson() {
        return Builder.gson().toJson(this);
    }
}
