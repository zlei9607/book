package com.zl.book.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zl.book.common.Response;
import com.zl.book.model.Reader;
import com.zl.book.service.IReaderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;

@Controller
@RequestMapping("/reader")
public class ReaderController {

    @Resource
    private IReaderService readerService;

    @RequestMapping("/list")
    public String list() {
        return "reader/list";
    }

    @RequestMapping("/page")
    @ResponseBody
    public Page<Reader> page(@RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "10") int limit,
                             @RequestParam(required = false) String name,
                             @RequestParam(required = false) String cardNo,
                             @RequestParam(required = false) String status) {
        QueryWrapper<Reader> qw = new QueryWrapper<Reader>()
                .like(StringUtils.hasLength(name), "name", name)
                .like(StringUtils.hasLength(cardNo), "card_no", cardNo)
                .eq(StringUtils.hasLength(status), "status", status)
                .orderByDesc("create_time");
        return readerService.page(new Page<>(page, limit), qw);
    }

    @RequestMapping("/toAdd")
    public String toAdd() {
        return "reader/add";
    }

    @RequestMapping("/add")
    @ResponseBody
    public Response<?> add(@RequestBody Reader reader) {
        if (!StringUtils.hasLength(reader.getCardNo())) {
            return Response.error("卡号不能为空");
        }
        if (readerService.count(new QueryWrapper<Reader>().eq("card_no", reader.getCardNo())) > 0) {
            return Response.error("卡号已存在");
        }
        if (reader.getMaxBorrow() == null) {
            reader.setMaxBorrow(5);
        }
        if (!StringUtils.hasLength(reader.getStatus())) {
            reader.setStatus("1");
        }
        if (!StringUtils.hasLength(reader.getGender())) {
            reader.setGender("1");
        }
        readerService.save(reader);
        return Response.success();
    }

    @RequestMapping("/toEdit/{id}")
    public String toEdit(@PathVariable Long id, Model model) {
        model.addAttribute("reader", readerService.getById(id));
        return "reader/edit";
    }

    @RequestMapping("/edit")
    @ResponseBody
    public Response<?> edit(@RequestBody Reader reader) {
        Reader old = readerService.getById(reader.getId());
        if (old == null) {
            return Response.error("读者不存在");
        }
        if (StringUtils.hasLength(reader.getCardNo()) && !reader.getCardNo().equals(old.getCardNo())) {
            int exist = readerService.count(new QueryWrapper<Reader>()
                    .eq("card_no", reader.getCardNo())
                    .ne("id", reader.getId()));
            if (exist > 0) {
                return Response.error("卡号已存在");
            }
        }
        readerService.updateById(reader);
        return Response.success();
    }

    @RequestMapping("/del/{id}")
    @ResponseBody
    public Response<?> del(@PathVariable Long id) {
        readerService.removeById(id);
        return Response.success();
    }

    @RequestMapping("/batchDel")
    @ResponseBody
    public Response<?> batchDel(@RequestBody Long[] ids) {
        readerService.removeByIds(Arrays.asList(ids));
        return Response.success();
    }
}
