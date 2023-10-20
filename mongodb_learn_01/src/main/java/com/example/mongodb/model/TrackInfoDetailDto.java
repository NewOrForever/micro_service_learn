package com.example.mongodb.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TrackInfoDetailDto {
    private String lat;
    private String lon;
    private String time;
    private String speed;
}
