package com.zl.book.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("t_book")
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String isbn;

    private String name;

    private String cover;

    private Long categoryId;

    private String author;

    private String press;

    private String description;

    /** 单价（分） */
    private Long price;

    /** 馆藏总数 */
    private Integer count;

    /** 可借数量 */
    private Integer availableCount;

    /** 1上架 0下架 */
    private String status;

    private Date createTime;

    private Date updateTime;

    @TableField(exist = false)
    private String categoryName;
}
