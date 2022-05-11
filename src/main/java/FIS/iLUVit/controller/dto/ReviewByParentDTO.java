package FIS.iLUVit.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ReviewByParentDTO {
    private List<ReviewDto> reviews = new ArrayList<>();

    @Data
    @AllArgsConstructor
    static public class ReviewDto {

        private String centerName;
        private String content;
        private LocalDate createDate;
    }
}
