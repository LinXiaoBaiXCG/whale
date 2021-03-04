package io.github.linxiaobaixcg.configure;

import io.github.linxiaobaixcg.properties.WhaleAutoProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author lcq
 * @description: whale自动配置
 * @date 2021/3/4 17:34
 */
@Configuration
@EnableConfigurationProperties(WhaleAutoProperties.class)
@ConditionalOnProperty(
        prefix = "whale",
        name = "enable",
        havingValue = "true"
)
public class WhaleAutoConfigure {

}
