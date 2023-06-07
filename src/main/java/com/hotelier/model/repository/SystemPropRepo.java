package com.hotelier.model.repository;

import com.hotelier.model.entity.SystemProp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.HashMap;
import java.util.Map;

public interface SystemPropRepo extends JpaRepository<SystemProp, Integer> {
    Map<String,String> props = new HashMap<>();

    default String findByNKey(String key){
        if(props.isEmpty()){
            findAll().forEach(prop -> props.put(prop.getNkey(),prop.getNvalue()));
        }
        return props.get(key);
    }


}
