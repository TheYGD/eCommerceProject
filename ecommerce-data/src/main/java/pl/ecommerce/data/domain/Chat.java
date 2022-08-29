package pl.ecommerce.data.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "chats")

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chat extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private MessageCause title;

    private String link;
    private String linkName;

    @ManyToOne
    @JoinColumn(name = "user1_id")
    private User user1;

    @ManyToOne
    @JoinColumn(name = "user2_id")
    private User user2;

    @OneToMany(mappedBy = "chat")
    private List<Message> messages;

    private LocalDateTime lastActivity;

    @ManyToOne
    @JoinColumn(name = "closed_by_user_with_id")
    private User closedBy;


    public String getTitle() {
        return title.getCause();
    }
}
