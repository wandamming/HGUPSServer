package com.hgups.express.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hgups.express.domain.EntryButton;
import com.hgups.express.domain.param.ButtonParam;
import com.hgups.express.domain.param.NameParam;
import com.hgups.express.domain.param.SortParam;
import com.hgups.express.domain.param.UserButtonParam;
import com.hgups.express.vo.EntryVo;
import jdk.internal.org.objectweb.asm.tree.analysis.Value;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lyx
 * @since 2021-07-26
 */
public interface EntryButtonMapper extends BaseMapper<EntryButton> {

    List<EntryVo> getEntry(@Param(value = "userId")Long id);

    List<EntryVo> getAllEntry(NameParam param);

    boolean insertEntry(@Param(value = "userId")Long id ,@Param(value = "param") ButtonParam param);

    boolean deleteEntry(@Param(value = "userId")Long id ,@Param(value = "param") ButtonParam param);

    List<EntryVo> getRecord(@Param(value = "userId")Long id);

    boolean deleteAllRecords(@Param(value = "userId")Long id);

    boolean updateRecords(UserButtonParam param);

    List<Integer> getWeight(@Param(value = "userId") Long id,@Param(value = "param") SortParam param);
    //boolean updateSort(@Param(value = "ids") List<Long> ids);
    boolean updateSort(@Param(value = "userId")Long id , Integer weight1,Integer weight2);
    boolean updateSort1(@Param(value = "userId")Long id,Integer weight1,Integer weight2);
    boolean updateChange(Integer weight1,@Param(value = "buttonId") Long buttonId,@Param(value = "userId") Long id);
    Integer getW(@Param(value = "userId") Long uId,@Param(value = "buttonId")Long bId);

}
