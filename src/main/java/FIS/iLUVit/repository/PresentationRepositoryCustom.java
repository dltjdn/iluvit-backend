package FIS.iLUVit.repository;

import FIS.iLUVit.domain.Presentation;
import FIS.iLUVit.dto.presentation.PresentationForUserResponse;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.KindOf;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

public interface PresentationRepositoryCustom {

    /*
        지역과 테마와 관심 연령과 종류와 설명회 종료일 이동과 시설이름이 각각 검색 조건에 부합하도록 필터링을하여 PresentationForUserDto 객체를 불러옵니다.
     */
    Slice<Presentation> findByFilter(List<Area> areas, Theme theme, Integer interestedAge, KindOf kindOf, String searchContent, Pageable pageable);

}
