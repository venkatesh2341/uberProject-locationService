package com.uber_project.LocationService.Service.Impl;

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
    private static final Double SEARCH_RADIUS= 1.0;
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
    public List<String> getNearbyDrivers(Double latitude, Double longitude) {
        GeoOperations<String, String> geoOps = stringRedisTemplate.opsForGeo();
        Distance radius= new Distance(SEARCH_RADIUS, Metrics.KILOMETERS);
        Circle within= new Circle(new Point(latitude,longitude ),radius);

        GeoResults<RedisGeoCommands.GeoLocation<String>> results = geoOps.radius(DRIVER_GEO_OPS_KEY,within);
        List<String> nearbyDrivers= new ArrayList<>();
        for(GeoResult<RedisGeoCommands.GeoLocation<String>> result: results){
            nearbyDrivers.add(result.getContent().getName());
        }
        return nearbyDrivers;
    }
}
