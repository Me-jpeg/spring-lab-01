package kz.iitu.spring_lab_01.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.List;

@Validated
@ConfigurationProperties(prefix = "app")
public record AppProperties(

        @NotBlank String owner,

        @NotBlank String group,

        @Valid Mail mail,

        @Valid Upload upload) {

    public record Mail(
            @NotBlank @Email String from,
            @Min(1) @Max(10) @DefaultValue("3") int retryCount,
            @DefaultValue("5s") Duration timeout,
            @DefaultValue("true") boolean enabled) {
    }

    public record Upload(
            @DefaultValue("10MB") DataSize maxFileSize,
            @NotEmpty List<String> allowedExtensions) {
    }
}