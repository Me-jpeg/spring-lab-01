package kz.iitu.spring_lab_01.notify;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component("timestamped")
@Order(3)
public class TimestampedNotifier implements Notifier {

    private static final Logger log = LoggerFactory.getLogger(TimestampedNotifier.class);

    @PostConstruct
    void init() {
        log.info("TimestampedNotifier initialized successfully");
    }

    @Override
    public String send(String message) {
        String timestamp = Instant.now().toString();
        String result = "[" + timestamp + "] " + message;
        log.info("TIMESTAMPED >> {}", result);
        return result;
    }

    @Override
    public String channel() {
        return "timestamped";
    }
}