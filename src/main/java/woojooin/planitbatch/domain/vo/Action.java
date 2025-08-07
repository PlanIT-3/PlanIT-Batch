package woojooin.planitbatch.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Action {
    private Long actionId;
    private Long objectId;
    private Long accountId;
    private Long memberProductId;
    private Integer allocatedRate;
    private Integer accountAllocatedRate;
    private String accountType;
    private Integer amount;
    private Boolean isDeleted;
}
