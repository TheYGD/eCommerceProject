package pl.ecommerce.data.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "messages")
public class Message extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "message_group_id")
    private Chat chat;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    private LocalDateTime date;

    private String content;
}
