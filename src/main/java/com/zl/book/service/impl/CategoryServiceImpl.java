package com.zl.book.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zl.book.model.Category;
import com.zl.book.mapper.CategoryMapper;
import com.zl.book.service.ICategoryService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 图书分类信息表 服务实现类
 * </p>
 *
 * @author zhanglei
 * @since 2022-08-10
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

}
