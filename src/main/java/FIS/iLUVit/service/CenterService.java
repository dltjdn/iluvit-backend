package FIS.iLUVit.service;

import FIS.iLUVit.controller.dto.CenterInfoResponseDto;
import FIS.iLUVit.controller.dto.CenterModifyRequestDto;
import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Score;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.exception.CenterException;
import FIS.iLUVit.exception.UserException;
import FIS.iLUVit.repository.ParentRepository;
import FIS.iLUVit.repository.dto.CenterAndDistancePreview;
import FIS.iLUVit.repository.CenterRepository;
import FIS.iLUVit.repository.dto.CenterBannerDto;
import FIS.iLUVit.repository.dto.CenterPreview;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CenterService {

    private final CenterRepository centerRepository;
    private final ImageService imageService;
    private final ParentRepository parentRepository;

    public Slice<CenterPreview> findByFilter(List<Area> areas, Theme theme, Integer interestedAge, String kindOf, Pageable pageable) {
        if (!kindOf.equals("Kindergarten") && !kindOf.equals("ChildHouse") && !kindOf.equals("ALL")) {
            throw new RuntimeException();
        }
        Slice<CenterPreview> results = centerRepository.findByFilter(areas, theme, interestedAge, kindOf, pageable);
        results.getContent().forEach(centerPreview -> {
            Long centerId = centerPreview.getId();
            String centerProfileDir = imageService.getCenterProfileDir();
            centerPreview.setProfileImage(imageService.getEncodedProfileImage(centerProfileDir, centerId));
        });
        return results;
    }

    public List<CenterAndDistancePreview> findByFilterAndMap(double longitude, double latitude, Theme theme, Integer interestedAge, String kindOf, Integer distance) {
        if (!kindOf.equals("Kindergarten") && !kindOf.equals("childHouse")) {
            throw new RuntimeException();
        }
        Map<Long, CenterAndDistancePreview> map =
                centerRepository.findByMapFilter(longitude, latitude, theme, interestedAge, kindOf, distance)
                        .stream().collect(Collectors.toMap(CenterAndDistancePreview::getId,
                        centerAndDistancePreview -> centerAndDistancePreview));
        List<Long> idList = new ArrayList<>(map.keySet());
        imageService.getEncodedProfileImage(imageService.getCenterProfileDir(), idList)
                .forEach((id, image) -> {
                    map.get(id).setImage(image);
                });
        return new ArrayList<>(map.values());
    }

    public CenterInfoResponseDto findInfoById(Long id) {
        Center center = centerRepository.findInfoByIdWithProgram(id)
                .orElseThrow(() -> new CenterException("해당 센터 존재하지 않음"));
        // Center 가 id 에 의해 조회 되었으므로 score에 1 추가
        center.addScore(Score.GET);
        List<AddInfo> addInfos = centerRepository.findInfoByIdWithAddInfo(id);
        CenterInfoResponseDto centerInfoResponseDto = new CenterInfoResponseDto(center);
        addInfos.forEach(addInfo -> centerInfoResponseDto.getAddInfos().add(addInfo.getInfo()));
        String imageDir = imageService.getCenterDir(id);
        centerInfoResponseDto.setImages(imageService.getEncodedInfoImage(imageDir, centerInfoResponseDto.getImgCnt()));
        return centerInfoResponseDto;
    }

    public CenterBannerDto findBannerById(Long id) {
        CenterBannerDto dto = centerRepository.findBannerById(id);
        String profileDir = imageService.getCenterProfileDir();
        dto.setProfileImage(imageService.getEncodedProfileImage(profileDir, id));
        return dto;
    }

    public Long modifyCenter(Long id, CenterModifyRequestDto requestDto, List<MultipartFile> files) {
        // 해당하는 center 없으면 RuntimeException 반환
        Center center = centerRepository.findById(id).orElseThrow(RuntimeException::new);
        String centerDir = imageService.getCenterDir(id);
        imageService.saveInfoImage(files, centerDir);
        center.update(requestDto);
        return center.getId();
    }

    public List<String> findCenterForParent(Long userId) {
        Parent parent = parentRepository.findById(userId).orElseThrow(() -> new UserException("해당 유저가 존재 하지 않습니다."));
        Theme theme = parent.getTheme();
        List<Long> idList = centerRepository.findByThemeAndAgeOnly3(theme, PageRequest.of(0, 3, Sort.by("score")));
        return new ArrayList<>(imageService.getEncodedProfileImage(imageService.getCenterProfileDir(), idList).values());
    }
}
