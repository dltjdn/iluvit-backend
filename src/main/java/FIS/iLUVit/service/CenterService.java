package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.CenterSearchFilterDTO;
import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.repository.CenterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CenterService {

    private final CenterRepository centerRepository;

    public List<Center> findByFilter(List<Area> areas, Theme theme, Integer interestedAge, String kindOf, Integer offset, Integer limit) {
        if(!kindOf.equals("KinderGarden") || !kindOf.equals("ChildHouse")){
            throw new RuntimeException();
        }
        return centerRepository.findByFilter(areas, theme, interestedAge, kindOf, offset, limit);
    }

    public List<Center> findByFilterAndMap(double longitude, double latitude, Theme theme, Integer interestedAge, String kindOf, Integer distance) {
        if(!kindOf.equals("KinderGarden") || !kindOf.equals("ChildHouse")){
            throw new RuntimeException();
        }
        return centerRepository.findByMapFilter(longitude, latitude, theme, interestedAge, kindOf, distance);
    }
}
