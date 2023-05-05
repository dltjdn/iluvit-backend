package FIS.iLUVit.domain.common.embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Score {

    GET(1, "조회로 인해 1점 plus"),
    Review(3, "시설에 리뷰 댓글이 달림");

    private final Integer score;
    private final String info;

}
