package pl.ecommerce.web.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.ecommerce.data.domain.Chat;
import pl.ecommerce.data.domain.UserCredentials;
import pl.ecommerce.data.dto.MessageDto;
import pl.ecommerce.data.other.StringResponse;
import pl.ecommerce.web.service.MessageService;

import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;


    @GetMapping
    public String messagePage(@AuthenticationPrincipal UserCredentials userCredentials, Model model) {

        List<Chat> chatList = messageService.findMessageGroup(userCredentials);

        model.addAttribute("chatList", chatList);

        return "messages/show";
    }


    @GetMapping("/{id}")
    @ResponseBody
    public Page<MessageDto> getMessages(@AuthenticationPrincipal UserCredentials userCredentials,
                                        @PathVariable("id") Long chatId,
                                        @RequestParam(defaultValue = "-1") int pageNr) {

        return messageService.findMessagesFromGroup(userCredentials, chatId, pageNr);
    }


    @PostMapping("/{id}")
    @ResponseBody
    public StringResponse sendMessage(@AuthenticationPrincipal UserCredentials userCredentials,
                                      @PathVariable("id") Long chatId, @RequestBody String content,
                                      Map<String, String> formData) {

         String response = messageService.sendMessage(userCredentials, chatId, content);
        return new StringResponse(response);
    }


    @PostMapping("/{id}/close")
    @ResponseBody
    public StringResponse closeChat(@AuthenticationPrincipal UserCredentials userCredentials,
                                      @PathVariable("id") Long chatId) {

        messageService.closeChat(userCredentials, chatId);
        return new StringResponse("Chat blocked.");
    }


    @PostMapping("/{id}/reopen")
    @ResponseBody
    public StringResponse reopenChat(@AuthenticationPrincipal UserCredentials userCredentials,
                                    @PathVariable("id") Long chatId) {

        messageService.reopenChat(userCredentials, chatId);
        return new StringResponse("Chat unlocked.");
    }
}
