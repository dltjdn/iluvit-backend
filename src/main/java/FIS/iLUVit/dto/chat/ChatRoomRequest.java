package FIS.iLUVit.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class ChatRoomRequest {
    @NotBlank(message = "메시지에 값을 채워주세요.")
    private String message;

    @NotBlank(message = "대화방 id 필요")
    private Long room_id;

    public void addMessage(String message){
        this.message = message;
    }

    public void addRoomId(Long roomId){
        this.room_id = roomId;
    }

}
