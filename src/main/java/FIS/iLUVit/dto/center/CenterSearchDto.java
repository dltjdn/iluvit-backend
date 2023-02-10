package FIS.iLUVit.dto.center;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
public class CenterSearchDto <T>{
    private Offset offset;
    private List<T> data;

    public CenterSearchDto(List<T> data, Integer startIndex, Integer endIndex) {
        this.data = data;
        this.offset = new Offset(startIndex, endIndex);
    }

    @Getter
    @AllArgsConstructor
    private static class Offset {
        Integer startIndex;
        Integer endIndex;
    }
}
