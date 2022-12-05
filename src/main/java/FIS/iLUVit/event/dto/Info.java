package FIS.iLUVit.event.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Info {
    private String status;
    private String id;
    private String message;
    private Details details;
}