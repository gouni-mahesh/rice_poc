package com.neo.tech.whatsappPoc.whatsapp.flow;

import com.neo.tech.whatsappPoc.buyer.entity.BuyerEntity;
import com.neo.tech.whatsappPoc.buyer.repository.BuyerRepository;
import com.neo.tech.whatsappPoc.executor.repository.ExecutorRepository;
import com.neo.tech.whatsappPoc.order.entity.OrderEntity;
import com.neo.tech.whatsappPoc.order.service.OrderService;

import com.neo.tech.whatsappPoc.util.*;
import com.neo.tech.whatsappPoc.whatsapp.dto.IncomingMessage;
import com.neo.tech.whatsappPoc.whatsapp.entity.WhatsAppConversationEntity;
import com.neo.tech.whatsappPoc.whatsapp.repository.WhatsAppConversationRepository;
import com.neo.tech.whatsappPoc.whatsapp.service.ConversationStateService;
import com.neo.tech.whatsappPoc.whatsapp.service.WhatsAppReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ManagerFlowHandler {

    private final ConversationStateService conversationStateService;
    private final BuyerRepository buyerRepository;
    private final OrderService orderService;
    private final ExecutorRepository executorRepository;
    private final WhatsAppReplyService replyService;
    private final EventPublisher eventPublisher;
    private final WhatsAppConversationRepository repository;



    // ==========================================================
    // ENTRY POINT
    // ==========================================================

    public void handle(IncomingMessage message) {

        String mobile = message.getMobile();
        String text = message.getText() != null
                ? message.getText().trim().toLowerCase()
                : null;

        WhatsAppConversationEntity conversation =
                conversationStateService.get(mobile);


        if (text != null &&
                (text.equals("hi")
                        || text.equals("hello")
                        || text.equals("hey")
                        || text.equals("baba"))) {

            if (conversation == null) {
                conversationStateService.startFlow(
                        mobile,
                        ConversationFlow.NONE,
                        ConversationStep.START
                );
            } else {
                conversationStateService.reset(conversation);
            }

            showMainMenu(mobile);
            return;
        }

        // --------------------------------------------------
        // ❌ If still no conversation, ignore message
        // --------------------------------------------------
        if (conversation == null) {
            replyService.sendText(
                    mobile,
                    "Please say *Hi* to start."
            );
            return;
        }

        // --------------------------------------------------
        // DEBUG LOG (SAFE NOW)
        // --------------------------------------------------
        log.info(
                "Conversation state for {} -> flow={}, step={}",
                mobile,
                conversation.getFlow(),
                conversation.getStep()
        );

        // --------------------------------------------------
        // MAIN MENU
        // --------------------------------------------------
        if (conversation.getFlow() == ConversationFlow.NONE
                && conversation.getStep() == ConversationStep.START) {

            handleMainMenu(message, conversation);
            return;
        }

        // --------------------------------------------------
        // MANAGER CREATE ORDER FLOW
        // --------------------------------------------------
        if (conversation.getFlow() == ConversationFlow.MANAGER_CREATE_ORDER) {
            handleCreateOrderFlow(message, conversation);
        }
    }



    // ==========================================================
    // STEP 0️⃣ GREETING (MANDATORY)
    // ==========================================================

//    private void handleGreeting(IncomingMessage message) {
//
//        String text = message.getText().trim().toLowerCase();
//
//        if (text.equals("hi") || text.equals("hello") || text.equals("hey") || text.equals(("baba"))) {
//            conversationStateService.startFlow(
//                    message.getMobile(),
//                    ConversationFlow.NONE,
//                    ConversationStep.START
//            );
//
//            showMainMenu(message.getMobile());
//            return;
//        }
//
//        log.info(
//                "Manager {} sent '{}' without greeting. Asking to say HI.",
//                message.getMobile(), message.getText()
//        );
//
//        // Later: send WhatsApp reply → "Please say HI to start"
//    }

    // ==========================================================
    // MAIN MENU
    // ==========================================================

//    private void handleMainMenu(
//            IncomingMessage message,
//            WhatsAppConversationEntity conversation
//    ) {
//        String action =
//                message.getType() == IncomingMessage.MessageType.BUTTON
//                        ? message.getSelectionId()
//                        : message.getText().trim().toLowerCase();
//        String text = message.getText().trim().toLowerCase();
//
//        if (
////                text.equals("create order") || text.equals("1") || text.equals("1️⃣")
//                "CREATE_ORDER".equals(action)
//                        || "create order".equals(action) || text.equals("1") || text.equals("1️⃣")
//        ) {
//
//            conversation.setFlow(ConversationFlow.MANAGER_CREATE_ORDER);
//            conversationStateService.updateStep(
//                    conversation,
//                    ConversationStep.SELECT_BUYER
//            );
//
//            showBuyerList(message.getMobile());
//            return;
//        }
//
//        log.warn("Unknown menu option '{}' from {}",
//                message.getText(), message.getMobile());
//    }
//
//    private void showMainMenu(String mobile) {
//
//        replyService.sendText(
//                mobile,
//                """
//                👋 Welcome!
//                Please choose an option:
//                1️⃣ Create Order
//                2️⃣ Create Buyer
//                """
//        );
//    }
//

private void showMainMenu(String mobile) {

    replyService.sendButtons(
            mobile,
            "👋 Welcome! Please choose an option:",
            List.of(
                    Map.of(
                            "type", "reply",
                            "reply", Map.of(
                                    "id", "CREATE_ORDER",
                                    "title", "Create Order"
                            )
                    ),
                    Map.of(
                            "type", "reply",
                            "reply", Map.of(
                                    "id", "CREATE_BUYER",
                                    "title", "Create Buyer"
                            )
                    )
            )
    );
}

private void handleMainMenu(
        IncomingMessage message,
        WhatsAppConversationEntity conversation
) {

    String action =
            message.getType() == IncomingMessage.MessageType.BUTTON
                    ? message.getSelectionId()
                    : message.getText().trim().toLowerCase();

    if ("CREATE_ORDER".equals(action)
            || "create order".equals(action)
            || "1".equals(action)
            || "1️⃣".equals(action)) {

        conversationStateService.updateFlowAndStep(
                conversation,
                ConversationFlow.MANAGER_CREATE_ORDER,
                ConversationStep.SELECT_BUYER
        );

        showBuyerList(message.getMobile());
        return;
    }

    replyService.sendText(
            message.getMobile(),
            "❌ Invalid option. Please choose from menu."
    );
}





    // ==========================================================
    // CREATE ORDER FLOW
    // ==========================================================

    private void handleCreateOrderFlow(
            IncomingMessage message,
            WhatsAppConversationEntity conversation
    ) {

        switch (conversation.getStep()) {

            case SELECT_BUYER:
                handleBuyerSelection(message, conversation);
                break;

            case SELECT_RICE_TYPE:
                handleRiceType(message, conversation);
                break;

            case ENTER_QUANTITY:
                handleQuantity(message, conversation);
                break;
            case SELECT_BRANCH:
                handleBranchSelection(message, conversation);
                break;


            default:
                log.warn("Unhandled step {} for manager {}",
                        conversation.getStep(), message.getMobile());
        }
    }

    // ==========================================================
    // STEP 1️⃣ SELECT BUYER
    // ==========================================================

    private void showBuyerList(String mobile) {

        List<Map<String, String>> rows =
                buyerRepository.findAll()
                        .stream()
                        .map(b -> Map.of(
                                "id", b.getId().toString(),
                                "title", b.getBuyerName()
                        ))
                        .toList();

        replyService.sendList(
                mobile,
                "Select Buyer",
                "Please select a buyer",
                "Buyers",
                rows
        );
    }


    private void handleBuyerSelection(
            IncomingMessage message,
            WhatsAppConversationEntity conversation
    ) {
        try {
            String buyerIdRaw =
                    message.getType() == IncomingMessage.MessageType.LIST
                            ? message.getSelectionId()
                            : message.getText();

            Long buyerId = Long.parseLong(buyerIdRaw);

            BuyerEntity buyer = buyerRepository.findById(buyerId)
                    .orElseThrow();

            conversationStateService.putContext(
                    conversation,
                    "buyerId",
                    buyer.getId()
            );

            conversationStateService.updateStep(
                    conversation,
                    ConversationStep.SELECT_RICE_TYPE
            );

            showRiceTypeList(message.getMobile());


//            replyService.sendText(
//                    message.getMobile(),
//                    "Please enter rice type"
//            );

        } catch (Exception e) {
            replyService.sendText(
                    message.getMobile(),
                    "❌ Invalid buyer selection. Please select again."
            );
        }
    }


    // ==========================================================
    // STEP 2️⃣ ENTER RICE TYPE
    // ==========================================================
    private void showRiceTypeList(String mobile) {

        List<Map<String, String>> rows = List.of(
                Map.of(
                        "id", "SONA_MASOORI",
                        "title", "Sona Masoori"
                ),
                Map.of(
                        "id", "BASMATI",
                        "title", "Basmati"
                ),
                Map.of(
                        "id", "PONNI",
                        "title", "Ponni"
                )
        );

        replyService.sendList(
                mobile,
                "Select Rice Type",
                "Please select the rice type",
                "Rice Types",
                rows
        );
    }


    private void handleRiceType(
            IncomingMessage message,
            WhatsAppConversationEntity conversation
    ) {

        try {
            String riceType =
                    message.getType() == IncomingMessage.MessageType.LIST
                            ? message.getSelectionId()
                            : message.getText().trim();

            conversationStateService.putContext(
                    conversation,
                    "riceType",
                    riceType
            );

            conversationStateService.updateStep(
                    conversation,
                    ConversationStep.ENTER_QUANTITY
            );

            replyService.sendText(
                    message.getMobile(),
                    "Please enter quantity (in tonnes)"
            );

        } catch (Exception e) {
            replyService.sendText(
                    message.getMobile(),
                    "❌ Please select a valid rice type"
            );
        }
    }


    // ==========================================================
    // STEP 3️⃣ ENTER QUANTITY (ORDER CREATION)
    // ==========================================================

    private void handleQuantity(
            IncomingMessage message,
            WhatsAppConversationEntity conversation
    ) {

        try {
            BigDecimal quantity =
                    new BigDecimal(message.getText().trim());

            // 1️⃣ Save quantity in context
            conversationStateService.putContext(
                    conversation,
                    "quantity",
                    quantity
            );

            // 2️⃣ Move to branch selection step
            conversationStateService.updateStep(
                    conversation,
                    ConversationStep.SELECT_BRANCH
            );

            // 3️⃣ Ask manager to select branch
            showBranchList(message.getMobile());

        } catch (Exception e) {
            log.warn(
                    "Invalid quantity '{}' from {}",
                    message.getText(),
                    message.getMobile()
            );

            /*
             * Later WhatsApp reply:
             * ❌ Please enter a valid quantity (e.g., 12.5)
             */
        }
    }

    private void showBranchList(String mobile) {

        List<Map<String, String>> rows =
                executorRepository.findDistinctBranches()
                        .stream()
                        .map(row -> {
                            String branchCode = row[0].toString();
                            String branchName = row[1].toString();

                            return Map.of(
                                    "id", branchCode,                    // backend
                                    "title", branchName,                 // visible
                                    "description", "Code: " + branchCode
                            );
                        })
                        .toList();

        replyService.sendList(
                mobile,
                "Select Branch",
                "Please select the branch for this order",
                "Branches",
                rows
        );
    }


    private void handleBranchSelection(
            IncomingMessage message,
            WhatsAppConversationEntity conversation
    ) {


        String branchCode =
                message.getType() == IncomingMessage.MessageType.LIST
                        ? message.getSelectionId()
                        : message.getText().trim();


        conversationStateService.putContext(
                conversation,
                "branchCode",
                branchCode
        );

        // ---- Read all context ----
        Long buyerId = Long.valueOf(
                conversationStateService
                        .getContext(conversation, "buyerId")
                        .toString()
        );
        BuyerEntity buyer = buyerRepository.findById(buyerId)
                .orElseThrow();

        String riceType =
                conversationStateService
                        .getContext(conversation, "riceType")
                        .toString();

        BigDecimal quantity =
                new BigDecimal(
                        conversationStateService
                                .getContext(conversation, "quantity")
                                .toString()
                );

        // ---- Create order ----
     OrderEntity order = orderService.createOrder(
                message.getMobile(),
                buyerId,
                riceType,
                quantity,
                branchCode
        );
        List<String> brachList = new ArrayList<>();
        brachList.add(branchCode);

        conversationStateService.clear(message.getMobile());

        log.info(
                "Order created successfully for manager {} in branch {}",
                message.getMobile(),
                branchCode
        );
        replyService.sendText(
                message.getMobile(),
                """
                ✅ Order created successfully!

                Rice Type : %s
                Quantity  : %s tons
                Buyer    : %s
                """.formatted(
                        riceType,
                        quantity,
                        buyer.getBuyerName()
                )
        );
        eventPublisher.publishEvent(new ContactCreatedEvent(this,brachList,order), NotificationEvent.BRANCH_NOTIFICATION);

    }

}
