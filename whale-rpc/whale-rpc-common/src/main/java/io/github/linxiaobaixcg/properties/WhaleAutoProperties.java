package io.github.linxiaobaixcg.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author lcq
 * @description:
 * @date 2021/3/4 17:34
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "whale")
@EnableConfigurationProperties(WhaleAutoProperties.class)
public class WhaleAutoProperties {


}
