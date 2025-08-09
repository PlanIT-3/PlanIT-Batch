package woojooin.planitbatch.domain.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import woojooin.planitbatch.domain.vo.InvestmentRatio;
import woojooin.planitbatch.domain.vo.Member;

@Mapper
public interface MemberMapper {

	void updateInvestmentRatios(InvestmentRatio ratios);

	void updateInvestmentRatiosBulk(List<InvestmentRatio> ratioList);

	List<Member> getMembersPaginated(Map<String, Object> params);
}
