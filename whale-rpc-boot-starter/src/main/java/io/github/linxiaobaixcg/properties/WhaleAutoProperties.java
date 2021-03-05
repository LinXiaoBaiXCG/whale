package io.github.linxiaobaixcg.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author lcq
 * @description:
 * @date 2021/3/4 17:34
 */
@ConfigurationProperties(prefix = "whale")
public class WhaleAutoProperties {

    private String test;

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }
}
