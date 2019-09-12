package org.mengyun.tcctransaction;


import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.api.TransactionStatus;
import org.mengyun.tcctransaction.api.TransactionXid;
import org.mengyun.tcctransaction.common.TransactionType;

import javax.transaction.xa.Xid;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by changmingxie on 10/26/15.
 */
public class Transaction implements Serializable {

    private static final long serialVersionUID = 7291423944314337931L;
    /**
     * 全局事务ID
     */
    private TransactionXid xid;
    /**
     * 事务的状态，可以为TRYING，CONFIRMING，CANCELLING，分别对应tcc三个阶段
     */
    private TransactionStatus status;
    /**
     * 事务类型，可以为ROOT，BRANCH，ROOT是主事务，BRANCH是分支事务，事务的发起方法对应主事务，其他被调用的dubbo接口在分支事务上
     */
    private TransactionType transactionType;
    /**
     * 事务重试次数，当confirm或者cancel失败重试时增加
     */
    private volatile int retriedCount = 0;
    /**
     * 事务的创建时间
     */
    private Date createTime = new Date();
    /**
     * 事务最后一次更新时间
     */
    private Date lastUpdateTime = new Date();
    /**
     * 事务的版本号，乐观锁？
     */
    private long version = 1;
    /**
     * 事务的参与者
     */
    private List<Participant> participants = new ArrayList<Participant>();
    /**
     * 附加参数，暂时没发现被用到
     */
    private Map<String, Object> attachments = new ConcurrentHashMap<String, Object>();

    public Transaction() {

    }

    public Transaction(TransactionContext transactionContext) {
        this.xid = transactionContext.getXid();
        this.status = TransactionStatus.TRYING;
        this.transactionType = TransactionType.BRANCH;
    }

    public Transaction(TransactionType transactionType) {
        this.xid = new TransactionXid();
        this.status = TransactionStatus.TRYING;
        this.transactionType = transactionType;
    }

    public Transaction(Object uniqueIdentity,TransactionType transactionType) {

        this.xid = new TransactionXid(uniqueIdentity);
        this.status = TransactionStatus.TRYING;
        this.transactionType = transactionType;
    }

    public void enlistParticipant(Participant participant) {
        participants.add(participant);
    }


    public Xid getXid() {
        return xid.clone();
    }

    public TransactionStatus getStatus() {
        return status;
    }


    public List<Participant> getParticipants() {
        return participants;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void changeStatus(TransactionStatus status) {
        this.status = status;
    }

    /**
     * 执行Transaction的commit或rollback方法，会对应执行所有participant的commit或rollback方法
     */
    public void commit() {

        for (Participant participant : participants) {
            participant.commit();
        }
    }

    public void rollback() {
        for (Participant participant : participants) {
            participant.rollback();
        }
    }

    public int getRetriedCount() {
        return retriedCount;
    }

    public void addRetriedCount() {
        this.retriedCount++;
    }

    public void resetRetriedCount(int retriedCount) {
        this.retriedCount = retriedCount;
    }

    public Map<String, Object> getAttachments() {
        return attachments;
    }

    public long getVersion() {
        return version;
    }

    public void updateVersion() {
        this.version++;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date date) {
        this.lastUpdateTime = date;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void updateTime() {
        this.lastUpdateTime = new Date();
    }


}
