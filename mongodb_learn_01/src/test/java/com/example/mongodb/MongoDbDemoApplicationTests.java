package com.example.mongodb;


import com.example.mongodb.model.Employee;
import com.example.mongodb.model.LocationHisTrack;
import com.example.mongodb.model.TrackInfoDetailDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import org.apache.commons.lang.time.DateFormatUtils;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.DateOperators;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
class MongoDbDemoApplicationTests {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void testCollection() {

        if (mongoTemplate.collectionExists("emp")) {
            mongoTemplate.dropCollection("emp");
        }
        mongoTemplate.createCollection("emp");
    }

    @Test
    public void testInsert() {
        Employee employee = new Employee();
        employee.setId(1);
        employee.setName("a");
        employee.setAge(18);
        employee.setSalary(20000d);
        employee.setBirthDay(new Date());
        employee.setUnSerialized("str");

        // mongoTemplate.insert(employee); // 重复插入报错DuplicateKeyException
        mongoTemplate.save(employee);       // 重复插入更新
    }

    @Test
    public void testInsertMany() throws ParseException, JsonProcessingException {
        List<LocationHisTrack> list = new ArrayList<>();
        LocationHisTrack locationHisTrack = new LocationHisTrack();
        locationHisTrack.setId(1528601079484952577L);
        locationHisTrack.setColor(2);
        locationHisTrack.setVehicleNo("陕YH0008");
        locationHisTrack.setStartTime(DateFormat.getDateInstance().parse("2022-05-20 00:00:00"));
        locationHisTrack.setEndTime(DateFormat.getDateInstance().parse("2022-05-21 00:00:00"));
        List<TrackInfoDetailDto> trackInfoDetailList = new ArrayList<>();
        locationHisTrack.setResult(trackInfoDetailList);
        TrackInfoDetailDto trackInfoDetailDto = new TrackInfoDetailDto();
        trackInfoDetailList.add(trackInfoDetailDto);
        trackInfoDetailDto.setLat("123");
        trackInfoDetailDto.setLon("111");
        trackInfoDetailDto.setSpeed("10");
        trackInfoDetailDto.setTime("2021-08-18 15:07:50");

        list.add(locationHisTrack);

        mongoTemplate.insert(list, LocationHisTrack.class);
    }

    @Test
    public void find() throws ParseException {


        // from mongodb
//        String queryJson = String.format("{color:2, startTime:new Date('2022-05-20 00:00:00')}");
//        Query query = new BasicQuery(queryJson);

        Criteria criteria = new Criteria();
        criteria.andOperator(
                Criteria.where("color").is(2),
                Criteria.where("startTime").is(DateFormat.getDateInstance().parse("2022-05-20 00:00:00"))
        );
        Query query = new Query(criteria);

        List<LocationHisTrack> locationHisTracks = mongoTemplate.find(query, LocationHisTrack.class);
        locationHisTracks.forEach(System.out::println);
    }

}
