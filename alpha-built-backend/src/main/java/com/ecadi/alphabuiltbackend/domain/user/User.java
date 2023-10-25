package com.ecadi.alphabuiltbackend.domain.user;

import io.netty.channel.ChannelHandlerContext;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;


import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.hibernate.Hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a User entity in the system.
 * Each User is identified by a unique userId and is associated with a projectId.
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "users")
@NoArgsConstructor
public class User {

    /**
     * The primary key for the User entity.
     * Generated automatically by the persistence framework.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * The unique ID of the user.
     */
    @Column(name = "user_id", nullable = false)
    private int userId;

    /**
     * The ID of the project that the user is associated with.
     */
    @Column(name = "project_id", nullable = false)
    private int projectId;

    /**
     * The ChannelHandlerContext associated with the user.
     * This field is not persisted in the database.
     */
    @Transient
    private ChannelHandlerContext channelHandlerContext;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<ActionLog> actionLogList;

    /**
     * Returns the ID of the user.
     *
     * @return The ID of the user.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the user.
     *
     * @param id The new ID of the user.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Constructs a new User with the specified userId, projectId, and ChannelHandlerContext.
     *
     * @param userId The unique ID of the user.
     * @param projectId The ID of the project that the user is associated with.
     * @param channelHandlerContext The ChannelHandlerContext associated with the user.
     */
    public User(int userId, int projectId, ChannelHandlerContext channelHandlerContext) {
        this.userId = userId;
        this.projectId = projectId;
        this.channelHandlerContext = channelHandlerContext;
        actionLogList = new ArrayList<>();
    }

    /**
     * Constructs a new User with the specified userId and projectId.
     *
     * @param userId The unique ID of the user.
     * @param projectId The ID of the project that the user is associated with.
     */
    public User(int userId, int projectId) {
        this.userId = userId;
        this.projectId = projectId;
        this.channelHandlerContext = null;
        actionLogList = new ArrayList<>();
    }

    /**
     * Compares this User to the specified object.
     * The result is true if and only if the argument is not null and
     *      is a User object that represents the same user ID as this object.
     *
     * @param o The object to compare this User against.
     * @return true if the given object represents a User equivalent to this User, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        } else {
            User user = (User) o;
            return Objects.equals(id, user.id)
                    && Objects.equals(userId, user.userId)
                    && Objects.equals(projectId, user.projectId)
                    && Objects.equals(channelHandlerContext, user.channelHandlerContext);
        }
    }

    /**
     * Adds a new ActionLog entry to this user's list of action logs.
     *
     * @param newActionLog The ActionLog to add.
     */
    public void appendNewAction(ActionLog newActionLog) {
        actionLogList.add(newActionLog);
    }


    /**
     * Returns a hash code for this User.
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * Clears all entries from this user's list of action logs.
     */
    public void clearActionLogs() {
        actionLogList.clear();
    }

    /**
     * Clear all actions with stale timestamp.
     *
     * @param time the maximum timestamp.
     * */
    public void clearStaleActionLogs(int time) {
        actionLogList.removeIf(actionLog -> actionLog.getTimestamp() <= time);
    }
}

