package pl.ecommerce.web.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.ecommerce.data.domain.*;
import pl.ecommerce.data.dto.MessageDto;
import pl.ecommerce.exceptions.ForbiddenException;
import pl.ecommerce.exceptions.InvalidArgumentException;
import pl.ecommerce.exceptions.ItemNotFoundException;
import pl.ecommerce.repository.ChatRepository;
import pl.ecommerce.repository.MessageRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
public class MessageService {

    @Value("${pl.ecommerce.messages-on-page}")
    private int MESSAGES_ON_PAGE;
    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;

    public MessageService(MessageRepository messageRepository, ChatRepository chatRepository) {
        this.messageRepository = messageRepository;
        this.chatRepository = chatRepository;
    }



    private Chat getChat(Long chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow( () -> {
                    String message = "Message group with id=%d not found!".formatted(chatId);
                    log.warn(message);
                    return new ItemNotFoundException(message);
                } );
    }
    private void isUsersChat(Chat chat, User user) {
        if (!chat.getUser1().equals(user) && !chat.getUser2().equals(user)) {
            log.warn("User %s tried to access others messages. (messageGroup=%d)"
                    .formatted(user.getCredentials().getUsername(), chat.getId()));
            throw new ForbiddenException("Access denied.");
        }
    }


    public List<Chat> findMessageGroup(UserCredentials userCredentials) {
        User user = userCredentials.getUserAccount();
        return chatRepository.findAllByUser1OrUser2OrderByLastActivityDesc(user, user);
    }


    public Page<MessageDto> findMessagesFromGroup(UserCredentials userCredentials, Long chatId, int pageNr) {

        User user = userCredentials.getUserAccount();

        Chat chat = getChat(chatId);
        isUsersChat(chat, user);

        if (pageNr < 0) { // we have to calculate the right pageNr
            long pageCount = messageRepository.countByChat(chat);
            pageNr = (int) Math.ceil((float) pageCount / MESSAGES_ON_PAGE) - 1;
        }

        Pageable pageable = PageRequest.of(pageNr, MESSAGES_ON_PAGE);
        return messageRepository.findAllByChatOrderByDateAsc(chat, pageable)
                .map(MessageDto::new);
    }


    @Transactional
    public void sendMessage(UserCredentials userCredentials, Long chatId, String content) {
        if (content.substring(1, content.length() - 1).isBlank()) {
            throw new InvalidArgumentException("Message cannot be blank!");
        }
        // 600 = 300*2, there can be 300 chars which need escape char
        if (content.substring(1, content.length() - 1).length() > 600) {
            throw new InvalidArgumentException("Message is too long!");
        }

        User user = userCredentials.getUserAccount();

        Chat chat = getChat(chatId);
        isUsersChat(chat, user);

        if (chat.getClosedBy() != null) {
            throw new InvalidArgumentException("Error! Try again later.");
        }

        Message message = messageRepository.save( new Message(chat, user, LocalDateTime.now(),
                content.substring(1, content.length() - 1)) );
        chat.getMessages().add(message);
        chat.setLastActivity(message.getDate());
        chatRepository.save(chat);
    }


    @Transactional
    public Long createChat(User user1, User user2, MessageCause messageCause, String content, String link,
                           String linkName) {

        if (user1.equals(user2)) {
            throw new InvalidArgumentException("You cannot send messages to yourself!");
        }

        Chat chat = Chat.builder()
                .user1(user1)
                .user2(user2)
                .title(messageCause)
                .link(link)
                .linkName(linkName)
                .messages(new LinkedList<>())
                .closedBy(null)
                .build();

        Message message = messageRepository.save( new Message(chat, user1, LocalDateTime.now(), content) );
        chat.getMessages().add(message);
        chat.setLastActivity(message.getDate());
        chat = chatRepository.save(chat);

        return chat.getId();
    }


    public void closeChat(UserCredentials userCredentials, Long chatId) {
        Chat chat = getChat(chatId);
        isUsersChat(chat, userCredentials.getUserAccount());

        if (chat.getClosedBy() != null) {
            throw new InvalidArgumentException("Error! Try again later.");
        }

        chat.setClosedBy(userCredentials.getUserAccount());
        chatRepository.save(chat);
    }

    public void reopenChat(UserCredentials userCredentials, Long chatId) {
        Chat chat = getChat(chatId);
        isUsersChat(chat, userCredentials.getUserAccount());

        if (!userCredentials.getUserAccount().equals(chat.getClosedBy())) {
            throw new ForbiddenException("You can't reopen this conversation!");
        }

        chat.setClosedBy(null);
        chatRepository.save(chat);
    }
}
