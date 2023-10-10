package FIS.iLUVit.domain.center.domain;

import lombok.*;

import javax.persistence.Embeddable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@AllArgsConstructor
@Builder
public class Theme {

    @Builder.Default
    private Boolean english = false;                // 양어
    @Builder.Default
    private Boolean foreigner = false;              // 원어민
    @Builder.Default
    private Boolean clean = false;                  // 청결
    @Builder.Default
    private Boolean buddhism = false;               // 불교
    @Builder.Default
    private Boolean christianity = false;           // 기독교
    @Builder.Default
    private Boolean catholic = false;               // 천주교
    @Builder.Default
    private Boolean animal = false;                 // 동물
    @Builder.Default
    private Boolean plant = false;                  // 식물
    @Builder.Default
    private Boolean camping = false;                // 캠핑
    @Builder.Default
    private Boolean nature = false;                 // 자연친화
    @Builder.Default
    private Boolean art = false;                    // 미술
    @Builder.Default
    private Boolean music = false;                  // 음악
    @Builder.Default
    private Boolean math = false;                   // 수학
    @Builder.Default
    private Boolean sport = false;                  // 체육
    @Builder.Default
    private Boolean coding = false;                 // 코딩
    @Builder.Default
    private Boolean manner = false;                 // 예절
    @Builder.Default
    private Boolean genius = false;                 // 양재

    /**
     * true인 테마리스트를 조회합니다
     */
    public List<String> trueList() {
        return Arrays.stream(Theme.class.getDeclaredFields())// Theme 클래스의 필드들을 가져오기
                .filter(field -> {
                    try {
                        field.setAccessible(true); // 접근 허용
                        return field.get(this) != null && (Boolean) field.get(this); // 필드가 true라면
                    } catch (IllegalAccessException e) {
                        return false;
                    }
                })
                .map(Field::getName) // 그 필드의 이름을 반환
                .collect(Collectors.toList());
    }

}
