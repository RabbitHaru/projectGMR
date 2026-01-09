package me.shinsunyoung.projectweatherly.region.Service;

import lombok.RequiredArgsConstructor;
import me.shinsunyoung.projectweatherly.region.domain.Region;
import me.shinsunyoung.projectweatherly.region.repository.RegionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class RegionService {

    private final RegionRepository regionRepository;

    public List<Region> findAll(){
        return regionRepository.findAll();
    }

    public List<Region> findActiveRegions(){
        return regionRepository.findAll()
                .stream()
                .filter(Region::isActive)
                .toList();
    }

    public Optional<Region> findByCode(String regionCode){
        return regionRepository.findById(regionCode);
    }

    public Region save(Region region){
        return regionRepository.save(region);
    }
}
