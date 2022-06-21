package FIS.iLUVit.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class PostList {
    Slice<GetPostResponsePreview> previews;

    public PostList(Slice<GetPostResponsePreview> previews) {
        this.previews = previews;
    }
}
