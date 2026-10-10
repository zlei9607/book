package com.zl.book.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zl.book.common.Response;
import com.zl.book.model.Book;
import com.zl.book.model.BorrowRecord;
import com.zl.book.model.Reader;
import com.zl.book.service.IBookService;
import com.zl.book.service.IBorrowRecordService;
import com.zl.book.service.IReaderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/borrow")
public class BorrowController {

    @Resource
    private IBorrowRecordService borrowRecordService;

    @Resource
    private IReaderService readerService;

    @Resource
    private IBookService bookService;

    @RequestMapping("/list")
    public String list() {
        return "borrow/list";
    }

    @RequestMapping("/page")
    @ResponseBody
    public Page<BorrowRecord> page(@RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int limit,
                                   @RequestParam(required = false) String status,
                                   @RequestParam(required = false) String readerName,
                                   @RequestParam(required = false) String cardNo,
                                   @RequestParam(required = false) String bookName) {
        borrowRecordService.refreshOverdueStatus();

        QueryWrapper<BorrowRecord> qw = new QueryWrapper<>();
        qw.eq(StringUtils.hasLength(status), "status", status);

        if (StringUtils.hasLength(readerName) || StringUtils.hasLength(cardNo)) {
            QueryWrapper<Reader> readerQw = new QueryWrapper<>();
            readerQw.like(StringUtils.hasLength(readerName), "name", readerName);
            readerQw.like(StringUtils.hasLength(cardNo), "card_no", cardNo);
            List<Long> readerIds = readerService.list(readerQw)
                    .stream().map(Reader::getId).collect(Collectors.toList());
            if (readerIds.isEmpty()) {
                return emptyPage(page, limit);
            }
            qw.in("reader_id", readerIds);
        }
        if (StringUtils.hasLength(bookName)) {
            List<Long> bookIds = bookService.list(new QueryWrapper<Book>().like("name", bookName))
                    .stream().map(Book::getId).collect(Collectors.toList());
            if (bookIds.isEmpty()) {
                return emptyPage(page, limit);
            }
            qw.in("book_id", bookIds);
        }

        qw.orderByDesc("borrow_time");
        Page<BorrowRecord> pageData = borrowRecordService.page(new Page<>(page, limit), qw);
        pageData.getRecords().forEach(this::fillExtra);
        return pageData;
    }

    @RequestMapping("/return/{id}")
    @ResponseBody
    public Response<?> returnBook(@PathVariable Long id) {
        return borrowRecordService.returnBook(id);
    }

    @RequestMapping("/toManual")
    public String toManual(Model model) {
        model.addAttribute("readers", readerService.list(new QueryWrapper<Reader>().eq("status", "1")));
        model.addAttribute("books", bookService.list(new QueryWrapper<Book>()
                .eq("status", "1")
                .gt("available_count", 0)));
        return "borrow/manual";
    }

    @RequestMapping("/manual")
    @ResponseBody
    public Response<?> manual(@RequestBody Map<String, Object> body) {
        if (body.get("readerId") == null || body.get("bookId") == null) {
            return Response.error("请选择读者和图书");
        }
        Long readerId = Long.valueOf(String.valueOf(body.get("readerId")));
        Long bookId = Long.valueOf(String.valueOf(body.get("bookId")));
        Integer borrowDays = null;
        if (body.get("borrowDays") != null && StringUtils.hasLength(String.valueOf(body.get("borrowDays")))) {
            borrowDays = Integer.valueOf(String.valueOf(body.get("borrowDays")));
        }
        return borrowRecordService.borrowBook(readerId, bookId, borrowDays);
    }

    private Page<BorrowRecord> emptyPage(int page, int limit) {
        Page<BorrowRecord> empty = new Page<>(page, limit);
        empty.setRecords(Collections.emptyList());
        empty.setTotal(0);
        return empty;
    }

    private void fillExtra(BorrowRecord record) {
        Reader reader = readerService.getById(record.getReaderId());
        if (reader != null) {
            record.setReaderName(reader.getName());
            record.setReaderCardNo(reader.getCardNo());
        }
        Book book = bookService.getById(record.getBookId());
        if (book != null) {
            record.setBookName(book.getName());
            record.setBookCover(book.getCover());
        }
    }
}
