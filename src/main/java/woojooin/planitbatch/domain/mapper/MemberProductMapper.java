package woojooin.planitbatch.domain.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import woojooin.planitbatch.domain.vo.MemberProduct;
import woojooin.planitbatch.domain.vo.totalInvestAmountVo.MemberVo;

@Mapper
public interface MemberProductMapper {
    List<MemberProduct> getMemberProductsByMemberIds(@Param("memberIds") List<Long> memberIds);
    List<MemberVo> selectMemberWithProducts(Map<String, Object> params);

}