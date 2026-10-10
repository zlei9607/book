package com.zl.book.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zl.book.model.Book;
import com.zl.book.model.BorrowRecord;
import com.zl.book.model.Category;
import com.zl.book.service.IBookService;
import com.zl.book.service.IBorrowRecordService;
import com.zl.book.service.ICategoryService;
import com.zl.book.service.IReaderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class IndexController {

    @Resource
    private IBookService bookService;

    @Resource
    private ICategoryService categoryService;

    @Resource
    private IReaderService readerService;

    @Resource
    private IBorrowRecordService borrowRecordService;

    @RequestMapping("/index")
    public String index(Model model) {
        borrowRecordService.refreshOverdueStatus();

        List<Category> categoryList = categoryService.list();
        List<Map<String, Object>> data = new ArrayList<>();
        for (Category category : categoryList) {
            int count = bookService.count(new QueryWrapper<Book>().lambda()
                    .eq(Book::getCategoryId, category.getId()));
            Map<String, Object> map = new HashMap<>(2);
            map.put("name", category.getName());
            map.put("value", count);
            data.add(map);
        }
        model.addAttribute("data", data);

        model.addAttribute("bookTotal", bookService.count());
        model.addAttribute("readerTotal", readerService.count());
        model.addAttribute("borrowingTotal", borrowRecordService.count(new LambdaQueryWrapper<BorrowRecord>()
                .in(BorrowRecord::getStatus, BorrowRecord.STATUS_BORROWING, BorrowRecord.STATUS_OVERDUE)));
        model.addAttribute("overdueTotal", borrowRecordService.count(new LambdaQueryWrapper<BorrowRecord>()
                .eq(BorrowRecord::getStatus, BorrowRecord.STATUS_OVERDUE)));
        model.addAttribute("availableTotal", bookService.list(new QueryWrapper<Book>().select("available_count"))
                .stream()
                .mapToInt(b -> b.getAvailableCount() == null ? 0 : b.getAvailableCount())
                .sum());

        return "index";
    }

    @RequestMapping("/")
    public String root() {
        return "redirect:/login";
    }
}
