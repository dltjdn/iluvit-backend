package FIS.iLUVit.domain.embeddable;

import lombok.*;

import javax.persistence.Embeddable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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

    // 필터링 사용하기 위해서 관심 목록만 뽑아내기
    public List<String> trueList() throws IllegalAccessException {
        List<String> trueList = new ArrayList<>();
        Field[] fields = Theme.class.getDeclaredFields();
        for(Field field : fields){
            field.setAccessible(true);
            if(field.get(this) != null && (Boolean) field.get(this)){
                trueList.add(field.getName());
            }
        }
        return trueList;
    }

}
