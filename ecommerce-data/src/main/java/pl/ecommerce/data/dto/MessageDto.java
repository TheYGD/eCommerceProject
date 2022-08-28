package pl.ecommerce.data.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import pl.ecommerce.data.domain.Message;

@Getter
@Setter
public class MessageDto {

    private String author;
    private LocalDateTime date;
    private String content;


    public MessageDto(Message message) {
        this.author = message.getAuthor().getCredentials().getUsername();;
        this.date = message.getDate();
        this.content = message.getContent();
    }

}
