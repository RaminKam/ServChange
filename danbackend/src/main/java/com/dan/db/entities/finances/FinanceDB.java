package com.dan.db.entities.finances;

import com.dan.db.entities.audit.DateAudit;
import com.dan.db.entities.auth.UserDB;
import com.dan.db.entities.services.DealDB;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "finances")
public class FinanceDB extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deal_id")
    private DealDB deal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserDB user;

    @NotNull
    private Float balance;
    private Float  credit,delta,blocked;
    @NotNull
    @Enumerated(EnumType.STRING)
    private OperationKind kind;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DealDB getDeal() {
        return deal;
    }

    public void setDeal(DealDB deal) {
        this.deal = deal;
    }

    public UserDB getUser() {
        return user;
    }

    public void setUser(UserDB user) {
        this.user = user;
    }

    public Float getBalance() {
        return balance;
    }

    public void setBalance(Float balance) {
        this.balance = balance;
    }

    public Float getDelta() {
        return delta;
    }

    public void setDelta(Float delta) {
        this.delta = delta;
    }

    public Float getBlocked() {
        return blocked;
    }

    public void setBlocked(Float blocked) {
        this.blocked = blocked;
    }

    public Float getCredit() {
        return credit;
    }

    public void setCredit(Float credit) {
        this.credit = credit;
    }

    public OperationKind getKind() {
        return kind;
    }

    public void setKind(OperationKind kind) {
        this.kind = kind;
    }
}
