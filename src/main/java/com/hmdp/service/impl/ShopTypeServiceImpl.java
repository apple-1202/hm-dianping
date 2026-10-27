package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.RedisConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public List<ShopType> queryTypeList() {
        //1.查询Redis，使用常量
        String jsonStr = stringRedisTemplate.opsForValue().get(RedisConstants.CACHE_SHOP_TYPE_LIST_KEY);
        if (StrUtil.isNotBlank(jsonStr)) {
            // 缓存命中，json字符串转为List集合直接返回
            List<ShopType> typeList = JSONUtil.toList(jsonStr, ShopType.class);
            return typeList;
        }
        //2.缓存没命中，查询数据库
        List<ShopType> typeList = query().orderByAsc("sort").list();
        //3.存入Redis，设置过期时间，读取常量TTL
        stringRedisTemplate.opsForValue()
                .set(RedisConstants.CACHE_SHOP_TYPE_LIST_KEY,
                        JSONUtil.toJsonStr(typeList),
                        RedisConstants.CACHE_SHOP_TYPE_TTL,
                        TimeUnit.MINUTES);
        return typeList;
    }


}
