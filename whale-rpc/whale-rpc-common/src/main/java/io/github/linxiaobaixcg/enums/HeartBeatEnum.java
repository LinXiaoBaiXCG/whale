package io.github.linxiaobaixcg.enums;

public enum HeartBeatEnum {

    PING("ping"),
    PONG("pong");

    private String type;

    HeartBeatEnum(String type) {
        this.type = type;
    }
}
