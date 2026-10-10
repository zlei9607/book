package com.zl.book.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zl.book.model.Book;
import com.zl.book.mapper.BookMapper;
import com.zl.book.service.IBookService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 图书信息表 服务实现类
 * </p>
 *
 * @author zhanglei
 * @since 2022-08-10
 */
@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements IBookService {

}
