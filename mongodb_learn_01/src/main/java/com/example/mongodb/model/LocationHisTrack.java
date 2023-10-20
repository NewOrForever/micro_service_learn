package com.example.mongodb.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

/**
 * 【请填写功能名称】对象 location_his_track
 *
 * @author freecode
 * @date 2022-09-21
 */
@Data
@Document("his_track")
public class LocationHisTrack {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    private Long id;

    /**
     * 车牌颜色,1 蓝色，2 黄色
     */
    @Field
    private Integer color;

    /**
     * 车牌号码
     */
    @Field
    private String vehicleNo;

    /**
     * 设备编号
     */
    @Field
    private String deviceNo;

    /**
     * 结果
     */
    @Field
    private List<TrackInfoDetailDto> result;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Field
    private Date startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Field
    private Date endTime;

}
