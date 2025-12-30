package com.neo.tech.whatsappPoc.buyer.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BuyerRequestDTO {

    private String buyerName;
    private String buyerCode;
    private String mobileNumber;
}

