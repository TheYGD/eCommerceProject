package pl.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ecommerce.data.domain.Chat;
import pl.ecommerce.data.domain.User;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    Chat save(Chat chat);


    List<Chat> findAllByUser1OrUser2OrderByLastActivityDesc(User user1, User user2);
}
