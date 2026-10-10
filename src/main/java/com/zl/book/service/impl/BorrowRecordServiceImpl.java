package com.zl.book.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zl.book.common.Response;
import com.zl.book.mapper.BorrowRecordMapper;
import com.zl.book.model.Book;
import com.zl.book.model.BorrowRecord;
import com.zl.book.model.Reader;
import com.zl.book.service.IBookService;
import com.zl.book.service.IBorrowRecordService;
import com.zl.book.service.IReaderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class BorrowRecordServiceImpl extends ServiceImpl<BorrowRecordMapper, BorrowRecord> implements IBorrowRecordService {

    public static final int DEFAULT_BORROW_DAYS = 30;

    @Resource
    private IBookService bookService;

    @Resource
    private IReaderService readerService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<?> borrowBook(Long readerId, Long bookId, Integer borrowDays) {
        int days = (borrowDays == null || borrowDays <= 0) ? DEFAULT_BORROW_DAYS : borrowDays;
        if (days > 365) {
            return Response.error("借阅天数不能超过 365 天");
        }

        Reader reader = readerService.getById(readerId);
        if (reader == null) {
            return Response.error("读者不存在");
        }
        if (!"1".equals(reader.getStatus())) {
            return Response.error("借阅卡已冻结");
        }

        Book book = bookService.getById(bookId);
        if (book == null) {
            return Response.error("图书不存在");
        }
        if (!"1".equals(book.getStatus())) {
            return Response.error("图书已下架");
        }
        if (book.getAvailableCount() == null || book.getAvailableCount() <= 0) {
            return Response.error("该图书暂无可借库存");
        }

        int currentBorrow = this.count(new LambdaQueryWrapper<BorrowRecord>()
                .eq(BorrowRecord::getReaderId, readerId)
                .in(BorrowRecord::getStatus, BorrowRecord.STATUS_BORROWING, BorrowRecord.STATUS_OVERDUE));
        int maxBorrow = reader.getMaxBorrow() == null ? 5 : reader.getMaxBorrow();
        if (currentBorrow >= maxBorrow) {
            return Response.error("已达最大可借册数（" + maxBorrow + "册）");
        }

        int sameBook = this.count(new LambdaQueryWrapper<BorrowRecord>()
                .eq(BorrowRecord::getReaderId, readerId)
                .eq(BorrowRecord::getBookId, bookId)
                .in(BorrowRecord::getStatus, BorrowRecord.STATUS_BORROWING, BorrowRecord.STATUS_OVERDUE));
        if (sameBook > 0) {
            return Response.error("该读者已借阅该书且尚未归还");
        }

        boolean stockOk = bookService.update(new LambdaUpdateWrapper<Book>()
                .eq(Book::getId, bookId)
                .gt(Book::getAvailableCount, 0)
                .setSql("available_count = available_count - 1"));
        if (!stockOk) {
            return Response.error("借阅失败，库存不足");
        }

        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.DAY_OF_MONTH, days);

        BorrowRecord record = new BorrowRecord();
        record.setReaderId(readerId);
        record.setBookId(bookId);
        record.setBorrowTime(now);
        record.setDueTime(cal.getTime());
        record.setStatus(BorrowRecord.STATUS_BORROWING);
        record.setRemark("借期" + days + "天");
        this.save(record);
        return Response.success(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<?> returnBook(Long recordId) {
        BorrowRecord record = this.getById(recordId);
        if (record == null) {
            return Response.error("借阅记录不存在");
        }
        if (BorrowRecord.STATUS_RETURNED.equals(record.getStatus())) {
            return Response.error("该记录已归还");
        }

        record.setReturnTime(new Date());
        record.setStatus(BorrowRecord.STATUS_RETURNED);
        this.updateById(record);

        bookService.update(new LambdaUpdateWrapper<Book>()
                .eq(Book::getId, record.getBookId())
                .setSql("available_count = available_count + 1"));
        return Response.success();
    }

    @Override
    public void refreshOverdueStatus() {
        Date now = new Date();
        List<BorrowRecord> list = this.list(new LambdaQueryWrapper<BorrowRecord>()
                .eq(BorrowRecord::getStatus, BorrowRecord.STATUS_BORROWING)
                .lt(BorrowRecord::getDueTime, now));
        for (BorrowRecord record : list) {
            record.setStatus(BorrowRecord.STATUS_OVERDUE);
            record.setRemark("已逾期");
            this.updateById(record);
        }
    }
}
