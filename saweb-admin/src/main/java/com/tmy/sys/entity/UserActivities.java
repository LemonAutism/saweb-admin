package com.tmy.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author tmy
 * @since 2024-02-20
 */
@TableName("x_user_activities")
public class UserActivities implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer userid;

    private Integer activityId;

    private Integer participationStatus;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }
    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }
    public Integer getParticipationStatus() {
        return participationStatus;
    }

    public void setParticipationStatus(Integer participationStatus) {
        this.participationStatus = participationStatus;
    }

    @Override
    public String toString() {
        return "UserActivities{" +
            "id=" + id +
            ", userid=" + userid +
            ", activityId=" + activityId +
            ", participationStatus=" + participationStatus +
        "}";
    }
}
