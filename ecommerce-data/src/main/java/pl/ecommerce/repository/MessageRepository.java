package pl.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ecommerce.data.domain.Chat;
import pl.ecommerce.data.domain.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Message save(Message message);


    Page<Message> findAllByChatOrderByDateAsc(Chat chat, Pageable pageable);

    long countByChat(Chat chat);
}
