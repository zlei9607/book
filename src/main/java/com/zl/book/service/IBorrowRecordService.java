package com.zl.book.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zl.book.common.Response;
import com.zl.book.model.BorrowRecord;

public interface IBorrowRecordService extends IService<BorrowRecord> {

    /**
     * 借书（可指定借阅天数，为空则用默认 30 天）
     */
    Response<?> borrowBook(Long readerId, Long bookId, Integer borrowDays);

    /**
     * 还书
     */
    Response<?> returnBook(Long recordId);

    /**
     * 刷新逾期状态
     */
    void refreshOverdueStatus();
}
