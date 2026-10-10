package com.zl.book.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zl.book.model.Book;
import com.zl.book.model.Category;
import com.zl.book.service.IBookService;
import com.zl.book.service.ICategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/book")
public class BookController {

    public static final String DEFAULT_COVER = "/file/5793c405e8ef467c929519b39d9d92dc.png";

    @Resource
    private IBookService bookService;

    @Resource
    private ICategoryService categoryService;

    @RequestMapping("/list")
    public String list(Model model) {
        model.addAttribute("categoryList", categoryService.list());
        return "book/list";
    }

    @RequestMapping("/page")
    @ResponseBody
    public Page<Book> page(@RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "5") int limit,
                           @RequestParam(required = false) String sort,
                           @RequestParam(required = false) String order,
                           @RequestParam(required = false) String name,
                           @RequestParam(required = false) String categoryId) {
        QueryWrapper<Book> qw = new QueryWrapper<Book>()
                .eq(StringUtils.hasLength(categoryId), "category_id", categoryId)
                .like(StringUtils.hasLength(name), "name", name);
        if (StringUtils.hasLength(sort) && "asc".equals(order)) {
            qw.orderByAsc(sort);
        } else if (StringUtils.hasLength(sort) && "desc".equals(order)) {
            qw.orderByDesc(sort);
        } else {
            qw.orderByDesc("create_time");
        }
        Page<Book> pageData = bookService.page(new Page<>(page, limit), qw);
        pageData.getRecords().forEach(book -> {
            Category category = categoryService.getById(book.getCategoryId());
            if (category != null) {
                book.setCategoryName(category.getName());
            }
        });
        return pageData;
    }

    @RequestMapping("/toAdd")
    public String toAdd(Model model) {
        model.addAttribute("categoryList", categoryService.list());
        return "book/add";
    }

    @RequestMapping("/add")
    @ResponseBody
    public void add(@RequestBody Book book) {
        fillDefaults(book);
        bookService.save(book);
    }

    @RequestMapping("/toEdit/{id}")
    public String toEdit(@PathVariable Long id, Model model) {
        model.addAttribute("book", bookService.getById(id));
        model.addAttribute("categoryList", categoryService.list());
        return "book/edit";
    }

    @RequestMapping("/edit")
    @ResponseBody
    public void edit(@RequestBody Book book) {
        if (!StringUtils.hasLength(book.getCover())) {
            book.setCover(DEFAULT_COVER);
        }
        bookService.updateById(book);
    }

    @RequestMapping("/del/{id}")
    @ResponseBody
    public void del(@PathVariable Long id) {
        bookService.removeById(id);
    }

    @RequestMapping("/batchDel")
    @ResponseBody
    public void batchDel(@RequestBody Long[] ids) {
        bookService.removeByIds(Arrays.asList(ids));
    }

    private void fillDefaults(Book book) {
        if (!StringUtils.hasLength(book.getCover())) {
            book.setCover(DEFAULT_COVER);
        }
        if (book.getCount() == null) {
            book.setCount(1);
        }
        if (book.getAvailableCount() == null) {
            book.setAvailableCount(book.getCount());
        }
        if (!StringUtils.hasLength(book.getStatus())) {
            book.setStatus("1");
        }
        if (book.getPrice() == null) {
            book.setPrice(0L);
        }
    }
}
