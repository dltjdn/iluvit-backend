package FIS.iLUVit.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequestDto {
    @NotBlank(message = "메시지에 값을 채워주세요.")
    private String message;

    @NotBlank(message = "대화방 id 필요")
    private Long roomId;
}
