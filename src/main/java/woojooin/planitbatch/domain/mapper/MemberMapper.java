package woojooin.planitbatch.domain.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import woojooin.planitbatch.domain.vo.InvestmentRatio;

@Mapper
public interface MemberMapper {

    void updateInvestmentRatios(InvestmentRatio ratios);
    
    void updateInvestmentRatiosBulk(List<InvestmentRatio> ratioList);
}
