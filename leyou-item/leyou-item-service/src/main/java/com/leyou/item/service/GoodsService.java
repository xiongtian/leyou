package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author xiongtian
 * @create 2020/8/24-16:17
 */
@Service
public class GoodsService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    /**
     * 根据条件分页查询spu
     *
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */

    public PageResult<SpuBo> querySpuBoBypage(String key, Boolean saleable, Integer page, Integer rows) {

        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        // 1.添加查询条件
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }

        // 2.添加剂上下架的过滤条件
        if (null != saleable) {
            criteria.andEqualTo("saleable", saleable);
        }

        // 3.添加分页
        PageHelper.startPage(page, rows);

        // 4.执行查询，获取spu集合
        List<Spu> spus = this.spuMapper.selectByExample(example);
        PageInfo<Spu> pageInfo = new PageInfo<>(spus);

        // 5.spu集合转化成spubo集合
        List<SpuBo> spuBos = spus.stream().map(spu -> {
            SpuBo spuBo = new SpuBo();
            BeanUtils.copyProperties(spu, spuBo);
            // 查询品牌名称
            Brand brand = this.brandMapper.selectByPrimaryKey(spu.getBrandId());
            spuBo.setBname(brand.getName());
            // 查询分类名称

            List<String> names = this.categoryService.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

            spuBo.setCname(StringUtils.join(names, "-"));
            return spuBo;
        }).collect(Collectors.toList());

        // 6.返回pageResult<spuBO>
        return new PageResult<>(pageInfo.getTotal(), page, spuBos);
    }

    /**
     * 新增商品
     *
     * @param spuBo
     * @return
     */
    @Transactional
    public void saveGoods(SpuBo spuBo) {
        // 1、先新增spu
        // 设置默认字段，防止恶意注入
        spuBo.setId(null);
        spuBo.setValid(true);
        spuBo.setValid(true);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        this.spuMapper.insertSelective(spuBo);

        // 2、再去新增spuDetail
        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        this.spuDetailMapper.insertSelective(spuDetail);

        saveSkuAndStock(spuBo);

    }

    /**
     * 新增sku和stock
     *
     * @param spuBo
     */
    private void saveSkuAndStock(SpuBo spuBo) {
        spuBo.getSkus().forEach(sku -> {
            // 3、新增sku
            sku.setId(null);
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            this.skuMapper.insertSelective(sku);
            ///4、新增stock
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            this.stockMapper.insert(stock);
        });
    }

    /**
     * 根据spuId查询spuDetail
     *
     * @param spuId
     * @return
     */
    public SpuDetail querySpuDetailBySpuId(Long spuId) {
        return this.spuDetailMapper.selectByPrimaryKey(spuId);
    }

    /**
     * 根据spuId查询sku的集合
     *
     * @param spuId
     * @return
     */
    public List<Sku> querySkusBySpuId(Long spuId) {

        Sku record = new Sku();
        record.setSpuId(spuId);
        List<Sku> skus = this.skuMapper.select(record);
        skus.forEach(sku -> {
            Stock stock = this.stockMapper.selectByPrimaryKey(sku.getId());
            sku.setStock(stock.getStock());
        });
        return skus;
    }

    /**
     * 跟新商品信息
     *
     * @param spuBo
     * @return
     */
    @Transactional
    public void updateGoods(SpuBo spuBo) {
        // 1、操作sku相关的
        // 根据spu的id查询需要删除的sku
        Sku record = new Sku();
        record.setSpuId(spuBo.getId());
        List<Sku> skus = this.skuMapper.select(record);
        skus.forEach(sku -> {
            // 1.1删除stock
            this.stockMapper.deleteByPrimaryKey(sku.getId());
        });

        // 1.2 删除sku
        Sku sku = new Sku();
        sku.setSpuId(spuBo.getId());
        this.skuMapper.delete(sku);
        // 1.3 新增sku
        // 1.4新增stock
        this.saveSkuAndStock(spuBo);
        // 2、更新spu和spuDetail

        //防止时间被恶意篡改
        spuBo.setCreateTime(null);
        spuBo.setLastUpdateTime(new Date());

        // 防止被篡改上下架的属性
        spuBo.setValid(null);
        spuBo.setSaleable(null);

        this.spuMapper.updateByPrimaryKeySelective(spuBo);
        this.spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());
    }
}
