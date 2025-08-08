package woojooin.planitbatch.domain.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import woojooin.planitbatch.domain.vo.MemberProduct;

@Mapper
public interface MemberProductMapper {
    List<MemberProduct> getMemberProductsByMemberIds(@Param("memberIds") List<Long> memberIds);
}