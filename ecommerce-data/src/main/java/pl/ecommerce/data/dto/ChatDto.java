package pl.ecommerce.data.dto;

import lombok.Getter;
import lombok.Setter;
import pl.ecommerce.data.domain.Chat;


@Getter
@Setter
public class ChatDto {

    private String title;
    private String user1;
    private String user2;


    public ChatDto(Chat chat) {
        this.title = chat.getTitle();
        this.user1 = chat.getUser1().getCredentials().getUsername();
        this.user2 = chat.getUser2().getCredentials().getUsername();
    }
}
