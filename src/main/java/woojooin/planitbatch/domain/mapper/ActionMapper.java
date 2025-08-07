package woojooin.planitbatch.domain.mapper;

import java.math.BigInteger;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import woojooin.planitbatch.domain.vo.Action;

@Mapper
public interface ActionMapper {
    List<Action> getActionsByObjectId(@Param("objectId") BigInteger objectId);
    List<Action> getActionsByObjectIds(@Param("objectIds") List<Long> objectIds);
}