package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author xiongtian
 * @create 2020/4/2-9:03
 */
public interface BrandMapper extends Mapper<Brand> {

    @Insert("INSERT INTO tb_category_brand (category_id,brand_id) values (#{cid},#{bid})")
    void insertCategoryAndBrand(@Param("cid") Long cid, @Param("bid") Long id);

    @Select("select *\n" +
            "from\n" +
            "tb_brand as a\n" +
            "inner join tb_category_brand as b on a.id=b.brand_id\n" +
            "where\n" +
            "b.category_id=#{cid}")
    List<Brand> selectBrandsByCid(Long cid);
}
