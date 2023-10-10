package FIS.iLUVit.domain.common.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationTitle {

    ILUVIT("ILUVIT", "아이러빗"),
    FINGERPRINT("FINGERPRINT", "지문등록");

    private final String value;
    private final String description;

}
