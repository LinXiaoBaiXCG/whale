package io.github.linxiaobaixcg.enums;

public enum HeartBeatType {

    PING("ping"),
    PONG("pong");

    private String type;

    HeartBeatType(String type) {
        this.type = type;
    }
}
