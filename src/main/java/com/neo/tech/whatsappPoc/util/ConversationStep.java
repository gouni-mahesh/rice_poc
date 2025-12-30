package com.neo.tech.whatsappPoc.util;

public enum ConversationStep {
    START,

    // Manager - Create Order
    SELECT_BUYER,
    SELECT_RICE_TYPE,   // ✅ instead of ENTER_RICE_TYPE

    ENTER_RICE_TYPE,
    ENTER_QUANTITY,
    SELECT_BRANCH,
    CONFIRM_ORDER,

    COMPLETED
}
