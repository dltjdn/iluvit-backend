package FIS.iLUVit.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

@Data
@NoArgsConstructor
public class PostListDto {
    Slice<PostPreviewResponse> previews;

    public PostListDto(Slice<PostPreviewResponse> previews) {
        this.previews = previews;
    }
}
