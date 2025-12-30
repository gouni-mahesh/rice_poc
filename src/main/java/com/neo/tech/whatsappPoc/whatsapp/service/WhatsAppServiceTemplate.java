package com.neo.tech.whatsappPoc.whatsapp.service;

import com.neo.tech.whatsappPoc.executor.entity.ExecutorEntity;
import com.neo.tech.whatsappPoc.executor.repository.ExecutorRepository;
import com.neo.tech.whatsappPoc.order.entity.OrderEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WhatsAppServiceTemplate {
    private static final String WHATSAPP_URL = "https://graph.facebook.com/v22.0/703654132841027/messages";
    private static final String ACCESS_TOKEN = "EAAWl4pZCZCZCggBPZAAisRiGYHvsJ6qzakPJjUBHKMwRBLTMg58VtYTAXV9iJtvwm20YX9oGkrei2Fkgsmrf2GcRe7hjjYm9UlXvx2PXfWdCZCawQYgoorcLNoJ4o2f2o9nvqUJUDrpHCkz1hWZBuuGIZCBm66mZCoMLYwI7eCQxXfz4cjWIOUZABMySGhyEtZAKv5bAZDZD";
    private final ExecutorRepository executorRepository;

    public void sendTemplateMessage(String phoneNumber, String templateName, ExecutorEntity contact , OrderEntity order) {
        try {
            log.info("Sending WhatsApp template message to {} using template {}", phoneNumber, templateName);
            URL url = new URL(WHATSAPP_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + ACCESS_TOKEN);
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            // Extract contact info or set empty strings
            String name = "";
            String email = "";
            String mobile = "";
            String service = "";
            if (contact != null) {
                log.info("Contact details found: {}", contact);
                name = contact.getName() != null ? contact.getName() : "";
//                email = contact.getEmail() != null ? contact.getEmail() : "";
//                mobile = contact.getMobileNumber() != null ? contact.getMobileNumber() : "";
                service = order.getBuyer().getBuyerName() + " - " + order.getId()+" qty: "+order.getTotalQuantity();

            }
//            String jsonInputString = "{"
//                    + "\"messaging_product\": \"whatsapp\","
//                    + "\"to\": \"" + phoneNumber + "\","
//                    + "\"type\": \"template\","
//                    + "\"template\": {"
//                    + "  \"name\": \"" + templateName + "\","
//                    + "  \"language\": {\"code\": \"en_IN\"},"
//                    + "  \"components\": [{"
//                    + "    \"type\": \"body\","
//                    + "    \"parameters\": ["
//                    + "      {\"type\": \"text\", \"text\": \"" + name + "\"},"
//                    + "      {\"type\": \"text\", \"text\": \"" + email + "\"},"
//                    + "      {\"type\": \"text\", \"text\": \"" + mobile + "\"},"
//                    + "      {\"type\": \"text\", \"text\": \"" + service + "\"}"
//                    + "    ]"
//                    + "  }]"
//                    + "}"
//                    + "}";
            String jsonInputString = "{"
                    + "\"messaging_product\": \"whatsapp\","
                    + "\"to\": \"" + phoneNumber + "\","
                    + "\"type\": \"template\","
                    + "\"template\": {"
                    + "  \"name\": \"" + templateName + "\","
                    + "  \"language\": {\"code\": \"en_US\"}"
                    + "}"
                    + "}";
            try (OutputStream os = con.getOutputStream()) {
                log.info("WhatsApp API Request Payload: {}", jsonInputString);
                os.write(jsonInputString.getBytes(StandardCharsets.UTF_8));
            }
            int responseCode = con.getResponseCode();
            log.info("WhatsApp API Response Code: {}", responseCode);
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    responseCode == 200 ? con.getInputStream() : con.getErrorStream(), StandardCharsets.UTF_8))) {

                String line;
                while ((line = br.readLine()) != null) {
                    log.info("WhatsApp API Response Line: {}", line);
                    response.append(line.trim());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error while sending WhatsApp message: {}", e.getMessage(), e);
            e.getMessage();
        }
    }

    public void sendFirstPendingContactMessage(String templateName, List<String> branchCodes , OrderEntity order) {

        List<ExecutorEntity> adminContacts =
                branchCodes.stream()
                        .flatMap(code -> executorRepository
                                .findAllByBranchCode(code)
                                .stream())
                        .toList();
        log.info("Found {} admin contacts for branches {}", adminContacts.size(), branchCodes);

        for (ExecutorEntity adminContact : adminContacts) {
            log.info("Sending WhatsApp message to admin contact: {}", adminContact.getUser().getMobileNumber());
            sendTemplateMessage(adminContact.getUser().getMobileNumber(), templateName, adminContact ,order);
            // Update messageSent flag after successful send


        }
    }
}
