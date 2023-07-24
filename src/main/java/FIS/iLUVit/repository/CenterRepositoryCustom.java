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
    /**
     * 일정 거리 내의 시설 전체 조회 + 검색어 있으면 검색어에 해당하는 시설 조회
     */
    List<Center> findByFilterForMap(double longitude, double latitude, Double distance, String searchContent);

    /**
     *  해당 시도, 시군구에서 학부모가 선택한 관심 테마를 가지고 있는 시설 조회
     */
    List<Center> findRecommendCenter(Theme theme, Location location, Pageable pageable);

    /**
     * 회원가입 과정에서 시설정보 가져오기
     */
    List<Center> findForSignup(String sido, String sigungu, String centerName);

    /**
     * 아이추가 과정에서 필요한 센터정보 가져오기
     */
    List<Center> findCenterForAddChild(String sido, String sigungu, String centerName);


}
