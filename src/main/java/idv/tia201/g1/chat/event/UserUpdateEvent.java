package idv.tia201.g1.chat.event;

import idv.tia201.g1.dto.PayloadDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserUpdateEvent extends ApplicationEvent {
    private final PayloadDTO payloadDTO;

    public UserUpdateEvent(Object source, PayloadDTO payloadDTO) {
        super(source);
        this.payloadDTO = payloadDTO;
    }
}