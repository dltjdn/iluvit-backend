package FIS.iLUVit.service;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.repository.dto.CenterAndDistancePreview;
import FIS.iLUVit.repository.CenterRepository;
import FIS.iLUVit.repository.dto.CenterInfoDto;
import FIS.iLUVit.repository.dto.CenterPreview;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CenterService {

    private final CenterRepository centerRepository;

    public List<CenterPreview> findByFilter(List<Area> areas, Theme theme, Integer interestedAge, String kindOf, Integer offset, Integer limit) {
        if (!kindOf.equals("Kindergarten") && !kindOf.equals("ChildHouse")) {
            throw new RuntimeException();
        }
        return centerRepository.findByFilter(areas, theme, interestedAge, kindOf, offset, limit);
    }


    public List<CenterAndDistancePreview> findByFilterAndMap(double longitude, double latitude, Theme theme, Integer interestedAge, String kindOf, Integer distance) {
        if (!kindOf.equals("Kindergarten") && !kindOf.equals("childHouse")) {
            throw new RuntimeException();
        }
        List<CenterAndDistancePreview> centerDTOList = new ArrayList<>();
        centerRepository.findByMapFilter(longitude, latitude, theme, interestedAge, kindOf, distance).
                forEach(centerAndDistance -> {
                    // 거리별 계산 해서 나오기
                    if (distance >= centerAndDistance.calculateDistance(longitude, latitude)) {
                        centerDTOList.add(centerAndDistance);
                    }
                });
        return centerDTOList;
    }

    public CenterInfoDto findInfoById(Long id) {
        return centerRepository.findInfoById(id);
    }
}
