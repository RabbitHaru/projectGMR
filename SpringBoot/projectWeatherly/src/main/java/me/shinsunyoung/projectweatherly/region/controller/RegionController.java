package me.shinsunyoung.projectweatherly.region.controller;

import lombok.RequiredArgsConstructor;
import me.shinsunyoung.projectweatherly.region.domain.Region;
import me.shinsunyoung.projectweatherly.region.repository.RegionRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RegionController {

    private final RegionRepository regionRepository;

    @GetMapping("/regions")
    public List<Region> regions(){
        return regionRepository.findAll();
    }
}
