package com.michelin.restaurants.entity;

import com.michelin.restaurants.dto.RestaurantDto;
import jakarta.persistence.*;
import lombok.Data;

@Entity(name = "restaurant")
@Data
public class RestaurantEntity {

    @Id
    @Column(name = "id")
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "imageUrl")
    private String imageUrl;

    public static RestaurantEntity buildFromDto(RestaurantDto restaurantDto) {
        var restaurantEntity = new RestaurantEntity();
        restaurantEntity.setId(restaurantDto.id());
        restaurantEntity.setName(restaurantDto.name());
        restaurantEntity.setAddress(restaurantDto.address());
        restaurantEntity.setImageUrl(restaurantDto.imageUrl());
        return restaurantEntity;
    }
}
