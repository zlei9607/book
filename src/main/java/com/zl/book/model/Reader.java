package com.zl.book.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("t_reader")
public class Reader implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 借阅卡号 */
    private String cardNo;

    private String name;

    private String phone;

    /** 0女 1男 */
    private String gender;

    /** 1正常 0冻结 */
    private String status;

    /** 最大可借册数 */
    private Integer maxBorrow;

    private Date createTime;

    private Date updateTime;
}
