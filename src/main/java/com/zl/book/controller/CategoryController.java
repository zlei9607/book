package com.zl.book.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zl.book.model.Category;
import com.zl.book.service.ICategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;

@Controller
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private ICategoryService categoryService;

    @RequestMapping("/list")
    public String list() {
        return "category/list";
    }

    @RequestMapping("/page")
    @ResponseBody
    public Page<Category> page(@RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "5") int limit,
                               @RequestParam(required = false) String name,
                               @RequestParam(required = false) String sort,
                               @RequestParam(required = false) String order) {
        QueryWrapper<Category> qw = new QueryWrapper<Category>()
                .like(StringUtils.hasLength(name), "name", name);
        if (StringUtils.hasLength(sort) && "asc".equals(order)) {
            qw.orderByAsc(sort);
        } else if (StringUtils.hasLength(sort) && "desc".equals(order)) {
            qw.orderByDesc(sort);
        } else {
            qw.orderByDesc("create_time");
        }
        return categoryService.page(new Page<>(page, limit), qw);
    }

    @RequestMapping("/toAdd")
    public String toAdd() {
        return "category/add";
    }

    @RequestMapping("/add")
    @ResponseBody
    public void add(@RequestBody Category category) {
        categoryService.save(category);
    }

    @RequestMapping("/toEdit/{id}")
    public String toEdit(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryService.getById(id));
        return "category/edit";
    }

    @RequestMapping("/edit")
    @ResponseBody
    public void edit(@RequestBody Category category) {
        categoryService.updateById(category);
    }

    @RequestMapping("/del/{id}")
    @ResponseBody
    public void del(@PathVariable Long id) {
        categoryService.removeById(id);
    }

    @RequestMapping("/batchDel")
    @ResponseBody
    public void batchDel(@RequestBody Long[] ids) {
        categoryService.removeByIds(Arrays.asList(ids));
    }
}
