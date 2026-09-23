package kz.iitu.spring_lab_01.web;

import kz.iitu.spring_lab_01.config.AppProperties;
import kz.iitu.spring_lab_01.config.EnvironmentBanner;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/lab3")
public class Lab3Controller {

    private final AppProperties props;
    private final EnvironmentBanner banner;
    private final Environment environment;

    public Lab3Controller(AppProperties props, EnvironmentBanner banner, Environment environment) {
        this.props = props;
        this.banner = banner;
        this.environment = environment;
    }

    @GetMapping("/config")
    public Map<String, Object> config() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("owner", props.owner());
        map.put("group", props.group());
        map.put("mailFrom", props.mail().from());
        map.put("mailRetryCount", props.mail().retryCount());
        map.put("mailTimeout", props.mail().timeout().toString());
        map.put("mailEnabled", props.mail().enabled());

        // Значения Варианта 4
        map.put("uploadMaxFileSize", props.upload().maxFileSize().toString());
        map.put("uploadAllowedExtensions", props.upload().allowedExtensions());

        map.put("serverPort", environment.getProperty("server.port"));
        map.put("activeProfiles", Arrays.asList(environment.getActiveProfiles()));
        map.put("banner", banner.describe());

        return map;
    }
}