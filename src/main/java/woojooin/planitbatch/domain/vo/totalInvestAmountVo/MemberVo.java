package woojooin.planitbatch.domain.vo.totalInvestAmountVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberVo {
    private Long memberId;
    private List<MemberProductVo> products;

}
