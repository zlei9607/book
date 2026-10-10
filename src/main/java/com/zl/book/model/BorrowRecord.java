package com.zl.book.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("t_borrow_record")
public class BorrowRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String STATUS_BORROWING = "0";
    public static final String STATUS_RETURNED = "1";
    public static final String STATUS_OVERDUE = "2";

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long readerId;

    private Long bookId;

    private Date borrowTime;

    private Date dueTime;

    private Date returnTime;

    /** 0借阅中 1已归还 2逾期 */
    private String status;

    private String remark;

    private Date createTime;

    private Date updateTime;

    @TableField(exist = false)
    private String readerName;

    @TableField(exist = false)
    private String readerCardNo;

    @TableField(exist = false)
    private String bookName;

    @TableField(exist = false)
    private String bookCover;
}
