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
public class Theme {
    private Boolean english = false;                // 양어
    private Boolean foreigner = false;              // 원어민
    private Boolean clean = false;                  // 청결
    private Boolean buddhism = false;               // 불교
    private Boolean christianity = false;           // 기독교
    private Boolean catholic = false;               // 천주교
    private Boolean animal = false;                 // 동물
    private Boolean plant = false;                  // 식물
    private Boolean camping = false;                // 캠핑
    private Boolean nature = false;                 // 자연친화
    private Boolean art = false;                    // 미술
    private Boolean music = false;                  // 음악
    private Boolean math = false;                   // 수학
    private Boolean sport = false;                  // 체육
    private Boolean coding = false;                 // 코딩
    private Boolean manner = false;                 // 예절
    private Boolean genius = false;                 // 양재

    // 필터링 사용하기 위해서 관심 목록만 뽑아내기
    public List<String> trueList() throws IllegalAccessException {
        List<String> trueList = new ArrayList<>();
        Field[] fields = Theme.class.getDeclaredFields();
        for(Field field : fields){
            field.setAccessible(true);
            if((Boolean) field.get(this)){
                trueList.add(field.getName());
            }
        }
        return trueList;
    }

    @Builder
    public Theme(Boolean english, Boolean foreigner, Boolean clean, Boolean buddhism, Boolean christianity, Boolean catholic, Boolean animal, Boolean plant, Boolean camping, Boolean nature, Boolean art, Boolean music, Boolean math, Boolean sport, Boolean coding, Boolean manner, Boolean genius) {
        this.english = english;
        this.foreigner = foreigner;
        this.clean = clean;
        this.buddhism = buddhism;
        this.christianity = christianity;
        this.catholic = catholic;
        this.animal = animal;
        this.plant = plant;
        this.camping = camping;
        this.nature = nature;
        this.art = art;
        this.music = music;
        this.math = math;
        this.sport = sport;
        this.coding = coding;
        this.manner = manner;
        this.genius = genius;
    }
}
