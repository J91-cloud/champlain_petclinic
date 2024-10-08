package com.petclinic.inventoryservice.presentationlayer;

import com.petclinic.inventoryservice.datalayer.Supply.Status;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SupplyResponseDTO {
    private String supplyId;
    private String inventoryId;
    private String supplyName;
    private String supplyDescription;
    private Double supplyPrice;
    private Integer supplyQuantity;
    private Double supplySalePrice;
    private Status status;
}

