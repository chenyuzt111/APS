package com.benewake.system.entity.feishu;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;


@Data
public class Message {
    @JsonProperty("receive_id")
    private String receiveId;

    @JsonProperty("msg_type")
    private String msgType;

    @JsonProperty("content")
    private String content;

}





