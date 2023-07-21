package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Center;
import FIS.iLUVit.dto.center.CenterDto;
import FIS.iLUVit.dto.center.CenterRecommendDto;
import FIS.iLUVit.domain.embeddable.Location;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import FIS.iLUVit.dto.center.CenterAndDistancePreviewDto;
import FIS.iLUVit.dto.center.CenterMapPreviewDto;
import FIS.iLUVit.dto.center.CenterPreviewDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

public interface CenterRepositoryCustom {
    /*
        지도에 대한 필터로 시설 지도 미리보기 DTO 리스트를 조회합니다.
    */
    List<Center> findByFilterForMap(double longitude, double latitude, Double distance, String searchContent);

    /*
        지도 리스트에 대한 필터로 시설과 거리 미리보기 DTO를 조회합니다.
     */
    //SliceImpl<CenterAndDistancePreviewDto> findByFilterForMapList(double longitude, double latitude, Long userId, KindOf kindOf, List<Long> centerIds, Pageable pageable);


    /*
        추천 시설로 시설 추천 DTO 리스트를 조회합니다.
     */
    List<CenterRecommendDto> findRecommendCenter(Theme theme, Location location, Pageable pageable);

    /*
        회원가입을 위해 시설 DTO를 조회합니다.
     */
    Slice<CenterDto> findForSignup(String sido, String sigungu, String centerName, Pageable pageable);

    /*
        자녀 추가를 위한 시설로 시설 DTO를 조회합니다.
     */
    Slice<CenterDto> findCenterForAddChild(String sido, String sigungu, String centerName, Pageable pageable);

    /*
        시설 즐겨찾기로 시설 미리보기 DTO를 조회합니다.
     */
    Slice<CenterPreviewDto> findByPrefer(Long userId, Pageable pageable);
}
