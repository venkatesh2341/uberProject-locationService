package com.uber_project.LocationService.Service.Impl;

import com.uber_project.LocationService.DTO.DriverLocationDto;
import com.uber_project.LocationService.Service.LocationService;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LocationServiceImpl implements LocationService {

    private static final String DRIVER_GEO_OPS_KEY = "Driver";
    private static final Double SEARCH_RADIUS= 100.0;
    private final StringRedisTemplate stringRedisTemplate;

    LocationServiceImpl(StringRedisTemplate stringRedisTemplate){
        this.stringRedisTemplate=stringRedisTemplate;
    }

    @Override
    public void saveDriverLocation(String driverId, Double latitude, Double longitude) {

        GeoOperations<String,String> geoOps= stringRedisTemplate.opsForGeo();
        geoOps.add(DRIVER_GEO_OPS_KEY,
                new RedisGeoCommands.GeoLocation<>(
                        driverId,
                        new Point(latitude, longitude)
                ));

    }

    @Override
    public List<DriverLocationDto> getNearbyDrivers(Double latitude, Double longitude, Integer radius) {


        if(radius >=20)
            return new ArrayList<>();

        GeoOperations<String, String> geoOps = stringRedisTemplate.opsForGeo();
        //Setting distance in kms
        Distance radiusVector= new Distance(radius, Metrics.KILOMETERS);
        // creates a circle with centre(latitude and longitude) and radius(distance)
        Circle withinCircle= new Circle(new Point(latitude,longitude ),radiusVector);

        //Getting all the location points within given circle with key Driver
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = geoOps.radius(DRIVER_GEO_OPS_KEY,withinCircle);



        List<DriverLocationDto> nearbyDrivers= new ArrayList<>();
        for(GeoResult<RedisGeoCommands.GeoLocation<String>> result: results){
            Point point= geoOps.position(DRIVER_GEO_OPS_KEY,result.getContent().getName()).get(0);
            nearbyDrivers.add(
                    DriverLocationDto.builder()
                            .driverId(result.getContent().getName())
                            .latitude(point.getX())
                            .longitude(point.getY())
                            .build()
            );
        }
        if(nearbyDrivers.isEmpty())
            return getNearbyDrivers(latitude, longitude, radius + 3);
        return nearbyDrivers;
    }
}
