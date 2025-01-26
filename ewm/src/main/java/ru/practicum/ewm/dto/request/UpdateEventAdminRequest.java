package ru.practicum.ewm.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.ewm.dto.Location;
import ru.practicum.ewm.model.AdminStateAction;

import java.time.LocalDateTime;

@Data
public class UpdateEventAdminRequest {

    @Size(min = 20, max = 2000)
    private String annotation;

    private Integer category;

    @Size(min = 20, max = 7000)
    private String description;

    @FutureOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Location location;

    @JsonProperty
    private Boolean paid;

    @Min(0)
    @JsonProperty
    private Integer participantLimit;

    @JsonProperty
    private Boolean requestModeration;

    private AdminStateAction stateAction;

    @Size(min = 3, max = 120)
    private String title;

}
