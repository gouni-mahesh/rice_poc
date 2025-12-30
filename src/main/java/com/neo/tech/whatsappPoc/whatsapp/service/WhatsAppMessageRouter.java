package com.neo.tech.whatsappPoc.whatsapp.service;

import com.neo.tech.whatsappPoc.user.service.UserService;
import com.neo.tech.whatsappPoc.util.UserRole;
import com.neo.tech.whatsappPoc.whatsapp.dto.IncomingMessage;
import com.neo.tech.whatsappPoc.whatsapp.flow.ManagerFlowHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WhatsAppMessageRouter {

    private final UserService userService;
    private final ManagerFlowHandler managerFlowHandler;

    public void route(IncomingMessage message) {

        // 1️⃣ Resolve & authorize user

        log.info(
                "Received message from {} | type={} | text={} | selectionId={}",
                message.getMobile(),
                message.getType(),
                message.getText(),
                message.getSelectionId()
        );

        userService.resolveUserByMobile(message.getMobile());
        UserRole role = userService.getUserRole(message.getMobile());

        log.info("Routing message from {} as role={}",
                message.getMobile(), role);

        // 2️⃣ Route by role
        if (role == UserRole.MANAGER) {
            managerFlowHandler.handle(message);
            return;
        }

        // 3️⃣ Executor not implemented yet
        log.warn("Executor flow not implemented yet for {}",
                message.getMobile());
    }
}
