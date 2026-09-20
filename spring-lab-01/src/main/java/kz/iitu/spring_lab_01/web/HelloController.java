package kz.iitu.spring_lab_01.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class HelloController {

    @Value("${app.owner:unknown}")
    private String owner;

    @GetMapping("/hello")
    public Greeting hello(@RequestParam(defaultValue = "world") String name) {
        return new Greeting("Hello, " + name + "!", owner, LocalDateTime.now());
    }

    @GetMapping("/info")
    public Info info() {
        return new Info(owner,
                System.getProperty("java.version"),
                Runtime.getRuntime().availableProcessors());
    }

    @GetMapping("/factorial")
    public FactorialResult calculateFactorial(@RequestParam(defaultValue = "0") int n) {
        if (n < 0 || n > 20) {
            throw new IllegalArgumentException("Number n must be between 0 and 20");
        }
        long result = 1;
        for (int i = 1; i <= n; i++) {
            result *= i;
        }
        return new FactorialResult(n, result);
    }

    public record FactorialResult(int n, long factorial) { }

    public record Greeting(String message, String owner, LocalDateTime timestamp) { }

    public record Info(String owner, String javaVersion, int cpuCores) { }
}

