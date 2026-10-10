package com.zl.book.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zl.book.mapper.ReaderMapper;
import com.zl.book.model.Reader;
import com.zl.book.service.IReaderService;
import org.springframework.stereotype.Service;

@Service
public class ReaderServiceImpl extends ServiceImpl<ReaderMapper, Reader> implements IReaderService {
}
